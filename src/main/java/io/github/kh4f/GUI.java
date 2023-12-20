package io.github.kh4f;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {
    private JPanel panelMain;
    private JButton undoBtn;
    private JTextArea textField;
    private JButton redoBtn;
    boolean itsNewState = true;
    boolean consoleOutput;
    int eventCounter = 0;

    private final DoublyLinkedList<String> textNodeList = new DoublyLinkedList<>();
    DoublyLinkedList.Node<String> currNode;

    public GUI(boolean consoleOutput) throws DoublyLinkedList.DoublyLinkedListException {
        this.consoleOutput = consoleOutput;

        this.setTitle("Text editor");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Установка положения окна в центре экрана
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - 300) / 2, (screenSize.height - 500) / 2);

        this.pack();


        textNodeList.addLast("");
        currNode = textNodeList.getTail();
        debugPrint("textNodeList status: " + textNodeList);
        debugPrint("currNode status: " + currNode.getValue() + "\n");


        // Перезаписывание событий вставки и удаления у текстового поля
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

                    // Так как setText вызывает события изменения текстового поля
                    itsNewState = false;
                    textField.setText(currNode.getValue());
                    itsNewState = true;
                    // (itsNewState всегда true, если изменение произошло не по кнопкам)
                    
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
            debugPrint("New text node detected!");

            textNodeList.addLast(textField.getText());
            debugPrint("textNodeList changed: " + textNodeList);

            currNode = textNodeList.getTail();
            debugPrint("currNode changed: " + currNode.getValue());
        } else {
            debugPrint("No new node, but current node changed.");
            debugPrint("textNodeList is still the same: " + textNodeList);
            debugPrint("currNode changed: " + currNode.getValue());
       }
       changeButtonsVisibility();
    }

    public void changeButtonsVisibility() {
        undoBtn.setEnabled(currNode.getPrev() != null);
        redoBtn.setEnabled(currNode.getNext() != null);
    }

    public void debugPrint(String message) {
        if (consoleOutput) {
            System.out.println(message);
        }
    }
    
}
