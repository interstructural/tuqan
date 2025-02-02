package pl.zenit.tuqan.generators.spring.client;

import java.io.File;
import pl.zenit.tuqan.util.Upcaster;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.FilesaveWrapper;
import pl.zenit.tuqan.generators.IndentedStringList;
import pl.zenit.tuqan.generators.ImportOrganizer;
import pl.zenit.tuqan.generators.dto.java.JavaOutputGenerator;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.lang.struct.TuqanObject;
import pl.zenit.tuqan.lang.struct.TuqanScopeclass;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.generators.literals.java.JavaScope;

public class JavaSpringClientDaoOutputGenerator extends JavaOutputGenerator {

    public JavaSpringClientDaoOutputGenerator(TuqanExecutionParameters params) {
        super(params);
    }

    @Override
    public void createOutput(TuqanObject data) throws GenerationFuckup {
        if ( data instanceof TuqanScopeclass ) {
            output(new JavaScope(new Upcaster(data).asInferred()));
        }
        else {
            throw new GenerationFuckup("unknown operation");
        }

    }

    private void output(JavaScope scope) throws GenerationFuckup {
        addPackageDeclaration(text, scope);
        addImports(text, scope);
        text.add("");
        text.add("public class " + scope.spring().daoName() + " {");
        text.pushIndent();
        text.add("");
        addFields(text, scope);

        addFunctionCount(text, scope);
        addFunctionGetAll(text, scope);
        addFunctionGet(text, scope);
        addFunctionGetLastest(text, scope);
        
        boolean ledger = scope.asTuqanObject().isLedger();
        boolean mirage = scope.asTuqanObject().isMirage();

        if (!mirage) {
            addFunctionAddAll(text, scope);
            addFunctionAdd(text, scope);
        }

        if (!ledger && !mirage) {
            addFunctionUpdateAll(text, scope);
            addFunctionUpdate(text, scope);
            addFunctionDeleteAll(text, scope);
            addFunctionDelete(text, scope);
        }
        
        text.popIndent();
        text.add("} // end of class");

        File filename = getFileFor(scope.spring().daoName() + ".java");
        FilesaveWrapper.saveToFile(text.getAsStringList(), filename.getAbsolutePath());
    }

    private void addPackageDeclaration(IndentedStringList text, JavaScope scope) {
        String pkg = new PackageLocationResolver(params).getRootPacakge() + ".dao";
        text.add("package " + pkg + ";");
        text.add("");
    }

    private void addImports(IndentedStringList text, JavaScope scope) {
        new ImportOrganizer().getCommonImports().forEach(i -> text.add("import " + i + ";"));
        new ImportOrganizer().getSpringClientImports().forEach(i -> text.add("import " + i + ";"));
        text.add("");
    }

    private void addFields(IndentedStringList text, JavaScope scope) {
        text.add("private final String baseUrl = "
            + "Endpoints.main + \"/"
            + scope.spring().baseRequestUrlName()
            + "\";");
        text.add("private final RestTemplate restTemplate = new RestTemplate();");
        text.add("");
    }

