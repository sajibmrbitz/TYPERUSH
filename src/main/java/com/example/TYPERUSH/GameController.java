package com.example.TYPERUSH;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import java.util.Random;

public class GameController extends BaseController { // Inheritance
    @FXML private Label targetLabel, wpmLabel, accuracyLabel, levelLabel;
    @FXML private TextField inputField;
    @FXML private StackPane carContainer;

    private String[] wordBank = {
            "Artificial intelligence is the simulation of human intelligence processes by machines, especially computer systems.",
            "Java is a high-level, class-based, object-oriented programming language designed to have few dependencies.",
            "Global warming is the long-term heating of Earth surface observed since the pre-industrial period."
    };

    private String currentText;
    private long startTime;
    private int totalKeyStrokes, correctKeyStrokes, wpm, accuracy;
    private boolean isRunning = false;
    private boolean isRaceFinished = false;

    @FXML public void initialize() { resetGame(); }

    public void resetGame() {
        isRunning = false;
        isRaceFinished = false;
        totalKeyStrokes = 0;
        correctKeyStrokes = 0;
        inputField.clear();
        inputField.setEditable(true);

        Random rand = new Random();
        currentText = wordBank[rand.nextInt(wordBank.length)];
        targetLabel.setText(currentText);
        carContainer.setLayoutX(40);

        wpmLabel.setText("WPM: 0");
        accuracyLabel.setText("Accuracy: 100%");
    }

    @FXML protected void handleTyping() {
        if (isRaceFinished) return;

        if (!isRunning) {
            startTime = System.currentTimeMillis();
            isRunning = true;
            startTimerThread(); // Threading start
        }

        String input = inputField.getText();
        totalKeyStrokes++;

        if (currentText.startsWith(input)) {
            correctKeyStrokes = input.length();
            double progress = (double) input.length() / currentText.length();
            carContainer.setLayoutX(40 + (progress * 950));
            inputField.setStyle("-fx-border-color: #2ecc71;");
        } else {
            inputField.setStyle("-fx-border-color: #ff4757;");
        }

        if (input.equals(currentText)) {
            isRunning = false;
            isRaceFinished = true;
            inputField.setEditable(false);
            saveResult();
        }
    }

    private void startTimerThread() {
        Thread timerThread = new Thread(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(500);
                    Platform.runLater(() -> updateStats());
                } catch (InterruptedException e) {
                    System.out.println("Thread Error: " + e.getMessage());
                }
            }
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }

    private void updateStats() {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 0) {
            int currentLen = inputField.getText().length();
            wpm = (int) ((currentLen / 5.0) / (elapsed / 60000.0));
            accuracy = (totalKeyStrokes > 0) ? (int) ((double) correctKeyStrokes / totalKeyStrokes * 100) : 100;
            wpmLabel.setText("WPM: " + wpm);
            accuracyLabel.setText("Accuracy: " + Math.min(100, accuracy) + "%");
        }
    }

    private void saveResult() {
        double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
        int wordCount = currentText.split("\\s+").length;
        RaceResult result = new RaceResult(wpm, accuracy, timeTaken, wordCount);
        UserManager.currentUser.addResult(result);
        UserManager.saveUsers();
        levelLabel.setText("Race Saved!");
    }

    @FXML protected void goToProfile() {
        switchScene("profile-view.fxml", "User Profile"); // BaseController method
    }
}