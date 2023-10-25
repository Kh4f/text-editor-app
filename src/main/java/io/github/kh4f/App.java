package io.github.kh4f;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class App extends JFrame {
    private JPanel panelMain;
    private JButton undoBtn;
    private JTextArea textField;
    private JButton redoBtn;
    boolean itsNewState = true;
    boolean itsButtonPressing = false;
    static boolean debugMode = false;
    int eventCounter = 0;

    private final DoublyLinkedList<String> textChangesList = new DoublyLinkedList<>();
    DoublyLinkedList.Node<String> currNode;

    public App() throws DoublyLinkedList.DoublyLinkedListException {
        this.setTitle("Text editor");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - 300) / 2, (screenSize.height - 400) / 2);
        this.pack();

        undoBtn.setEnabled(false);
        redoBtn.setEnabled(false);

        textChangesList.addLast("");
        currNode = textChangesList.getTail();
        debugPrint("textChangesList status: " + textChangesList);
        debugPrint("currNode status: " + currNode.getValue() + "\n");



        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    debugPrint("\n" + (++eventCounter) + ". " + "Event insertUpdate triggered, itsNewState=" + itsNewState);
                    textChanged();
                } catch (DoublyLinkedList.DoublyLinkedListException ex) {
                    System.err.println(ex.getMessage());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e){
                try {
                    debugPrint("\n" + (++eventCounter) + ". " + "Event removeUpdate triggered, itsNewState=" + itsNewState);
                    textChanged();
                } catch (DoublyLinkedList.DoublyLinkedListException ex) {
                    System.err.println(ex.getMessage());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });

        undoBtn.addActionListener(actionEvent -> {
            try {
                if (currNode.getPrev() != null) {
                    currNode = currNode.getPrev();
                    itsNewState = false;

                    itsButtonPressing = true;
                    textField.setText("");
                    itsButtonPressing = false;

                    textField.setText(currNode.getValue());
                    itsNewState = true;


                    changeButtonsVisibility();
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        });

        redoBtn.addActionListener(actionEvent -> {
            try {
                if (currNode.getNext() != null) {
                    currNode = currNode.getNext();
                    itsNewState = false;

                    itsButtonPressing = true;
                    textField.setText("");
                    itsButtonPressing = false;

                    textField.setText(currNode.getValue());
                    itsNewState = true;

                    changeButtonsVisibility();
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        });

        AbstractAction undoAction = new AbstractAction("undo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                undoBtn.doClick();
            }
        };
        KeyStroke ctrlZKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
        textField.getInputMap().put(ctrlZKeyStroke, "undo");
        textField.getActionMap().put("undo", undoAction);

        AbstractAction redoAction = new AbstractAction("redo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                redoBtn.doClick();
            }
        };
        KeyStroke ctrlYKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);
        textField.getInputMap().put(ctrlYKeyStroke, "redo");
        textField.getActionMap().put("redo", redoAction);
    }

    public void textChanged() throws DoublyLinkedList.DoublyLinkedListException {
        if (itsNewState) {
            debugPrint("New text state detected!");

            textChangesList.addLast(textField.getText());
            debugPrint("textChangesList changed: " + textChangesList);

            currNode = textChangesList.getTail();
            debugPrint("currNode changed: " + currNode.getValue());
        } else {
            debugPrint("No new state, but current state changed.");
            debugPrint("textChangesList is still the same: " + textChangesList);
            debugPrint("currNode changed: " + currNode.getValue());
       }
       changeButtonsVisibility();
    }

    public void changeButtonsVisibility() {
        undoBtn.setEnabled(currNode.getPrev() != null);
        redoBtn.setEnabled(currNode.getNext() != null);
    }

    public void debugPrint(String message) {
        if (debugMode) {
            System.out.println(message);
        }
    }

    public static void main(String[] args) {

        for (String arg : args) {
            if (arg.equals("-debug")) {
                debugMode = true;
                break;
            }
        }

        java.awt.EventQueue.invokeLater(() -> {
            try {
                new App().setVisible(true);
            } catch (DoublyLinkedList.DoublyLinkedListException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }
}
