package com.example.TYPERUSH;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextFlow;
import java.io.InputStream;
import java.util.Random;
import javafx.scene.media.AudioClip;

public class GameController extends BaseController {
    @FXML private Label wpmLabel, accuracyLabel, levelLabel;
    @FXML private TextFlow targetTextFlow;
    @FXML private TextField inputField;
    @FXML private StackPane carContainer;
    @FXML private ImageView handGuideView;

    private static String selectedDifficulty = "NORMAL"; // Default

    // 15 Paragraphs (5 for each level)
    private final String[] beginnerBank = {
            "asdf jkl; asdf jkl; a s d f j k l semicolon all day long typing is fun and easy for everyone start now",
            "the cat sat on the mat and the dog ran to the park for a ball to play with my friend today",
            "red blue green yellow black white orange purple pink brown grey silver gold clear bright dark light sun moon",
            "home row keys are the best way to start learning how to type fast without looking at the keys now",
            "keep your back straight and your feet on the floor while you type these simple words over and over again"
    };

    private final String[] intermediateBank = {
            "The quick brown fox jumps over the lazy dog. Typing with capital letters and periods is a step up from beginner.",
            "Consistency is the foundation of improvement in any skill. Typing regularly helps build speed, accuracy, and confidence. Practice daily.",
            "Developing a smooth typing rhythm reduces mental strain and increases focus. When your fingers move naturally, ideas flow freely and clearly.",
            "A hare once mocked a slow-moving tortoise for his steady pace. The tortoise challenged the hare to a race. Slow and steady wins.",
            "Coding in Java requires attention to detail. Semicolons and curly braces are essential for your program to run correctly and efficiently."
    };

    private final String[] proBank = {
            "Complex algorithms (like O(log n)) require precise typing. Don't forget: 'Single Quotes' and \"Double Quotes\" are different in most languages!",
            "Hyper-threading technology (HTT) is used to improve parallelization of computations performed on x86 microprocessors. It is very technical stuff.",
            "User-defined functions {int main()} must be declared properly. Errors like NullPointerException or StackOverflowError can be very frustrating for developers.",
            "The 19th century was a period of rapid industrialization. Steam engines (Watt's design) changed the world forever; 100% true story!",
            "Can you type @#% symbols quickly? Professional typists reach 120+ WPM while maintaining 99% accuracy on technical documentation and logs."
    };

    private String currentText;
    private long startTime;
    private int totalKeyStrokes = 0, correctKeyStrokes = 0, wpm = 0, accuracy = 100, wordCount = 0;
    private boolean isRunning = false, isRaceFinished = false;

    // Static method to set difficulty from Menu or Level Selector
    public static void setDifficulty(String diff) {
        selectedDifficulty = diff;
    }

    @FXML public void initialize() {
        levelLabel.setText("Level: " + selectedDifficulty);
        resetGame();
    }

    public void resetGame() {
        totalKeyStrokes = 0; correctKeyStrokes = 0;
        isRunning = false; isRaceFinished = false;
        inputField.clear(); inputField.setEditable(true);
        inputField.setStyle("-fx-border-color: #333;");

        Random rand = new Random();
        String[] bank;
        switch (selectedDifficulty) {
            case "BEGINNER" -> bank = beginnerBank;
            case "INTERMEDIATE" -> bank = intermediateBank;
            case "PRO" -> bank = proBank;
            default -> bank = intermediateBank;
        }

        currentText = bank[rand.nextInt(5)];

        targetTextFlow.getChildren().clear();
        for (int i = 0; i < currentText.length(); i++) {
            Label charLabel = new Label(String.valueOf(currentText.charAt(i)));
            charLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
            targetTextFlow.getChildren().add(charLabel);
        }

        wordCount = currentText.split("\\s+").length;
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
        int currentCorrectInInput = 0;
        for (int i = 0; i < targetTextFlow.getChildren().size(); i++) {
            Label l = (Label) targetTextFlow.getChildren().get(i);
            if (i < inputLength) {
                if (input.charAt(i) == currentText.charAt(i)) {
                    l.setStyle("-fx-background-color: rgba(46, 204, 113, 0.3); -fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
                    currentCorrectInInput++;
                } else {
                    l.setStyle("-fx-background-color: rgba(255, 71, 87, 0.4); -fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
                }
            } else {
                l.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
            }
        }

        correctKeyStrokes = currentCorrectInInput;
        double ratio = (double) correctKeyStrokes / currentText.length();
        carContainer.setLayoutX(40.0 + (ratio * 1000.0));

        updateStats(inputLength);
        if (inputLength < currentText.length()) {
            updateHandGuide(currentText.charAt(inputLength));
        }

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
            wpm = (int) ((correctKeyStrokes / 5.0) / ((elapsed / 1000.0) / 60.0));
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