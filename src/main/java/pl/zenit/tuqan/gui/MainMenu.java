package pl.zenit.tuqan.gui;

import javax.swing.*;

public class MainMenu extends JMenuBar {

    private Runnable eventNew = ()->{};
    private Runnable eventOpen = ()->{};
    private Runnable eventSave = ()->{};
    private Runnable eventExit = ()->{};

    public MainMenu() {
        JMenu fileMenu = new JMenu("File");
        add(fileMenu);
        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(e -> eventNew.run());
        fileMenu.add(newItem);

        JMenuItem openItem = new JMenuItem("Open (ctrl+O)");
        openItem.addActionListener(e -> eventOpen.run());
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Save (ctrl+S)");
        saveItem.addActionListener(e -> eventSave.run());
        fileMenu.add(saveItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> eventExit.run());
        fileMenu.add(exitItem);
    }

    public void setEventNew(Runnable eventNew) {
        this.eventNew = eventNew;
    }

    public void setEventOpen(Runnable eventOpen) {
        this.eventOpen = eventOpen;
    }

    public void setEventSave(Runnable eventSave) {
        this.eventSave = eventSave;
    }

    public void setEventExit(Runnable eventExit) {
        this.eventExit = eventExit;
    }
}
