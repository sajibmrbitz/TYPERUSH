package com.example.TYPERUSH;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.util.Random;

public class GameController extends BaseController {
    @FXML private Label targetLabel,wpmLabel,accuracyLabel,levelLabel;
    @FXML private TextField inputField;
    @FXML private StackPane carContainer;

    private String[] paragraphBank = {
            "Consistency is the foundation of improvement in any skill. Typing regularly helps build speed, accuracy, and confidence. Small daily efforts often produce better results than occasional intense practice.",
            "Developing a smooth typing rhythm reduces mental strain and increases focus. When your fingers move naturally across the keyboard, ideas flow more freely. Balance is the secret to lasting progress.",
            "A hare once mocked a slow-moving tortoise for his steady pace. Tired of the teasing, the tortoise challenged the hare to a race. In the end, slow and steady effort won the race.",
            "Graph theory and linear algebra are powerful mathematical tools used extensively in computer science. They form the backbone of complex network routing algorithms and machine learning models.",
            "Studying at a rigorous engineering campus requires dedication and late-night problem-solving. The journey builds resilience and deep technical expertise through challenging course materials."
    };

    private String currentText;
    private long startTime;
    private int totalKeyStrokes = 0;
    private int correctKeyStrokes = 0;
    private int wpm = 0;
    private int accuracy = 100;
    private int wordCount = 0;
    private boolean isRunning = false;
    private boolean isRaceFinished = false;

    @FXML
    public void initialize() {
        resetGame();
    }

    public void resetGame() {
        totalKeyStrokes = 0;
        correctKeyStrokes = 0;
        isRunning = false;
        isRaceFinished = false;

        inputField.clear();
        inputField.setEditable(true);
        inputField.setStyle("-fx-border-color: #333;");

        // Pick a random paragraph
        Random rand = new Random();
        int randomIndex = rand.nextInt(5); // Hardcoded to 5 since there are 5 paragraphs
        currentText = paragraphBank[randomIndex];

        // Count words manually by counting spaces
        int spaceCount = 0;
        for (int i = 0; i < currentText.length(); i++) {
            char currentCharacter = currentText.charAt(i);
            if (currentCharacter == ' ') {
                spaceCount = spaceCount + 1;
            }
        }
        wordCount = spaceCount + 1; // Words are spaces + 1

        targetLabel.setText(currentText);
        carContainer.setLayoutX(40);
        wpmLabel.setText("WPM: 0");
        accuracyLabel.setText("Accuracy: 100%");
        levelLabel.setText("Level: Manual Mode");
    }

    @FXML
    protected void handleTyping() {
        if (isRaceFinished == true) {
            return;
        }

        String input = inputField.getText();
        int inputLength = input.length();

        if (inputLength == 0) {
            return;
        }

        if (isRunning == false) {
            startTime = System.currentTimeMillis();
            isRunning = true;
        }

        totalKeyStrokes = totalKeyStrokes + 1;

        // Check if typing is correct so far
        boolean isCorrect = true;
        int textLength = currentText.length();

        if (inputLength > textLength) {
            isCorrect = false;
        } else {
            for (int i = 0; i < inputLength; i++) {
                char inputChar = input.charAt(i);
                char textChar = currentText.charAt(i);

                if (inputChar != textChar) {
                    isCorrect = false;
                    break;
                }
            }
        }

        if (isCorrect == true) {
            correctKeyStrokes = inputLength;

            // Move car forward calculation (broken down into simple steps)
            double ratio = (double) inputLength / textLength;
            double moveDistance = ratio * 1000.0;
            double newPosition = 40.0 + moveDistance;

            carContainer.setLayoutX(newPosition);

            updateStats(inputLength);
            inputField.setStyle("-fx-border-color: #2ecc71;"); // Green border

        } else {
            // Move car back on error
            double currentX = carContainer.getLayoutX();
            if (currentX > 40.0) {
                carContainer.setLayoutX(currentX - 5.0);
            }
            inputField.setStyle("-fx-border-color: #ff4757;"); // Red border
        }

        // Check if the game is over
        if (inputLength == textLength) {
            if (isCorrect == true) {
                isRunning = false;
                isRaceFinished = true;
                inputField.setEditable(false);
                saveResult();
            }
        }
    }

    private void updateStats(int charLength) {
        long currentTime = System.currentTimeMillis();
        long elapsedMillis = currentTime - startTime;

        if (elapsedMillis > 0) {
            // Calculate WPM step-by-step
            double seconds = elapsedMillis / 1000.0;
            double minutes = seconds / 60.0;
            double words = charLength / 5.0; // 5 characters = 1 word

            wpm = (int) (words / minutes);

            // Calculate Accuracy step-by-step
            double accuracyDouble = (double) correctKeyStrokes / totalKeyStrokes;
            accuracyDouble = accuracyDouble * 100.0;
            accuracy = (int) accuracyDouble;

            // Simple if-statement instead of advanced ternary operator
            if (accuracy > 100) {
                accuracy = 100;
            }

            wpmLabel.setText("WPM: " + wpm);
            accuracyLabel.setText("Accuracy: " + accuracy + "%");
        }
    }

    private void saveResult() {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - startTime;
        double timeTaken = timeDiff / 1000.0;

        if (timeTaken > 0.1) {
            RaceResult res = new RaceResult(wpm, accuracy, timeTaken, wordCount);
            UserManager.currentUser.addResult(res);
            UserManager.saveUsers();
            levelLabel.setText("Race Saved!");
        }
    }

    @FXML
    protected void goToProfile() {
        switchScene("profile-view.fxml", "User Profile");
    }
}