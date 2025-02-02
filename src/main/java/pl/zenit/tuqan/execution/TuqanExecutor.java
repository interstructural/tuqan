package pl.zenit.tuqan.execution;

import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.lang.TuqanContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import pl.zenit.tuqan.execution.TestSupplier.RunnableAsTest;
import pl.zenit.tuqan.generators.GeneratorGroup;
import pl.zenit.tuqan.generators.OutputGenerator;
import pl.zenit.tuqan.lang.expression.TextExpression;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.fuckup.ParsingFuckup;
import pl.zenit.tuqan.lang.statement.StatementParser;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.ProcessingFuckup;
import pl.zenit.tuqan.util.ActionSynchronizer;
import pl.zenit.tuqan.util.StackTracePrinter;
import pl.zenit.tuqan.util.Threads;

public class TuqanExecutor {

    private final Consumer<String> logger;

    private TuqanExecutionParameters params;

    private boolean debugLog = false;

    public TuqanExecutor(Consumer<String> logger) {
        this.logger = logger;
    }

    public TuqanExecutor setDebugLog(final boolean value) {
        this.debugLog = value;
        return this;
    }
    
    public synchronized void compile(TuqanExecutionParameters params)
          throws ParsingFuckup, 
                ProcessingFuckup, 
                IOException, 
                InterruptedException,
                TestFuckup {
        this.params = params;
        long timestamp = System.currentTimeMillis();
        log("dry running compilation");
        
        List<TextExpression> expressions = parseText(params.getEnviourment().getCode());
        log("" + expressions.size() + " statements found");

        TuqanContext context = new TuqanContext();
        TuqanContext.setCurrent(context);

        processCode(expressions, context);
        printParsingResult(context);
        if (!params.getBasic().getSkipTests())
            runTests(context, false);
        log("context successfully processed");        

        long time = System.currentTimeMillis() - timestamp;
        log("compilation finished in " + time + "ms");
    }
        
    public synchronized void execute(TuqanExecutionParameters params) 
        throws ParsingFuckup, 
                ProcessingFuckup, 
                GenerationFuckup, 
                IOException, 
                InterruptedException,
                TestFuckup {
        this.params = params;
        long timestamp = System.currentTimeMillis();

        log("starting tuqan at " + params.getEnviourment().getOutputRootDir().getAbsolutePath());
        createOutputDir(params);
        clearOutputDir(params);
        createCodeFile();

        List<TextExpression> expressions = parseText(params.getEnviourment().getCode());
        log("" + expressions.size() + " statements found");

        TuqanContext context = new TuqanContext();
        TuqanContext.setCurrent(context);

        processCode(expressions, context);        
        printParsingResult(context);        
        if (!params.getBasic().getSkipTests())            
            runTests(context, params.getEnviourment().getGeneratorSetup().getGroup("spring").isPresent());
        
        log("voiding virtual entities");
        context.getAllScopes().removeIf(p-> p.isVirtual());
        printParsingResult(context);

        log("context successfully processed");
        
        List<GeneratorRunner> runners = new ArrayList<>();
        Supplier<List<OutputGenerator>> chosenGeneratorsSupplier = () -> getNewScopeGenerators(params.getEnviourment().getGeneratorSetup().asList());
        runners.addAll(queueGenerators(context.getAllScopes(), chosenGeneratorsSupplier));
        Supplier<List<OutputGenerator>> chosenEnumGeneratorsSupplier = () -> getNewEnumGenerators(params.getEnviourment().getGeneratorSetup().asList());
        runners.addAll(queueGenerators(context.getEnums(), chosenEnumGeneratorsSupplier));
        log("queued " + runners.size() + " generators");

        log("running group initializers");
        params.getEnviourment().getGeneratorSetup().asList().forEach(group -> group.getInitializer(params).run());

        int threadCount = params.getEnviourment().getThreadCount();
        log("generating output on " + threadCount + " threads");
        generateOutput(runners, threadCount);

        long time = System.currentTimeMillis() - timestamp;
        log("generation finished in " + time + "ms");
    }

