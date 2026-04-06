package com.example.TYPERUSH;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import java.io.InputStream;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.layout.HBox;

public class GameController extends BaseController {
    @FXML private Label wpmLabel, accuracyLabel, levelLabel;
    @FXML private TextFlow targetTextFlow;
    @FXML private TextField inputField;
    @FXML private StackPane carContainer;
    @FXML private Pane raceTrackContainer;
    @FXML private ImageView handGuideView;
    @FXML private Label comboLabel;

    private static String selectedDifficulty = "NORMAL";
    private static boolean isTutorMode = false;

    public static void setTutorMode(boolean value) {
        isTutorMode = value;
    }

    private final String[] beginnerBank = {
            "osman bin hadi is a symbol of resistance and youth spirit in our country who was unfortunately killed just before the National Parliament Election and Referendum.",
            "july revolution in bangladesh showed the power of students and the unity of common people which led to the resignation of the then fascist government.",
            "pilkhana tragedy was a very sad day for our nation and we remember the brave soldiers who were killed in the mutiny.",
            "safe street movement in two thousand eighteen was started by students to make our roads better, safe and sound.",
            "academic pressure in buet is very high for every student and we have to study hard throughout the semester."
    };
    private final String[] intermediateBank = {
            "Martyr Osman Bin Hadi dreamt of a new Bangladesh and he had to sacrifice his life for it.",
            "The July revolution in BD proved that when students stand together, they can change the entire history.",
            "The Pilkhana tragedy of BDR remains one of the darkest chapters in our history, where many frontline and junior army officers were killed.",
            "In two thousand and eighteen, the 'Safe Street Movement' taught us how school children can lead a nation towards better discipline.",
            "Academic pressure of BUET is no joke; balancing lab reports and term finals is a constant struggle for us."
    };

    private final String[] proBank = {
            "Martyr Osman Bin Hadi (a young visionary) was an MP candidate from Dhaka 8, where he along with his fellow workers from Inkilab Manch conducted his election campaign in a quite simple way.",
            "The 'July Revolution' of 2024 was a massive shift for our political sector, and it took out the corrupt, fascist government from power.",
            "Pilkhana Tragedy, a dark chapter in the history of Bangladesh, unfortunately doesn't get the attention it deserves as far as the investigation is considered.",
            "The 'Safe Street Movement', initiated by some school students, raised a massive awareness about road safety and traffic laws in Dhaka city.",
            "Surviving each semester in BUET requires a student to solve O(n!) puzzles, with more than 30 hours worktime required for 1.5 credit sessional courses."
    };

    private String currentText;
    private List<Label> charLabels = new ArrayList<>();
    private long startTime;
    private int totalKeyStrokes = 0, correctKeyStrokes = 0, wpm = 0, accuracy = 100, wordCount = 0;
    private boolean isRunning = false, isRaceFinished = false;
    private int previousInputLength = 0;
    private Timeline wpmTimer;
    private int consecutiveCorrectPresss = 0;
    private Transition currentComboAnim;

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
        consecutiveCorrectPresss = 0;
        if (comboLabel != null) {
            comboLabel.setOpacity(0.0);
        }
        stopWpmTimer();
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
        charLabels.clear();
        HBox currentWord = new HBox();
        for (int i = 0; i < currentText.length(); i++) {
            char c = currentText.charAt(i);
            Label charLabel = new Label(String.valueOf(c));
            charLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
            charLabel.setMinWidth(Label.USE_PREF_SIZE);

            charLabels.add(charLabel);
            currentWord.getChildren().add(charLabel);

            if (c == ' ') {
                targetTextFlow.getChildren().add(currentWord);
                currentWord = new HBox();
            }
        }
        if (!currentWord.getChildren().isEmpty()) {
            targetTextFlow.getChildren().add(currentWord);
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

        if (inputLength > currentText.length()) {
            inputField.setText(input.substring(0, currentText.length()));
            inputField.positionCaret(currentText.length());
            input = inputField.getText();
            inputLength = input.length();
        }

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
                consecutiveCorrectPresss++;
                
                if (consecutiveCorrectPresss > 0 && consecutiveCorrectPresss % 5 == 0) {
                    int multiplier = (consecutiveCorrectPresss / 5) + 1;
                    comboLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #e2b714; -fx-font-family: 'Courier New';");
                    showComboAnimation("Combo x" + multiplier + "! ", multiplier);
                }
            } else {
                SoundManager.getInstance().playWrong();
                consecutiveCorrectPresss = 0;
                hideComboAnimation();
            }
        }
        previousInputLength = inputLength;

        if (!isRunning) {
            startTime = System.currentTimeMillis();
            isRunning = true;
            startWpmTimer();
        }

        totalKeyStrokes++;
        int currentCorrectInInput = 0;

        for (int i = 0; i < charLabels.size(); i++) {
            Label l = charLabels.get(i);
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
            stopWpmTimer();
            inputField.setEditable(false);
            SoundManager.getInstance().playFinish();
            saveResult();
        }
    }

    private void resetHighlighting() {
        for (int i = 0; i < charLabels.size(); i++) {
            Label l = charLabels.get(i);
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
        } catch (Exception e) {
            // nothing
        }
    }

    private void updateStats(int charLength) {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 0) {
            wpm = (int) Math.ceil(((correctKeyStrokes / 5.0) / ((elapsed / 1000.0) / 60.0)));
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

    private void startWpmTimer() {
        stopWpmTimer();
        wpmTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (isRunning && !isRaceFinished) {
                int inputLength = inputField.getText().length();
                updateStats(inputLength);
            }
        }));
        wpmTimer.setCycleCount(Timeline.INDEFINITE);
        wpmTimer.play();
    }

    private void stopWpmTimer() {
        if (wpmTimer != null) {
            wpmTimer.stop();
            wpmTimer = null;
        }
    }

    private void showComboAnimation(String text, int multiplier) {
        if (currentComboAnim != null) {
            currentComboAnim.stop();
        }

        comboLabel.setText(text);
        comboLabel.setScaleX(0.2);
        comboLabel.setScaleY(0.2);
        comboLabel.setOpacity(0.0);
        //comboLabel.setTranslateY(-60);
        
        double targetScale = 1.0;
        
        ScaleTransition st = new ScaleTransition(Duration.millis(300), comboLabel);
        st.setToX(targetScale);
        st.setToY(targetScale);
        
        FadeTransition ftIn = new FadeTransition(Duration.millis(300), comboLabel);
        ftIn.setToValue(0.40);
        
        ParallelTransition ptIn = new ParallelTransition(comboLabel, st, ftIn);

        FadeTransition ftOut = new FadeTransition(Duration.millis(300), comboLabel);
        ftOut.setToValue(0.0);
        ftOut.setDelay(Duration.seconds(1));

        SequentialTransition seq = new SequentialTransition(ptIn, ftOut);
        currentComboAnim = seq;
        seq.play();
    }

    private void hideComboAnimation() {
        if (currentComboAnim != null) {
            currentComboAnim.stop();
        }
        if (comboLabel != null && comboLabel.getOpacity() > 0) {
            FadeTransition ft = new FadeTransition(Duration.millis(150), comboLabel);
            ft.setToValue(0.0);
            ft.play();
            currentComboAnim = ft;
        }
    }

    @FXML protected void goToProfile() { switchScene("profile-view.fxml", "User Profile"); }
    @FXML protected void menupage() { switchScene("menu-view.fxml", "Menu Page"); }
}
