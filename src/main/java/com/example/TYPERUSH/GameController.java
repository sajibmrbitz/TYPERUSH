package com.example.TYPERUSH;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
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
    @FXML private Pane raceTrackContainer;
    @FXML private ImageView handGuideView;

    private static String selectedDifficulty = "NORMAL";
    private static boolean isTutorMode = false;

    public static void setTutorMode(boolean value) {
        isTutorMode = value;
    }

    private final String[] beginnerBank = {
            "osman bin hadi is a young talent in our country who is known for his work in bangladesh today",
            "july revolution in bangladesh showed the power of students and the unity of common people now",
            "pilkhana tragedy was a very sad day for our nation and we remember the brave soldiers always",
            "safe street movement in two thousand eighteen was started by students to make our roads better",
            "academic pressure in buet is very high for every student and we have to study all day and night"
    };
    private final String[] intermediateBank = {
            "Myrtar Osman Bin Hadi is making a significant impact in BD through his dedicated social work and vision.",
            "The July revolution in BD proved that when students stand together, they can change the entire history.",
            "The Pilkhana tragedy of BDR remains one of the darkest chapters in our history, where many lives were lost.",
            "In 2018, the Safe Street Movement taught us how school children can lead a nation towards better discipline.",
            "Academic pressure of BUET is no joke; balancing lab reports and term finals is a constant struggle for us."
    };

    private final String[] proBank = {
            "Osman Bin Hadi (a young visionary) is working 24/7 for BD; his efforts are 100% focused on social change!",
            "The 'July Revolution' of 2024 was a massive shift; students faced 100% risks to ensure a new future for BD.",
            "Pilkhana Tragedy (Feb 25, 2009) was a national crisis; we lost 57+ brave army officers in that dark event.",
            "The 'Safe Street Movement' (2018) raised a 10/10 awareness about road safety and traffic laws in Dhaka city!",
            "Life at BUET: 5 theory courses + 3 labs per week = 0% free time. The O(n!) complexity of exams is real!"
    };

    private String currentText;
    private long startTime;
    private int totalKeyStrokes = 0, correctKeyStrokes = 0, wpm = 0, accuracy = 100, wordCount = 0;
    private boolean isRunning = false, isRaceFinished = false;
    private int previousInputLength = 0;

    public static void setDifficulty(String diff) {
        selectedDifficulty = diff;
    }

    @FXML public void initialize() {
        levelLabel.setText("Level: " + selectedDifficulty);

        if (isTutorMode) {
            raceTrackContainer.setVisible(false);
            raceTrackContainer.setManaged(false);
        } else {
            raceTrackContainer.setVisible(true);
            raceTrackContainer.setManaged(true);
        }

        resetGame();
    }

    public void resetGame() {
        previousInputLength = 0;
        totalKeyStrokes = 0;
        correctKeyStrokes = 0;
        isRunning = false;
        isRaceFinished = false;
        inputField.clear();
        inputField.setEditable(true);
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
            char c = currentText.charAt(i);
            Label charLabel = new Label(String.valueOf(c));
            charLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 24px; -fx-font-family: 'Courier New';");

            if (c == ' ') {
                // space = TextFlow is allowed to wrap here
                charLabel.setMinWidth(0);
                charLabel.setPrefWidth(8);
            } else {
                // non-space = never break mid-word
                charLabel.setMinWidth(Label.USE_PREF_SIZE);
            }

            targetTextFlow.getChildren().add(charLabel);
        }

        wordCount = currentText.split("\\s+").length;
        if (!isTutorMode) carContainer.setLayoutX(40);
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
            previousInputLength = 0;
            return;
        }

        if (inputLength > previousInputLength && inputLength <= currentText.length()) {
            char typedChar = input.charAt(inputLength - 1);
            char targetChar = currentText.charAt(inputLength - 1);
            if (typedChar == targetChar) {
                SoundManager.getInstance().playCorrect();
            } else {
                SoundManager.getInstance().playWrong();
            }
        }
        previousInputLength = inputLength;

        if (!isRunning) { startTime = System.currentTimeMillis(); isRunning = true; }

        totalKeyStrokes++;
        int currentCorrectInInput = 0;

        for (int i = 0; i < targetTextFlow.getChildren().size(); i++) {
            Label l = (Label) targetTextFlow.getChildren().get(i);
            char c = currentText.charAt(i);

            if (i < inputLength) {
                if (input.charAt(i) == c) {
                    l.setStyle("-fx-background-color: rgba(46, 204, 113, 0.3); -fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
                    currentCorrectInInput++;
                } else {
                    l.setStyle("-fx-background-color: rgba(255, 71, 87, 0.4); -fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
                }
            } else {
                l.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
            }

            // reapply word boundary rules after every style change
            if (c == ' ') {
                l.setMinWidth(0);
                l.setPrefWidth(8);
            } else {
                l.setMinWidth(Label.USE_PREF_SIZE);
            }
        }

        correctKeyStrokes = currentCorrectInInput;
        double ratio = (double) correctKeyStrokes / currentText.length();

        if (!isTutorMode) {
            carContainer.setLayoutX(40.0 + (ratio * 1000.0));
        }

        updateStats(inputLength);
        if (inputLength < currentText.length()) {
            updateHandGuide(currentText.charAt(inputLength));
        }

        if (inputLength >= currentText.length()) {
            isRunning = false;
            isRaceFinished = true;
            inputField.setEditable(false);
            SoundManager.getInstance().playFinish();
            saveResult();
        }
    }

    private void resetHighlighting() {
        for (int i = 0; i < targetTextFlow.getChildren().size(); i++) {
            Label l = (Label) targetTextFlow.getChildren().get(i);
            char c = currentText.charAt(i);
            l.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
            if (c == ' ') {
                l.setMinWidth(0);
                l.setPrefWidth(8);
            } else {
                l.setMinWidth(Label.USE_PREF_SIZE);
            }
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
        } catch (Exception e) {
            // nothing
        }
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
    @FXML protected void menupage() { switchScene("menu-view.fxml", "Menu Page"); }
}