    private void createOutputDir(TuqanExecutionParameters params) throws IOException {
        File targetDir = params.getEnviourment().getOutputRootDir();
   
        if (!targetDir.isDirectory())
        Files.createDirectory(targetDir.toPath());
    }

    private void clearOutputDir(TuqanExecutionParameters params) throws IOException {
        if (params.getBasic().getCleanOutput()) {            
            log("cleaning output directory");
            File root = params.getEnviourment().getOutputRootDir();
            Files.walk(root.toPath())
                .map(Path::toFile)
                .filter(p-> !p.equals("."))
                .filter(p-> !p.equals(".."))
                .filter(p-> !p.getName().equals("code.tuqan"))
                .filter(p-> !p.getName().equals("project.cfg"))
                .filter(p-> !p.getAbsolutePath().equalsIgnoreCase(root.getAbsolutePath()))
                .sorted((o1, o2)-> o1.compareTo(o2))
                .forEach(File::delete);
        }    
    }

    private void createCodeFile() throws IOException, InterruptedException {
        log("creating code page");        
        File codefile = new File(params.getEnviourment().getOutputRootDir(), "code.tuqan");
        if ( codefile.isFile()) {
            Thread.sleep(1);
            Files.delete(codefile.toPath());
        }
        else Files.createFile(codefile.toPath());
        
        Files.write(codefile.toPath(), params.getEnviourment().getCode().getBytes(), StandardOpenOption.CREATE);
    }

    private List<TextExpression> parseText(String text) throws ParsingFuckup {
        
        text = removeComments(text);
        List<String> sl = new ArrayList<>();
        sl.addAll(Arrays.asList(text.split(";")));
        sl.removeIf(s -> s.isEmpty());
        sl = sl.parallelStream().map(m -> m.replace("\r", "\n").replace("\n", " ").trim()).collect(Collectors.toList());
        sl.removeIf(s -> s.isEmpty());

        List<TextExpression> list = new ArrayList<>();
        for ( int i = 0; i < sl.size(); ++i ) {
            list.add(new TextExpression(sl.get(i)));
        }
        return list;
    }
    
    private String removeComments(String text) throws ParsingFuckup {
        final String opening = "/*";
        final String closing = "*/";
        
        int start = -1;
       
        
        while ((start = text.indexOf(opening)) > -1) {
            int end = text.indexOf(closing);
            if (end == -1)
                throw new ParsingFuckup("unexpected end of file");
            if (end < start) 
                throw new ParsingFuckup(closing + " was not expected by the parser");
            
            String before = text.substring(0, start);
            String after = text.substring(end + closing.length());
            text = before + after;
        }
        if (text.indexOf(closing) != -1)
            throw new ParsingFuckup(closing + " was not expected by the parser");
        return text;
    }

    private void processCode(List<TextExpression> expressions, TuqanContext context)
          throws ProcessingFuckup, ParsingFuckup {
        for (TextExpression expression : expressions) {
            TuqanObject object = new StatementParser(params, context).parse(expression);
            context.register(object);
        }
    }

    private void printParsingResult(TuqanContext context) {
        int e = context.getEnums().size();
        int scopeCount = context.getDefaultScopes().size();
        int ledgerCount = context.getLedgers().size();
        int mirageCount = context.getMirages().size();
        long virtualCount = context.getAllScopes().stream().filter(p-> p.isVirtual()).count();
        int all = scopeCount + ledgerCount;
        
        List<TuqanScopeclass> mirageLedgerList = context.getLedgers().stream()
            .filter(p-> p.isMirage()).collect(Collectors.toList());
        
        
        if ( debugLog ) {
            log(all + " objects parsed:");
            log(e + " enums (" + context.getEnums().stream()
                  .map(m -> m.getName())
                  .collect(Collectors.joining(", ")) + ")");
            log(scopeCount + " scopes (" + context.getDefaultScopes().stream()
                  .map(m -> m.getName())
                  .collect(Collectors.joining(", ")) + ")");
            log(ledgerCount + " ledgers (" + context.getLedgers().stream()                  
                  .map(m -> m.getName())
                  .collect(Collectors.joining(", ")) + ")");
            log(mirageCount + " mirages (" + context.getMirages().stream()
                    .map(m -> m.getName())
                    .collect(Collectors.joining(", ")) + ")");
            log(virtualCount + " virtual entities (" + context.getAllScopes().stream()
                  .filter(p-> p.isVirtual())
                  .map(m -> m.getName())
                  .collect(Collectors.joining(", ")) + ")");

            mirageLedgerList.forEach(l-> log("WARNING: mirage ledger " + l.getName()));
        }
        else {
            log(all + " objects parsed: ("
                + e + " enums, "
                + scopeCount + " scopes, "
                + ledgerCount + " ledgers, "
                + mirageCount + " mirages)");

            if (mirageLedgerList.size() > 0)
                log(mirageLedgerList.size() +  " mirage ledgers found. are you sure?");
        }
        
    }
    
