package pl.zenit.tuqan.gui;

import pl.zenit.tuqan.util.StackTracePrinter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ConsolePanel extends JPanel {

    private JTextArea console;
    private JTextField input;
    private JLabel incent = new JLabel();
    private AtomicBoolean printingNow = new AtomicBoolean(false);
    private Consumer<String> inputConsumer = s->{};

    public ConsolePanel() {
        setLayout(new BorderLayout());

        console = new JTextArea();
        console.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        input = new JTextField();
        input.addKeyListener(new InputKeyAdapter());

        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(Wrapper.margin(incent, 0, 5, 0, 0), BorderLayout.WEST);
        inputPanel.add(input, BorderLayout.CENTER);

        add(inputPanel, BorderLayout.SOUTH);

        Arrays.asList(input, incent, console).forEach(this::setLooks);
    }

    private void setLooks(Component c) {
        c.setFont(new Font("Courier New", Font.PLAIN, 14));
        c.setForeground(Color.BLACK);
        c.setBackground(Color.WHITE);

        if (c instanceof JLabel) {
            ((JLabel)c).setOpaque(true);
        }
    }

    public void clear() {
        console.setText("");
    }

    private class InputKeyAdapter extends KeyAdapter {
        @Override public void keyReleased(KeyEvent e) {
            super.keyReleased(e);
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                new Thread(() -> {
                    String text = input.getText();
                    input.setText("");
                    //console.append(text);
                    printOutput(incent.getText() + text);
                    inputConsumer.accept(text);
                }).start();
            }
        }
    }

    public void setInputConsumer(Consumer<String> inputConsumer) {
        this.inputConsumer = inputConsumer;
    }

    public void startPrintingOutput() {
        printingNow.set(true);
    }

    public void printOutput(String text) {
        if (console.getText() != null && !console.getText().isEmpty()) {
            console.append("\n");
        }
        console.append(text);
        console.setCaretPosition(console.getDocument().getLength());
    }

    public void stopPrintingOutput() {
        printingNow.set(false);
        console.setCaretPosition(console.getDocument().getLength());
    }

    public void printThrowable(Throwable th, boolean fullLog) {
        StackTracePrinter stp = new StackTracePrinter(this::printOutput);
        if (fullLog) {
            stp.printStackTrace(th);
        }
        else {
            stp.printHeader(th);
        }
    }

    public void setIncentText(String incent) {
        this.incent.setText(incent);
    }

}
