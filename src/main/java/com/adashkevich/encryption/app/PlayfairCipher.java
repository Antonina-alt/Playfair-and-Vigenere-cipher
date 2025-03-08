package com.adashkevich.encryption.app;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PlayfairCipher {
    private char[][] matrix;
    private static final int SIZE = 5;

    public PlayfairCipher(String key) {
        if (!key.matches("[A-Za-z]+")) {
            showError();
            Main.clearKeyField();
            throw new IllegalArgumentException("Ключ должен содержать только английские буквы.");
        }
        this.matrix = generateMatrix(key);
    }

    private void showError() {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Ключ должен содержать только английские буквы.");
            alert.showAndWait();
        });
    }


    private char[][] generateMatrix(String key) {
        boolean[] used = new boolean[26];
        char[][] table = new char[SIZE][SIZE];
        key = key.toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I");

        StringBuilder matrixString = new StringBuilder();
        for (char c : key.toCharArray()) {
            if (!used[c - 'A']) {
                matrixString.append(c);
                used[c - 'A'] = true;
            }
        }

        for (char c = 'A'; c <= 'Z'; c++) {
            if (c != 'J' && !used[c - 'A']) {
                matrixString.append(c);
                used[c - 'A'] = true;
            }
        }

        int index = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                table[i][j] = matrixString.charAt(index++);
            }
        }
        printMatrix(table);
        return table;
    }

    public void printMatrix(char[][] table) {
        StringBuilder matrixLog = new StringBuilder("Матрица шифрования:\n");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                matrixLog.append(table[i][j]).append(" ");
            }
            matrixLog.append("\n");
        }
        Main.appendToLog(matrixLog.toString());
    }

    public String encrypt(String text) {
        text = filterText(text);
        Main.appendToLog("Исходный текст: " + text);
        return processText(prepareText(text), true);
    }

    public String decrypt(String text) {
        text = filterText(text);
        Main.appendToLog("Исходный текст: " + text);
        return processText(text, false);
    }

    private String filterText(String text) {
        return text.toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I");
    }

    private String prepareText(String text) {
        StringBuilder preparedText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            preparedText.append(currentChar);

            if (i + 1 < text.length() && currentChar == text.charAt(i + 1)) {
                char filler = (currentChar == 'X') ? 'Z' : 'X';
                preparedText.append(filler);
            }
        }
        if (preparedText.length() % 2 != 0) {
            preparedText.append(preparedText.charAt(preparedText.length() - 1) == 'X' ? 'Z' : 'X');
        }
        return preparedText.toString();
    }


    private String processText(String text, boolean encrypt) {
        StringBuilder processedText = new StringBuilder();

        for (int i = 0; i < text.length(); i += 2) {
            char a = text.charAt(i);
            char b = text.charAt(i + 1);

            int[] posA = findPosition(a);
            int[] posB = findPosition(b);

            if (posA[0] == posB[0]) {
                processedText.append(matrix[posA[0]][(posA[1] + (encrypt ? 1 : 4)) % SIZE]);
                processedText.append(matrix[posB[0]][(posB[1] + (encrypt ? 1 : 4)) % SIZE]);
            } else if (posA[1] == posB[1]) {
                processedText.append(matrix[(posA[0] + (encrypt ? 1 : 4)) % SIZE][posA[1]]);
                processedText.append(matrix[(posB[0] + (encrypt ? 1 : 4)) % SIZE][posB[1]]);
            } else {
                processedText.append(matrix[posA[0]][posB[1]]);
                processedText.append(matrix[posB[0]][posA[1]]);
            }
        }
        return processedText.toString();
    }

    private int[] findPosition(char c) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (matrix[i][j] == c) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
}
