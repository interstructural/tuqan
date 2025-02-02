package pl.zenit.tuqan.gui;

import javax.swing.*;
import java.awt.*;

public class Wrapper {

    public static JPanel margin(JComponent comp, int margin) {
        return margin(comp, margin, margin, margin, margin);
    }

    public static JPanel margin(JComponent comp, int top, int left, int bottom, int right) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(comp, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        return panel;
    }

    public static JPanel addCaption(Component comp, String caption) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(caption);
        panel.add(label, BorderLayout.NORTH);
        panel.add(comp, BorderLayout.CENTER);
        return panel;
    }

    public static JPanel hpack(Component comp, Component comp2) {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(comp);
        panel.add(comp2);
        return panel;
    }

    public static JPanel vpack(Component comp, Component comp2) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(comp);
        panel.add(comp2);
        return panel;
    }

}