    private List<GeneratorRunner> queueGenerators(List<? extends TuqanObject> objects,
                        Supplier<List<OutputGenerator>> generatorListSupplier) {
        List<GeneratorRunner> list = new ArrayList<>();
        for (TuqanObject object : objects) {
            List<OutputGenerator> generators = generatorListSupplier.get();
            for (OutputGenerator generator : generators) {
                list.add(new GeneratorRunner(generator, object));
            }
        }
        return list;
    }

    private List<OutputGenerator> getNewScopeGenerators(List<GeneratorGroup> groups) {
        List<OutputGenerator> list = new ArrayList<>();
        groups.forEach(group -> list.addAll(group.getGenerators(params)));
        return list;
    }

    private List<OutputGenerator> getNewEnumGenerators(List<GeneratorGroup> groups) {
        List<OutputGenerator> list = new ArrayList<>();
        groups.forEach(group -> list.addAll(group.getEnumGenerators(params)));
        return list;
    }

    private void generateOutput(List<GeneratorRunner> runners, int threadCount) throws GenerationFuckup {
        firstFuckup = null;

        List<List<GeneratorRunner>> stacks = new ArrayList<>();
        for ( int i = 0; i < threadCount; ++i ) {
            stacks.add(new ArrayList<>());
        }

        for ( int i = 0; i < runners.size(); ++i ) {
            stacks.get(i % stacks.size()).add(runners.get(i));
        }

        final List<Thread> threads = new ArrayList<>();
        for ( int i = 0; i < stacks.size(); ++i ) {
            int finali = i;
            final List<GeneratorRunner> actions = stacks.get(i);
            Thread t = Threads.lazy(() -> {
                for ( int j = 0; j < actions.size(); ++j ) 
                        try {
                    if ( firstFuckup == null ) {
                        actions.get(j).run();
                    }
                }
                catch (GenerationFuckup e) {
                    firstFuckup = e;
                    break;
                }
                catch (Exception e) {
                    int finalj = j;
                    TuqanExecutor.this.log("exception at thread " + finali + ", item " + j);
                    if (debugLog) {
                        e.printStackTrace();
                        new StackTracePrinter(TuqanExecutor.this::log).printStackTrace(e);
                    }
                    break;
                }
            });
            t.setName("generator thread " + i);
            threads.add(t);
        }
        ActionSynchronizer as = new ActionSynchronizer();
        threads.stream()
              .map(t -> (Runnable) () -> t.run())
              .forEachOrdered(as.actions::add);
        as.run();
        if ( firstFuckup != null ) {
            throw firstFuckup;
        }
    }

    private volatile GenerationFuckup firstFuckup = null;

    private void log(String s) {
        logger.accept(s);
    }

    private void runTests(TuqanContext context, boolean includeRestParamTests) throws TestFuckup {
        List<RunnableAsTest> list = new ArrayList<>();
        if (includeRestParamTests) {
            list.addAll(new RestAddressUtils(params).getTests());
        }
        list.addAll(new ContextIntegrityTests(context).getTests());

        logger.accept("running " + list.size() + " tests");
        for (RunnableAsTest item : list) 
        item.runTest();
    }

} //end of class TuqanExecutor
