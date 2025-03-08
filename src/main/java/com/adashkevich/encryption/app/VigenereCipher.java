package com.adashkevich.encryption.app;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class VigenereCipher {
    private static final String ALPHABET = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";

    public static String encrypt(String text, String key) {
        if (!isValidKey(key)) {
            showError();
            Main.clearKeyField();
            throw new IllegalArgumentException("Ключ должен содержать только символы русского алфавита.");
        }

        text = filterText(text);
        key = generateProgressiveKey(text, key);
        StringBuilder encryptedText = new StringBuilder();

        String substitutionTable = "Таблица подстановки:\n" +
                "Исходный текст:          " + text + "\n" +
                "Прогрессивный ключ: " + key + "\n";
        Main.appendToLog(substitutionTable);

        for (int i = 0; i < text.length(); i++) {
            int textIndex = ALPHABET.indexOf(text.charAt(i));
            int keyIndex = ALPHABET.indexOf(key.charAt(i));
            int newIndex = (textIndex + keyIndex) % ALPHABET.length();
            encryptedText.append(ALPHABET.charAt(newIndex));
        }

        return encryptedText.toString();
    }

    public static String decrypt(String text, String key) {
        if (!isValidKey(key)) {
            showError();
            Main.clearKeyField();
            throw new IllegalArgumentException("Ключ должен содержать только символы русского алфавита.");
        }

        text = filterText(text);
        key = generateProgressiveKey(text, key);
        StringBuilder decryptedText = new StringBuilder();

        String substitutionTable = "Таблица подстановки:\n" +
                "Зашифрованный текст:  " + text + "\n" +
                "Прогрессивный ключ:     " + key + "\n";
        Main.appendToLog(substitutionTable);

        for (int i = 0; i < text.length(); i++) {
            int textIndex = ALPHABET.indexOf(text.charAt(i));
            int keyIndex = ALPHABET.indexOf(key.charAt(i));
            int newIndex = (textIndex - keyIndex + ALPHABET.length()) % ALPHABET.length();
            decryptedText.append(ALPHABET.charAt(newIndex));
        }

        return decryptedText.toString();
    }

    private static boolean isValidKey(String key) {
        return key.matches("[А-ЯЁа-яё]+");
    }

    private static void showError() {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Ключ должен содержать только символы русского алфавита.");
            alert.showAndWait();
        });
    }

    private static String filterText(String text) {
        return text.toUpperCase().replaceAll("[^А-ЯЁ]", "");
    }

    private static String generateProgressiveKey(String text, String key) {
        StringBuilder expandedKey = new StringBuilder();
        key = filterText(key);

        while (expandedKey.length() < text.length()) {
            expandedKey.append(key);
            key = shiftKey(key);
        }

        return expandedKey.substring(0, text.length());
    }

    private static String shiftKey(String key) {
        StringBuilder shiftedKey = new StringBuilder();
        for (char c : key.toCharArray()) {
            int index = ALPHABET.indexOf(c);
            int newIndex = (index + 1) % ALPHABET.length();
            shiftedKey.append(ALPHABET.charAt(newIndex));
        }
        return shiftedKey.toString();
    }
}
