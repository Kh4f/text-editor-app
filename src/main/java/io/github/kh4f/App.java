package io.github.kh4f;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {

        boolean consoleOutput = Arrays.stream(args).anyMatch(arg -> arg.equals("-debug"));

        java.awt.EventQueue.invokeLater(() -> {
            try {
                new GUI(consoleOutput).setVisible(true);
            } catch (DoublyLinkedList.DoublyLinkedListException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }
}
