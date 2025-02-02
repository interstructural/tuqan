package pl.zenit.tuqan.gui;

import pl.zenit.tuqan.execution.parameters.SpringCustomParams;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsNewDialog extends javax.swing.JDialog {

    private final List<String> allSettingsNames = new ArrayList<>();

    private final Map<String, Setting> settings = new TreeMap<>();
    private final JButton jButtonOk = new JButton("OK");
    private final JButton jButtonCancel = new JButton("Cancel");
    private volatile boolean canceled;

    private class Setting {
        private final String caption;
        private final String paramName;
        private final JTextField textField;

        public Setting(String caption, String paramName, JTextField textField) {
            this.caption = caption;
            this.paramName = paramName;
            this.textField = textField;
        }

        public String getCaption() {
            return caption;
        }

        public String getParamName() {
            return paramName;
        }

        public JTextField getTextField() {
            return textField;
        }
    }

    public SettingsNewDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    public void initComponents() {
        jButtonOk.addActionListener(a-> { canceled = false; dispose();} );
        jButtonCancel.addActionListener(a-> dispose());

        allSettingsNames.addAll(Arrays.asList(SpringCustomParams.values())
            .stream()
            .map(m-> m.name())
            .collect(Collectors.toList()));

        //allSettingsNames.add(DeprecatedCustomParams.JAVA_DEFAULT_ROOT_PACKAGE);

        allSettingsNames.forEach(name-> settings.put(name, new Setting(name, name, new JTextField()) ));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        int margin = 5;

        settings.forEach((k, v)-> {
            panel.add(Wrapper.margin(
                    Wrapper.addCaption(v.textField, v.caption)
                    , margin));
        });

        panel.add(Wrapper.margin(Wrapper.hpack(jButtonOk, jButtonCancel), margin));
        this.setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
    }

    public Map<String, String> showSelf(Map<String, String> initParams) {
        initParams.forEach((k, v)-> {
            Setting setting = settings.get(k);
            if (setting != null)
                setting.getTextField().setText(v);
        });

        canceled = true;
        setVisible(true);
        return canceled ? new HashMap<>() : getValues();
    }

    private Map<String, String> getValues() {
        Map<String, String> map = new HashMap<>();
        settings.forEach((k, v)-> map.put(k, v.getTextField().getText()));
        return map;
    }
   
}