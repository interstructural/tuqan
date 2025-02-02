package pl.zenit.tuqan.gui;

import pl.zenit.tuqan.execution.GeneratorSetup;
import pl.zenit.tuqan.execution.parameters.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigPanel extends JPanel {

    private final Map<String, String> settings = new HashMap<>();

    private final int MARGIN = 10;
    private final int HALFMARGIN = 5;
    private final JButton compileButton = new JButton("compile (F9)");
    private final JButton generateButton = new JButton("generate (ctrl+F9)");
    private final JButton settingsButton = new JButton("settings");
    private final JButton mvnSpringServerButton = new JButton("mvn build spring server");
    private final JCheckBox debugComboBox = new JCheckBox("debug", false);
    private final JCheckBox multiThreadComboBox = new JCheckBox("multithread", true);
    private final JCheckBox autogenerateIdComboBox = new JCheckBox("autogenerate ID", true);
    private final JCheckBox cleanOutputComboBox = new JCheckBox("clean output", false);

    private Runnable eventCompile = ()-> {};
    private Runnable eventGenerate = ()-> {};
    private Runnable eventMvnSpringServer = ()-> {};

    public ConfigPanel() {
        setPreferredSize( new Dimension(280, 700) );

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add( Wrapper.margin(Wrapper.hpack(cleanOutputComboBox, autogenerateIdComboBox), HALFMARGIN) );
        panel.add( Wrapper.margin(Wrapper.hpack(debugComboBox, multiThreadComboBox), HALFMARGIN) );

        panel.add(Wrapper.margin( Wrapper.hpack(
                Wrapper.margin(compileButton, HALFMARGIN),
                Wrapper.margin(settingsButton, HALFMARGIN)
        ), HALFMARGIN));
        panel.add( Wrapper.margin(generateButton, 0, MARGIN, 0, MARGIN) );
        panel.add( Wrapper.margin(mvnSpringServerButton, MARGIN) );

        compileButton.addActionListener(l-> eventCompile.run());
        generateButton.addActionListener(l-> eventGenerate.run());
        mvnSpringServerButton.addActionListener(l-> eventMvnSpringServer.run());
        settingsButton.addActionListener(l->
            new SettingsNewDialog(null, true).showSelf(settings).forEach(settings::put)
        );
        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);

        setDefaultSettings();
    }

    public void setDefaultSettings() {
        settings.clear();
        settings.put(SpringCustomParams.APPLICATION_NAME.name(), "tuqan");
        settings.put(SpringCustomParams.APPLICATION_GROUP_ID.name(), "pl.zenit");
        settings.put(SpringCustomParams.SERVER_PROTOCOLHOST.name(), "http://localhost");
        settings.put(SpringCustomParams.SERVER_ROOT_PATH.name(), "endpoints");
        settings.put(SpringCustomParams.SERVER_PORT.name(), "8443");
        settings.put(SpringCustomParams.DB_URL.name(), "jdbc:mysql://localhost:3306/tuqanpublic");
        settings.put(SpringCustomParams.DB_USERNAME.name(), "tuqanpublic");
        settings.put(SpringCustomParams.DB_PASSWORD.name(), "tuqanpublic");
        settings.put(SpringCustomParams.HIBERNATE_DDL_AUTO.name(), "update");
        settings.put(SpringCustomParams.ENDPOINT_POSTMAN.name(), "/doc");
    }

    public void displayParams(TuqanExecutionParameters tep) {
        debugComboBox.setSelected(tep.getBasic().isDebug());
        multiThreadComboBox.setSelected(tep.getEnviourment().getThreadCount() > 1);
        autogenerateIdComboBox.setSelected(tep.getBasic().getAddId());
        cleanOutputComboBox.setSelected(tep.getBasic().getCleanOutput());
        settings.clear();
        tep.getCustomParams().getAll().forEach((k, v) -> settings.put(k, v));

    }
    public TuqanExecutionParameters gatherParams(File projectRoot, String code) {
        GeneratorSetup gp = new GeneratorSetup(
                true,
                true,
                true);
        TuqanBasicParameters basic = TuqanBasicParameters
                .builder()
                .indentSize(4)
                .addId(autogenerateIdComboBox.isSelected())
                .targetJavaVersion("17")
                .cleanOutput(cleanOutputComboBox.isSelected())
                .skipTests(false)
                .debug(debugComboBox.isSelected())
                .build();

        TuqanEnvironmentalParameters environment = TuqanEnvironmentalParameters
                .builder()
                .code(code)
                .outputRootDir(projectRoot)
                .threadCount(multiThreadComboBox.isSelected() ? 8 : 1)
                .generatorSetup(gp)
                .useCustomAccessControl(true)
                .build();

        TuqanCustomParameters tcp = new TuqanCustomParameters();
        settings.forEach((k, v)-> tcp.set(k, v));

        return new TuqanExecutionParameters(basic,environment, tcp);
    }

    public void setEventCompile(Runnable eventCompile) {
        this.eventCompile = eventCompile;
    }

    public void setEventGenerate(Runnable eventGenerate) {
        this.eventGenerate = eventGenerate;
    }

    public void setEventMvnSpringServer(Runnable eventMvnSpringServer) {
        this.eventMvnSpringServer = eventMvnSpringServer;
    }

    public void setBusy(boolean busy) {
        settingsButton.setEnabled(!busy);
        generateButton.setEnabled(!busy);
        compileButton.setEnabled(!busy);
        mvnSpringServerButton.setEnabled(!busy);
    }

}
