package com.example.TYPERUSH;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.io.IOException;
import java.util.Random;

public class GameController {
    @FXML private Label targetLabel, wpmLabel, accuracyLabel, levelLabel;
    @FXML private TextField inputField;
    @FXML private StackPane carContainer;


    private String[] paragraphBank = {
        "Consistency is the foundation of improvement in any skill. Typing regularly helps build speed, accuracy, and confidence. Small daily efforts often produce better results than occasional intense practice. Focus on steady progress rather than perfection.",
"Developing a smooth typing rhythm reduces mental strain and increases focus. When your fingers move naturally across the keyboard, ideas flow more freely. Avoid rushing, as speed without control often leads to errors. Balance is the secret to lasting progress.",
"A hare once mocked a slow-moving tortoise for his steady pace. Tired of the teasing, the tortoise calmly challenged the hare to a race. Confident in his speed, the hare ran far ahead and decided to rest midway. Meanwhile, the tortoise continued walking without stopping or losing focus. In the end, slow and steady effort won the race.",
            "Graph theory and linear algebra are powerful mathematical tools used extensively in computer science. They form the backbone of complex network routing algorithms, machine learning models, and advanced graphics rendering pipelines.",
            "Studying at a rigorous engineering campus requires dedication and late-night problem-solving. From debugging complex assignments to collaborating with classmates on challenging course materials, the journey builds resilience and deep technical expertise."
    };
    private String currentText;
    private long startTime;
    private int totalKeyStrokes = 0, correctKeyStrokes = 0, wpm = 0, accuracy = 100, wordCount = 0;
    private boolean isRunning = false;
    private boolean isRaceFinished = false; // নতুন ফ্ল্যাগ

    @FXML public void initialize() { resetGame(); }

    public void resetGame() {
        totalKeyStrokes = 0;
        correctKeyStrokes = 0;
        isRunning = false;
        isRaceFinished = false;
        inputField.clear();
        inputField.setEditable(true);
        inputField.setStyle("-fx-border-color: #333;");

        // --- NEW PARAGRAPH SELECTION LOGIC ---
        Random rand = new Random();
        // Pick 1 random paragraph from the 5 available
        currentText = paragraphBank[rand.nextInt(paragraphBank.length)];

        // Dynamically count the words in the selected paragraph so WPM is accurate
        wordCount = currentText.split("\\s+").length;
        // -------------------------------------

        targetLabel.setText(currentText);
        carContainer.setLayoutX(40);
        wpmLabel.setText("WPM: 0");
        accuracyLabel.setText("Accuracy: 100%");
        levelLabel.setText("Level: Paragraph Mode"); // Updated level text
    }

    @FXML protected void handleTyping() {
        // যদি রেস অলরেডি শেষ হয়ে থাকে, তবে আর কিছু করবে না
        if (isRaceFinished) return;

        String input = inputField.getText();
        if (input.isEmpty()) return;

        if (!isRunning) {
            startTime = System.currentTimeMillis();
            isRunning = true;
        }

        totalKeyStrokes++;

        if (currentText.startsWith(input)) {
            correctKeyStrokes = input.length();
            double progress = (double) input.length() / currentText.length();
            carContainer.setLayoutX(40 + (progress * 1000));
            updateStats(input.length());
            inputField.setStyle("-fx-border-color: #2ecc71;");
        } else {
            carContainer.setLayoutX(Math.max(40, carContainer.getLayoutX() - 15));
            inputField.setStyle("-fx-border-color: #ff4757;");
        }

        // রেস শেষ হওয়ার লজিক
        if (input.equals(currentText)) {
            isRunning = false;
            isRaceFinished = true; // রেস শেষ হিসেবে মার্ক করা হলো
            inputField.setEditable(false);
            saveResult();
        }
    }

    private void updateStats(int len) {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed <= 0) return;

        wpm = (int) ((len / 5.0) / (elapsed / 60000.0));
        accuracy = (int) ((double) correctKeyStrokes / totalKeyStrokes * 100);

        wpmLabel.setText("WPM: " + wpm);
        accuracyLabel.setText("Accuracy: " + Math.min(100, accuracy) + "%");
    }

    private void saveResult() {
        double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
        // নিশ্চিত করা হচ্ছে যে টাইম ০ এর বেশি
        if (timeTaken <= 0.1) return;

        UserManager.currentUser.addResult(new RaceResult(wpm, accuracy, timeTaken, wordCount));
        UserManager.saveUsers();
        levelLabel.setText("Race Finished & Saved!");
    }

    @FXML protected void goToProfile() throws IOException { HelloApplication.showProfileScene(); }
}