    private void addFunctionCount(IndentedStringList text, JavaScope scope) {
        text.add("public long " + scope.rest().methodCount() + "() {");
        text.pushIndent();
        text.add("ResponseEntity<Long> response = restTemplate.exchange(");
        text.pushIndent();
        text.add("baseUrl + \"/count\",");
        text.add("HttpMethod.GET,");
        text.add("null,");
        text.add("Long.class");
        text.popIndent();
        text.add(");");
        text.add("return response.getBody();");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addFunctionGetAll(IndentedStringList text, JavaScope scope) {
        text.add("public List<" + scope.dtoName() + "> " + scope.rest().methodGetAll() + "() {");
        text.pushIndent();
        text.add("ResponseEntity<List<" + scope.dtoName() + ">> response = restTemplate.exchange(");
        text.pushIndent();
        text.add("baseUrl,");
        text.add("HttpMethod.GET,");
        text.add("null,");
        text.add("new ParameterizedTypeReference<List<" + scope.dtoName() + ">>() {}");
        text.popIndent();
        text.add(");");
        text.add("return response.getBody();");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addFunctionGet(IndentedStringList text, JavaScope scope) {
        text.add("public " + scope.dtoName() + " " + scope.rest().methodGet() + "(final int id) {");
        text.pushIndent();
        text.add("ResponseEntity<" + scope.dtoName() + "> response = restTemplate.exchange(");
        text.pushIndent();
        text.add("baseUrl + \"/\" + id,");
        text.add("HttpMethod.GET,");
        text.add("null,");
        text.add(scope.dtoName() + ".class");
        text.popIndent();
        text.add(");");
        text.add("return response.getBody();");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addFunctionGetLastest(IndentedStringList text, JavaScope scope) {
        text.add("public " + scope.dtoName() + " " + scope.rest().methodGetLatest() + "() {");
        text.pushIndent();
        text.add("ResponseEntity<" + scope.dtoName() + "> response = restTemplate.exchange(");
        text.pushIndent();
        text.add("baseUrl + \"/latest\",");
        text.add("HttpMethod.GET,");
        text.add("null,");
        text.add(scope.dtoName() + ".class");
        text.popIndent();
        text.add(");");
        text.add("return response.getBody();");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addFunctionAdd(IndentedStringList text, JavaScope scope) {
        text.add("public " + scope.dtoName() + " " + scope.rest().methodAdd() + "(final " + scope.dtoName() + " item) {");
        text.pushIndent();
        text.add("HttpEntity<" + scope.dtoName() + "> request = new HttpEntity<>(item);");
        text.add("ResponseEntity<" + scope.dtoName() + "> response = restTemplate.exchange(");
        text.pushIndent();
        text.add("baseUrl,");
        text.add("HttpMethod.POST,");
        text.add("request,");
        text.add(scope.dtoName() + ".class");
        text.popIndent();
        text.add(");");
        text.add("return response.getBody();");
        text.popIndent();
        text.add("}");
        text.add("");
    }
    
    private void addFunctionUpdate(IndentedStringList text, JavaScope scope) {
        text.add("public " + scope.dtoName() + " " + scope.rest().methodUpdate() + "(final int id, final " + scope.dtoName() + " item) {");
        text.pushIndent();
        text.add("HttpEntity<" + scope.dtoName() + "> request = new HttpEntity<>(item);");
        text.add("ResponseEntity<" + scope.dtoName() + "> response = restTemplate.exchange(");
        text.pushIndent();
        text.add("baseUrl + \"/\" + id,");
        text.add("HttpMethod.PUT,");
        text.add("request,");
        text.add(scope.dtoName() + ".class");
        text.popIndent();
        text.add(");");
        text.add("return response.getBody();");
        text.popIndent();
        text.add("}");
        text.add("");
    }

    private void addFunctionDelete(IndentedStringList text, JavaScope scope) {
        text.add("public void " + scope.rest().methodDelete() + "(final int id) {");
        text.pushIndent();
        text.add("restTemplate.exchange(");
        text.pushIndent();
        text.add("baseUrl + \"/\" + id,");
        text.add("HttpMethod.DELETE,");
        text.add("null,");
        text.add("Void.class");
        text.popIndent();
        text.add(");");
        text.popIndent();
        text.add("}");
        text.add("");
    }
    
    private void addFunctionAddAll(IndentedStringList text, JavaScope scope) {
        text.add("public List<" + scope.dtoName() + "> " + scope.rest().methodAddAll() + "(final List<" + scope.dtoName() + "> items) {");
        text.pushIndent();
        text.add("HttpEntity<List<" + scope.dtoName() + ">> request = new HttpEntity<>(items);");
        text.add("ResponseEntity<List<" + scope.dtoName() + ">> response = restTemplate.exchange(");
        text.pushIndent();
        text.add("baseUrl + \"/all\",");
        text.add("HttpMethod.POST,");
        text.add("request,");
        text.add("new ParameterizedTypeReference<List<" + scope.dtoName() + ">>() {}");
        text.popIndent();
        text.add(");");
        text.add("return response.getBody();");
        text.popIndent();
        text.add("}");
        text.add("");

    }
    
    private void addFunctionUpdateAll(IndentedStringList text, JavaScope scope) {
        text.add("public List<" + scope.dtoName() + "> " + scope.rest().methodUpdateAll() + "(final List<" + scope.dtoName() + "> items) {");
        text.pushIndent();
        text.add("HttpEntity<List<" + scope.dtoName() + ">> request = new HttpEntity<>(items);");
        text.add("ResponseEntity<List<" + scope.dtoName() + ">> response = restTemplate.exchange(");
        text.pushIndent();
        text.add("baseUrl + \"/all\",");
        text.add("HttpMethod.PUT,");
        text.add("request,");
        text.add("new ParameterizedTypeReference<List<" + scope.dtoName() + ">>() {}");
        text.popIndent();
        text.add(");");
        text.add("return response.getBody();");
        text.popIndent();
        text.add("}");
        text.add("");
    }    
    
    private void addFunctionDeleteAll(IndentedStringList text, JavaScope scope) {
        text.add("public void " + scope.rest().methodDeleteAll() + "(final List<Integer> ids) {");
        text.pushIndent();
        text.add("HttpEntity<List<Integer>> request = new HttpEntity<>(ids);");
        text.add("restTemplate.exchange(");
        text.pushIndent();
        text.add("baseUrl + \"/all\",");
        text.add("HttpMethod.DELETE,");
        text.add("request,");
        text.add("Void.class");
        text.popIndent();
        text.add(");");
        text.popIndent();
        text.add("}");
        text.add("");
    }

   @Override
    protected File getFileFor(String filename) {
        File overallRoot = params.getEnviourment().getOutputRootDir();
        PackageLocationResolver plr = new PackageLocationResolver(params);
        File clientDir = plr.getSpringDesktopClientProjectRoot(overallRoot);
        clientDir = plr.getSourceCodeAppRootDir(clientDir);
        clientDir = new File(clientDir, "dao");
        return new File(clientDir, filename);
    }

} //end of class
