package com.example.TYPERUSH;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import java.util.Random;

public class GameController extends BaseController {
    @FXML private Label targetLabel, wpmLabel, accuracyLabel, levelLabel;
    @FXML private TextField inputField;
    @FXML private StackPane carContainer;
    @FXML private ImageView handGuideView; // Added for hand guide

    private String[] paragraphBank = {
            "Consistency is the foundation of improvement in any skill. Typing regularly helps build speed, accuracy, and confidence. Small daily efforts often produce better results than occasional intense practice.",
            "Developing a smooth typing rhythm reduces mental strain and increases focus. When your fingers move naturally across the keyboard, ideas flow more freely. Balance is the secret to lasting progress.",
            "A hare once mocked a slow-moving tortoise for his steady pace. Tired of the teasing, the tortoise challenged the hare to a race. In the end, slow and steady effort won the race.",
            "Graph theory and linear algebra are powerful mathematical tools used extensively in computer science. They form the backbone of complex network routing algorithms and machine learning models.",
            "Studying at a rigorous engineering campus requires dedication and late-night problem-solving. The journey builds resilience and deep technical expertise through challenging course materials."
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
        currentText = paragraphBank[rand.nextInt(5)];

        int spaceCount = 0;
        for (int i = 0; i < currentText.length(); i++) {
            if (currentText.charAt(i) == ' ') spaceCount++;
        }
        wordCount = spaceCount + 1;

        targetLabel.setText(currentText);
        carContainer.setLayoutX(40);
        wpmLabel.setText("WPM: 0");
        accuracyLabel.setText("Accuracy: 100%");
        levelLabel.setText("Level: Manual Mode");

        // Show guide for the very first character
        updateHandGuide(currentText.charAt(0));
    }

    @FXML protected void handleTyping() {
        if (isRaceFinished) return;
        String input = inputField.getText();
        int inputLength = input.length();
        if (inputLength == 0) {
            updateHandGuide(currentText.charAt(0));
            return;
        }
        if (!isRunning) { startTime = System.currentTimeMillis(); isRunning = true; }

        totalKeyStrokes++;
        boolean isCorrect = true;
        if (inputLength > currentText.length()) isCorrect = false;
        else {
            for (int i = 0; i < inputLength; i++) {
                if (input.charAt(i) != currentText.charAt(i)) { isCorrect = false; break; }
            }
        }

        if (isCorrect) {
            correctKeyStrokes = inputLength;
            double ratio = (double) inputLength / currentText.length();
            carContainer.setLayoutX(40.0 + (ratio * 1000.0));
            updateStats(inputLength);
            inputField.setStyle("-fx-border-color: #2ecc71;");

            // Update hand guide for the NEXT character
            if (inputLength < currentText.length()) {
                updateHandGuide(currentText.charAt(inputLength));
            }
        } else {
            if (carContainer.getLayoutX() > 40.0) carContainer.setLayoutX(carContainer.getLayoutX() - 5.0);
            inputField.setStyle("-fx-border-color: #ff4757;");
        }

        if (inputLength == currentText.length() && isCorrect) {
            isRunning = false; isRaceFinished = true;
            inputField.setEditable(false);
            saveResult();
        }
    }

    private void updateHandGuide(char nextChar) {
        try {
            // ASCII Logic
            int ascii = (int) Character.toUpperCase(nextChar);

            // Special cases for Space, Full stop, Comma
            if (nextChar == ' ') ascii = 32;
            else if (nextChar == '.') ascii = 46;
            else if (nextChar == ',') ascii = 44;

            String path = "hands/" + ascii + ".png";
            InputStream is = getClass().getResourceAsStream(isExist(path) ? path : "hands/32.png");

            if (is != null) {
                handGuideView.setImage(new Image(is));
            }
        } catch (Exception e) {
            // Fallback to spacebar image if something goes wrong
        }
    }

    private boolean isExist(String path) {
        return getClass().getResourceAsStream(path) != null;
    }

    private void updateStats(int charLength) {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 0) {
            double minutes = (elapsed / 1000.0) / 60.0;
            wpm = (int) ((charLength / 5.0) / minutes);
            accuracy = (int) (((double) correctKeyStrokes / totalKeyStrokes) * 100);
            if (accuracy > 100) accuracy = 100;
            wpmLabel.setText("WPM: " + wpm);
            accuracyLabel.setText("Accuracy: " + accuracy + "%");
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
}