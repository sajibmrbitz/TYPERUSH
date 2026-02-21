package com.example.TYPERUSH;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextFlow;
import java.io.InputStream;
import java.util.Random;

public class GameController extends BaseController {
    @FXML private Label wpmLabel, accuracyLabel, levelLabel;
    @FXML private TextFlow targetTextFlow;
    @FXML private TextField inputField;
    @FXML private StackPane carContainer;
    @FXML private ImageView handGuideView;

    private String[] paragraphBank = {
            "Consistency is the foundation of improvement in any skill. Typing regularly helps build speed, accuracy, and confidence. Small daily efforts often produce better results than occasional intense practice.",
            "Developing a smooth typing rhythm reduces mental strain and increases focus. When your fingers move naturally across the keyboard, ideas flow more freely. Balance is the secret to lasting progress.",
            "A hare once mocked a slow-moving tortoise for his steady pace. Tired of the teasing, the tortoise challenged the hare to a race. In the end, slow and steady effort won the race."
    };

    private String currentText;
    private long startTime;
    private int totalKeyStrokes = 0, correctKeyStrokes = 0, wpm = 0, accuracy = 100, wordCount = 0;
    private boolean isRunning = false, isRaceFinished = false;

    @FXML public void initialize() { resetGame(); }

    public void resetGame() {
        totalKeyStrokes = 0; correctKeyStrokes = 0;
        isRunning = false; isRaceFinished = false;
        inputField.clear(); inputField.setEditable(true);
        inputField.setStyle("-fx-border-color: #333;");

        Random rand = new Random();
        currentText = paragraphBank[rand.nextInt(3)];

        targetTextFlow.getChildren().clear();
        for (int i = 0; i < currentText.length(); i++) {
            Label charLabel = new Label(String.valueOf(currentText.charAt(i)));
            charLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
            targetTextFlow.getChildren().add(charLabel);
        }

        int spaceCount = 0;
        for (int i = 0; i < currentText.length(); i++) {
            if (currentText.charAt(i) == ' ') spaceCount++;
        }
        wordCount = spaceCount + 1;

        carContainer.setLayoutX(40);
        wpmLabel.setText("WPM: 0");
        accuracyLabel.setText("Accuracy: 100%");
        updateHandGuide(currentText.charAt(0));
    }

    @FXML protected void handleTyping() {
        if (isRaceFinished) return;
        String input = inputField.getText();
        int inputLength = input.length();

        if (inputLength == 0) {
            resetHighlighting();
            updateHandGuide(currentText.charAt(0));
            return;
        }

        if (!isRunning) { startTime = System.currentTimeMillis(); isRunning = true; }
        totalKeyStrokes++;

        // --- Highlighting & Correct Count Logic ---
        int currentCorrectInInput = 0;
        for (int i = 0; i < targetTextFlow.getChildren().size(); i++) {
            Label l = (Label) targetTextFlow.getChildren().get(i);
            if (i < inputLength) {
                if (input.charAt(i) == currentText.charAt(i)) {
                    // Correct character
                    l.setStyle("-fx-background-color: rgba(46, 204, 113, 0.3); -fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
                    currentCorrectInInput++;
                } else {
                    // Wrong character
                    l.setStyle("-fx-background-color: rgba(255, 71, 87, 0.4); -fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
                }
            } else {
                l.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
            }
        }

        // --- Updated Car Movement Logic ---
        // গাড়ি এখন আগাবে ইনপুটের ভেতর যতগুলো সঠিক অক্ষর আছে তার ওপর ভিত্তি করে
        correctKeyStrokes = currentCorrectInInput;
        double ratio = (double) correctKeyStrokes / currentText.length();
        carContainer.setLayoutX(40.0 + (ratio * 1000.0));

        // Update Stats & Hand Guide
        updateStats(inputLength);
        if (inputLength < currentText.length()) {
            updateHandGuide(currentText.charAt(inputLength));
        }

        // Finish Logic
        if (inputLength >= currentText.length()) {
            isRunning = false;
            isRaceFinished = true;
            inputField.setEditable(false);
            saveResult();
        }
    }

    private void resetHighlighting() {
        for (int i = 0; i < targetTextFlow.getChildren().size(); i++) {
            Label l = (Label) targetTextFlow.getChildren().get(i);
            l.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
        }
    }

    private void updateHandGuide(char nextChar) {
        try {
            int ascii = (int) Character.toUpperCase(nextChar);
            if (nextChar == ' ') ascii = 32;
            else if (nextChar == '.') ascii = 46;
            else if (nextChar == ',') ascii = 44;
            InputStream is = getClass().getResourceAsStream("hands/" + ascii + ".png");
            if (is != null) handGuideView.setImage(new Image(is));
        } catch (Exception e) {}
    }

    private void updateStats(int charLength) {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 0) {
            // WPM is based on total characters typed correctly
            wpm = (int) ((correctKeyStrokes / 5.0) / ((elapsed / 1000.0) / 60.0));
            // Accuracy is (Correct characters in current input) / (Total keys pressed)
            accuracy = (int) (((double) correctKeyStrokes / totalKeyStrokes) * 100);
            wpmLabel.setText("WPM: " + wpm);
            accuracyLabel.setText("Accuracy: " + (accuracy > 100 ? 100 : accuracy) + "%");
        }
    }

    private void saveResult() {
        double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
        if (timeTaken > 0.1) {
            UserManager.addResult(new RaceResult(wpm, accuracy, timeTaken, wordCount));
            levelLabel.setText("Race Saved!");
        }
    }

    @FXML protected void goToProfile() { switchScene("profile-view.fxml", "User Profile"); }
    @FXML protected void menupage(){  switchScene("menu-view.fxml", "Menu Page");}
}