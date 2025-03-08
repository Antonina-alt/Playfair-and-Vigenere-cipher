package com.adashkevich.encryption.app;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.text.Font;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends Application {

    private static TextField keyField;
    private static TextArea logArea;
    private ComboBox<String> algorithmSelector;
    private Button openFileButton;
    private Button encryptButton;
    private Button decryptButton;
    private File selectedFile;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Шифрование и Дешифрование");

        algorithmSelector = new ComboBox<>();
        algorithmSelector.getItems().addAll("Шифр Плейфейра", "Шифр Виженера");
        algorithmSelector.setPromptText("Выберите алгоритм");
        algorithmSelector.setOnAction(e -> openFileButton.setDisable(algorithmSelector.getValue() == null));

        openFileButton = new Button("Открыть файл");
        openFileButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        openFileButton.setDisable(true);
        openFileButton.setOnAction(e -> openFile(primaryStage));

        keyField = new TextField();
        keyField.setPromptText("Введите ключ");
        keyField.setFont(Font.font(14));
        keyField.setDisable(true);
        keyField.textProperty().addListener((obs, oldText, newText) -> {
            boolean keyEntered = !newText.trim().isEmpty();
            encryptButton.setDisable(!keyEntered);
            decryptButton.setDisable(!keyEntered);
        });

        encryptButton = new Button("Зашифровать");
        encryptButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        encryptButton.setDisable(true);
        encryptButton.setOnAction(e -> encryptFile());

        decryptButton = new Button("Расшифровать");
        decryptButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        decryptButton.setDisable(true);
        decryptButton.setOnAction(e -> decryptFile());

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setFont(Font.font("Arial", 14));
        logArea.setStyle("-fx-control-inner-background: #f0f0f0;");
        logArea.setPrefHeight(400);

        HBox buttonBox = new HBox(10, encryptButton, decryptButton);
        VBox layout = new VBox(15, algorithmSelector, openFileButton, keyField, buttonBox, logArea);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #ECEFF1;");

        Scene scene = new Scene(layout, 650, 550);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть текстовый файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));
        selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            logArea.appendText("Файл открыт: " + selectedFile.getAbsolutePath() + "\n");
            keyField.setDisable(false);
        }
    }

    private void encryptFile() {
        if (selectedFile == null || keyField.getText().trim().isEmpty()) {
            logArea.appendText("Ошибка: выберите файл и введите ключ перед шифрованием.\n");
            return;
        }
        try {
            String content = new String(Files.readAllBytes(selectedFile.toPath()));
            String key = keyField.getText().trim();
            String algorithm = algorithmSelector.getValue();

            String encryptedText = "";
            logArea.setText("");
            if ("Шифр Плейфейра".equals(algorithm)) {
                PlayfairCipher cipher = new PlayfairCipher(key);
                encryptedText = cipher.encrypt(content);
            } else if ("Шифр Виженера".equals(algorithm)) {
                encryptedText = VigenereCipher.encrypt(content, key);
            }
            Files.write(Paths.get(selectedFile.getParent(), "encrypted.txt"), encryptedText.getBytes());
            logArea.appendText("Результат шифрования: " + encryptedText + "\n");
            logArea.appendText("Файл зашифрован и сохранен как encrypted.txt\n");
        } catch (IOException e) {
            logArea.appendText("Ошибка при обработке файла: " + e.getMessage() + "\n");
        }
    }

    private void decryptFile() {
        if (selectedFile == null || keyField.getText().trim().isEmpty()) {
            logArea.appendText("Ошибка: выберите файл и введите ключ перед расшифрованием.\n");
            return;
        }
        try {
            String content = new String(Files.readAllBytes(selectedFile.toPath()));
            String key = keyField.getText().trim();
            String algorithm = algorithmSelector.getValue();

            String decryptedText = "";
            logArea.setText("");
            if ("Шифр Плейфейра".equals(algorithm)) {
                PlayfairCipher cipher = new PlayfairCipher(key);
                decryptedText = cipher.decrypt(content);
            } else if ("Шифр Виженера".equals(algorithm)) {
                decryptedText = VigenereCipher.decrypt(content, key);
            }
            Files.write(Paths.get(selectedFile.getParent(), "decrypted.txt"), decryptedText.getBytes());
            logArea.appendText("Результат расшифрования: " + decryptedText + "\n");
            logArea.appendText("Файл расшифрован и сохранен как decrypted.txt\n");
        } catch (IOException e) {
            logArea.appendText("Ошибка при обработке файла: " + e.getMessage() + "\n");
        }
    }

    public static void appendToLog(String message) {
        logArea.appendText(message + "\n");
    }

    public static void clearKeyField() {
        keyField.setText("");
    }
}

