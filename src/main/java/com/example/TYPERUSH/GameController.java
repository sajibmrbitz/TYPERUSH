package com.example.TYPERUSH;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import java.util.Random;

public class GameController {
    @FXML private Label targetLabel, wpmLabel, accuracyLabel, levelLabel;
    @FXML private TextField inputField;
    @FXML private StackPane carContainer;

    private String[] easyTexts = {"The cat is on the mat.", "Java is a fun language.", "Type fast to win the race."};
    private String[] hardTexts = {"Adaptive learning algorithms optimize user performance.", "JavaFX provides a rich set of graphics and media APIs.", "Synchronous programming requires careful thread management."};

    private String currentText;
    private long startTime;
    private int level = 1;
    private int errors = 0;

    @FXML
    public void initialize() {
        resetGame();
    }

    public void resetGame() {
        errors = 0;
        inputField.clear();
        inputField.setEditable(true);

        // Adaptive Learning: লেভেল অনুযায়ী টেক্সট সিলেক্ট করা
        Random rand = new Random();
        currentText = (level == 1) ? easyTexts[rand.nextInt(easyTexts.length)] : hardTexts[rand.nextInt(hardTexts.length)];

        targetLabel.setText(currentText);
        carContainer.setLayoutX(20);
        startTime = System.currentTimeMillis();
    }

    @FXML
    protected void handleTyping() {
        String input = inputField.getText();
        int correctChars = 0;

        // কার এনিমেশন লজিক (Progress based)
        double progress = (double) input.length() / currentText.length();
        carContainer.setLayoutX(20 + (progress * 1000));

        // ভুল চেক করা (Red text if wrong)
        if (!currentText.startsWith(input)) {
            inputField.setStyle("-fx-text-fill: red;");
            errors++;
        } else {
            inputField.setStyle("-fx-text-fill: black;");
        }

        // গেম শেষ হলে
        if (input.equals(currentText)) {
            long endTime = System.currentTimeMillis();
            calculateStats(endTime - startTime);
            inputField.setEditable(false);
        }
    }

    private void calculateStats(long timeMillis) {
        double minutes = timeMillis / 60000.0;
        int wpm = (int) ((currentText.length() / 5.0) / minutes);
        int accuracy = Math.max(0, 100 - (errors * 100 / currentText.length()));

        wpmLabel.setText("WPM: " + wpm);
        accuracyLabel.setText("Accuracy: " + accuracy + "%");

        // Adaptive Learning Logic: রিসার্চের মূল অংশ
        if (wpm > 40 && accuracy > 90) {
            level = 2;
            levelLabel.setText("Level: 2 (Hard) - Adaptive Boost!");
        } else {
            level = 1;
            levelLabel.setText("Level: 1 (Easy)");
        }
    }
}