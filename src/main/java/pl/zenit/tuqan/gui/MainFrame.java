package pl.zenit.tuqan.gui;

import javax.swing.*;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rtextarea.RTextScrollPane;
import pl.zenit.tuqan.Main;
import pl.zenit.tuqan.execution.TestFuckup;
import pl.zenit.tuqan.execution.parameters.ConfigFileManager;
import pl.zenit.tuqan.execution.parameters.ConfigFileToParams;
import pl.zenit.tuqan.batch.ProcessPrinter;
import pl.zenit.tuqan.codeeditor.LanguageManager;
import pl.zenit.tuqan.execution.TuqanExecutor;
import pl.zenit.tuqan.execution.parameters.TuqanExecutionParameters;
import pl.zenit.tuqan.generators.PackageLocationResolver;
import pl.zenit.tuqan.lang.fuckup.GenerationFuckup;
import pl.zenit.tuqan.lang.fuckup.ParsingFuckup;
import pl.zenit.tuqan.lang.fuckup.ProcessingFuckup;
import pl.zenit.tuqan.util.Coalescer;
import pl.zenit.tuqan.util.Threads;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

    private static final String codeFile = "code.tuqan";
    private static final String projectFile = "project.cfg";
    private static final int BORDER = 10;

    private final ConsolePanel consolePanel = new ConsolePanel();
    private final RSyntaxTextArea codeEditor = new RSyntaxTextArea();
    private final ConfigPanel configPanel = new ConfigPanel();
    private final MainMenu mainMenu = new MainMenu();
    private File projectRoot = new File("");

    public MainFrame() {
        initComponents();
        initCodeArea();
        linkActions();
        pack();
        setLocationRelativeTo(null);
        Threads.lazy(this::newProject).start();
    }
    private void initComponents() {
        String title = Main.getAppInfo().getName() + " " + Main.getAppInfo().getVersion();
        setTitle(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 700));
        setLayout(new BorderLayout());
        this.setJMenuBar(mainMenu);

        codeEditor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        consolePanel.setInputConsumer(this::runConsole);
        consolePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        consolePanel.setPreferredSize(new Dimension(1000, 100));
        configPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        RTextScrollPane codePanelScrollPane = new RTextScrollPane(codeEditor);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codePanelScrollPane, consolePanel);
        splitPane.setResizeWeight(0.9);

        add(Wrapper.margin(new JLabel(title), BORDER, BORDER, 0, 0), BorderLayout.NORTH);
        add(Wrapper.margin(configPanel, BORDER, 0, BORDER, BORDER), BorderLayout.EAST);
        add(Wrapper.margin(splitPane, BORDER), BorderLayout.CENTER);
    }
    private void initCodeArea() {
        codeEditor.addKeyListener(new CodeEditorKeyAdapter());
        LanguageManager.registerTuqanSyntax();
        LanguageManager.setTuqanSyntax(codeEditor);
        Font font = new Font("Courier New", Font.PLAIN, 18);
        codeEditor.setFont(font);
        LanguageManager.setTuqanColoring(codeEditor, font);
        LanguageManager.prepareEditorForTuqanCode(codeEditor);
        codeEditor.getFoldManager().setCodeFoldingEnabled(true);
        codeEditor.setCodeFoldingEnabled(true);
        consolePanel.setIncentText(projectRoot.getAbsolutePath() + " > ");
    }
    private void linkActions() {
        mainMenu.setEventNew(this::newProject);
        mainMenu.setEventOpen(this::openProject);
        mainMenu.setEventSave(this::saveProject);
        mainMenu.setEventExit(()-> System.exit(1));

        configPanel.setEventCompile(this::compile);
        configPanel.setEventGenerate(this::generate);
        configPanel.setEventMvnSpringServer(this::mvnSpringServer);
    }
    //--------------------------------------------------------------------------------
    private void runConsole(String cmd) {
        String[] cmds = new String[]{"cmd", "/c", cmd};
        try {
            consolePanel.startPrintingOutput();
            consolePanel.printOutput("> " + cmd);
            new ProcessPrinter(consolePanel::printOutput).run(projectRoot, cmds);
        }
        catch (Exception ex) {
            consolePanel.printThrowable(ex, configPanel.gatherParams(projectRoot, "").getBasic().isDebug());
        }
        finally {
            consolePanel.stopPrintingOutput();
        }
    }
    private class CodeEditorKeyAdapter extends KeyAdapter {
        @Override public void keyPressed(KeyEvent evt) {
            if ( evt.getKeyCode() == KeyEvent.VK_F9 ) {
                if (evt.isControlDown()) {
                    generate();
                }
                else {
                    compile();
                }
            }
            else if ( evt.getKeyCode() == KeyEvent.VK_F1 ) {
                codeEditor.setText("CREATE SCOPE Abc(int aaa, bool bbb, String ccc); \nCREATE ENUM MyEnum(VAL1, VAL2); \nCREATE SCOPE Scope(int var1)");
            }
            else if ( evt.getKeyCode() == KeyEvent.VK_ADD ) {
                if (evt.isControlDown())
                    if (evt.isShiftDown())
                        fireFoldUnfold(true);
            }
            else if ( evt.getKeyCode() == KeyEvent.VK_SUBTRACT ) {
                if (evt.isControlDown())
                    if (evt.isShiftDown())
                        fireFoldCollapse(true);
            }
            else if ( evt.getKeyCode() == KeyEvent.VK_S ) {
                if (evt.isControlDown()) {
                    saveProject();
                    consolePanel.printOutput("[project saved]");
                }
            }
            else if ( evt.getKeyCode() == KeyEvent.VK_O ) {
                if (evt.isControlDown()) {
                    openProject();
                }
            }
        }
    }
    //--------------------------------------------------------------------------------
    private void newProject() {
        codeEditor.setText("");
        loadProject( new File(Main.getAppInfo().getEntryPoint(), "output") );
    }
    private void saveProject() {
        saveProject(this.projectRoot);
    }
    private void openProject() {
        JFrame frame = new JFrame();
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setSelectedFile(Main.getAppInfo().getEntryPoint());
        chooser.setCurrentDirectory(Main.getAppInfo().getEntryPoint());
        int returnValue = chooser.showOpenDialog(frame);
        if ( returnValue == JFileChooser.APPROVE_OPTION ) {
            loadProject(chooser.getSelectedFile());
        }
    }
    private void fireFoldUnfold(boolean all) {
        if (all) {
            for (int i = 0; i < codeEditor.getFoldManager().getFoldCount() ; ++i )
                codeEditor.getFoldManager().getFold(i).setCollapsed(false);
        }
        else {
            int caretPosition = codeEditor.getCaretPosition();
            Fold fold = codeEditor.getFoldManager().getFoldForLine(caretPosition);
            if ( fold != null ) fold.setCollapsed(false);
        }
    }
    private void fireFoldCollapse(boolean all) {
        if (all) {
            for (int i = 0; i < codeEditor.getFoldManager().getFoldCount() ; ++i )
                codeEditor.getFoldManager().getFold(i).setCollapsed(true);
        }
        else {
            int caretPosition = codeEditor.getCaretPosition();
            Fold fold = codeEditor.getFoldManager().getFoldForLine(caretPosition);
            if ( fold != null ) fold.setCollapsed(true);
        }
    }
    //--------------------------------------------------------------------------------
    private void loadProject(File projectRoot) {
        this.projectRoot = projectRoot;
        consolePanel.setIncentText(projectRoot.getAbsolutePath() + " > ");
        try {
            if (!projectRoot.isDirectory()) {
                Files.createDirectory(projectRoot.toPath());
            }
            loadCode(projectRoot);
            loadConfig(projectRoot);
            consolePanel.printOutput("[loaded "+ projectRoot.getAbsolutePath() +"]");
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getClass().getSimpleName() + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    private void loadCode(File projectRoot) throws IOException {
        File f = new File(projectRoot, codeFile);
        if ( !f.isFile() ) {
            return;
        }
        codeEditor.setText(
            Files.readAllLines(f.toPath(), StandardCharsets.UTF_8)
                .stream()
                .collect(Collectors.joining("\n"))
        );
    }
    private void loadConfig(File projectRoot) throws TestFuckup, GenerationFuckup, IOException, ParsingFuckup, ProcessingFuckup, InterruptedException {
        File f = new File(projectRoot, projectFile);
        if ( !f.isFile() ) {
            return;
        }
        ConfigFileManager cfm = new ConfigFileManager(f);
        cfm.loadConfig();
        Map<String, String> map = new HashMap<>();

        TuqanExecutionParameters tep = new ConfigFileToParams().fileToParams("", cfm);
        configPanel.displayParams(tep);
    }

    private void saveProject(File projectRoot) {
        saveCode(projectRoot);
        saveConfig(projectRoot);
    }
    private void saveCode(File projectRoot) {
        File f = new File(projectRoot, codeFile);
        if ( f.isFile() ) {
            f.delete();
        }
        List<String> sl = new ArrayList<>();
        try {
            String code = codeEditor.getText();
            Files.write(f.toPath(), code.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        }
        catch (Exception e) {
            codeEditor.setText(e.getMessage());
        }
    }
    private void saveConfig(File projectRoot) {
        ConfigFileManager cfm = new ConfigFileManager(new File(projectRoot, projectFile));
        new ConfigFileToParams().fillWithParams(cfm, configPanel.gatherParams(projectRoot, ""));
        cfm.flushConfig();
    }
    //--------------------------------------------------------------------------------
    private void compile() {
        TuqanExecutionParameters params = configPanel.gatherParams(projectRoot, codeEditor.getText());
        configPanel.setBusy(true);
        Threads.lazy(() -> {
            consolePanel.clear();
            Coalescer.ignore(() -> Coalescer.handle(()-> {
                    new TuqanExecutor(consolePanel::printOutput)
                        .setDebugLog(params.getBasic().isDebug())
                        .compile(params);
                    },
                    th-> {
                        consolePanel.printThrowable(th, params.getBasic().isDebug());
                    }));
            configPanel.setBusy(false);
        }).start();
    }
    private void generate() {
        TuqanExecutionParameters params = configPanel.gatherParams(projectRoot, codeEditor.getText());
        configPanel.setBusy(true);
        Threads.lazy(() -> {
            consolePanel.clear();
            Coalescer.ignore(() -> Coalescer.handle(()-> {
                        new TuqanExecutor(consolePanel::printOutput)
                                .setDebugLog(params.getBasic().isDebug())
                                .execute(params);
                    },
                    th-> {
                        consolePanel.printThrowable(th, params.getBasic().isDebug());
                    }));
            configPanel.setBusy(false);
        }).start();
    }

    private void mvnSpringServer() {
        Threads.lazy(()-> {
            TuqanExecutionParameters tep = configPanel.gatherParams(projectRoot, "");
            File serverDir = new PackageLocationResolver(tep).getSpringServerProjectRoot(projectRoot);
            Path p = projectRoot.toPath().relativize(serverDir.toPath());
            runConsole("mvn clean install -f " + p.toString());
        }).start();
    }

}
