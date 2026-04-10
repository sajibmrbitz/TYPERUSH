package com.example.TYPERUSH;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.layout.HBox;

public class MultiplayerGameController extends BaseController implements ProgressListener {

    @FXML private StackPane myCarContainer, opponentCarContainer;
    @FXML private Label wpmLabel, accuracyLabel;
    @FXML private Label opponentStatusLabel, opponentWpmLabel, opponentAccLabel;
    @FXML private Label myNameLabel;
    @FXML private Label timerLabel;
    @FXML private TextFlow targetTextFlow;
    @FXML private TextField inputField;
    @FXML private Label countdownLabel;
    @FXML private StackPane resultOverlay;
    @FXML private Label resultIcon, resultTitle, resultMessage, resultWpm, resultAcc;
    @FXML private StackPane freezeWarning;

    private static final int MATCH_SECONDS   = 150;
    private static final int MAX_WRONG_CHARS = 10;

    private String currentText = "";
    private List<Label> charLabels = new ArrayList<>();
    private boolean isRaceFinished  = false;
    private boolean isRunning       = false;
    private boolean isFrozen        = false;
    private boolean matchTimerStarted = false;

    private long startTime;
    private int totalKeyStrokes   = 0;
    private int correctKeyStrokes = 0;
    private int lastWpm           = 0;
    private int lastAcc           = 100;
    private int previousInputLength = 0;

    private int wrongCharCount = 0;

    private double myProgress       = 0.0;
    private double opponentProgress = 0.0;

    private Timeline wpmTimer;
    private Timeline matchTimer;
    private int secondsLeft = MATCH_SECONDS;

    @FXML
    public void initialize() {
        previousInputLength = 0;
        inputField.setEditable(false);
        myNameLabel.setText(GameSession.localPlayerName);
        updateTimerLabel(MATCH_SECONDS);

        // Key filter: when frozen only allow Backspace/Delete through
        inputField.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (isFrozen) {
                if (event.getCode() != KeyCode.BACK_SPACE && event.getCode() != KeyCode.DELETE) {
                    event.consume();
                }
            }
        });

        if (GameSession.isHost) {
            opponentStatusLabel.setText("Waiting for opponent to join...");
            currentText = "As a final act of love, I will never reach you out again. But I will become everything I told you about. " +
                    "I won't chase you. I won't beg for you a closure. Instead, I will pour all that love into myself. " +
                    "I'll build the life which I promised I'll build with you. And maybe one day, you'll hear my name " +
                    "and you'll realize what walked away from you.";
            GameSession.server = new GameServer(currentText, this);
            GameSession.server.start();
        } else {
            opponentStatusLabel.setText("Connecting to host...");
            GameSession.client = new GameClient(GameSession.joinIp, this);
            GameSession.client.start();
        }
    }

    @FXML
    protected void handleTyping() {
        if (isRaceFinished) return;

        String input    = inputField.getText();
        int inputLength = input.length();

        // Hard cap
        if (inputLength > currentText.length()) {
            inputField.setText(input.substring(0, currentText.length()));
            inputField.positionCaret(currentText.length());
            input       = inputField.getText();
            inputLength = input.length();
        }

        if (inputLength == 0) {
            previousInputLength = 0;
            wrongCharCount      = 0;
            unfreeze();
            resetHighlighting();
            return;
        }


        if (isFrozen && inputLength >= previousInputLength) {
            return;
        }


        if (inputLength > previousInputLength && inputLength <= currentText.length()) {
            char typed  = input.charAt(inputLength - 1);
            char target = currentText.charAt(inputLength - 1);
            if (typed == target) {
                SoundManager.getInstance().playCorrect();
            } else {
                SoundManager.getInstance().playWrong();
            }
        }
        previousInputLength = inputLength;

        if (!isRunning && inputLength > 0) {
            startTime = System.currentTimeMillis();
            isRunning = true;
            startWpmTimer();
        }

        totalKeyStrokes++;


        int prefixMatch = 0;
        int redCount    = 0;
        boolean hasError = false;

        for (int i = 0; i < charLabels.size(); i++) {
            Label l = charLabels.get(i);
            if (i < inputLength) {
                if (!hasError && input.charAt(i) == currentText.charAt(i)) {
                    l.setStyle("-fx-background-color: rgba(46,204,113,0.3); -fx-text-fill: white; " +
                            "-fx-font-size: 24px; -fx-font-family: 'Courier New';");
                    prefixMatch++;
                } else {
                    l.setStyle("-fx-background-color: rgba(255,71,87,0.4); -fx-text-fill: white; " +
                            "-fx-font-size: 24px; -fx-font-family: 'Courier New';");
                    hasError = true;
                    redCount++;
                }
            } else {
                l.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; " +
                        "-fx-font-size: 24px; -fx-font-family: 'Courier New';");
            }
        }

        wrongCharCount    = redCount;
        correctKeyStrokes = prefixMatch;

        if (wrongCharCount >= MAX_WRONG_CHARS) {
            freeze();
        } else {
            unfreeze();
        }

        myProgress = (double) correctKeyStrokes / currentText.length();
        myCarContainer.setLayoutX(40.0 + (myProgress * 1000.0));

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 0) {
            lastWpm = (int) ((correctKeyStrokes / 5.0) / ((elapsed / 1000.0) / 60.0));
            lastAcc = (int) (((double) correctKeyStrokes / totalKeyStrokes) * 100);
            wpmLabel.setText("WPM: " + lastWpm);
            accuracyLabel.setText("Accuracy: " + Math.min(lastAcc, 100) + "%");
        }

        GameSession.sendStats(myProgress, lastWpm, lastAcc);

        // FIX 2: Only finish when ALL text is correctly typed (no wrong chars remaining)
        if (inputLength >= currentText.length() && wrongCharCount == 0 && !isRaceFinished) {
            isRaceFinished = true;
            stopWpmTimer();
            stopMatchTimer();
            GameSession.sendFinish();
            SoundManager.getInstance().playFinish();
            showResultOverlay(true, "Finished first!");
        }
    }

    private void freeze() {
        if (!isFrozen) {
            isFrozen = true;
            freezeWarning.setVisible(true);
        }
    }

    private void unfreeze() {
        if (isFrozen) {
            isFrozen = false;
            freezeWarning.setVisible(false);
        }
    }

    private void resetHighlighting() {
        for (Label l : charLabels) {
            l.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; " +
                    "-fx-font-size: 24px; -fx-font-family: 'Courier New';");
        }
    }

    private void startMatchTimer() {
        stopMatchTimer();
        secondsLeft = MATCH_SECONDS;
        matchTimerStarted = true;
        matchTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            updateTimerLabel(secondsLeft);

            if (secondsLeft <= 30) {
                timerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ff4757;");
            }

            if (secondsLeft <= 0) {
                stopMatchTimer();
                stopWpmTimer();
                if (!isRaceFinished) {
                    isRaceFinished = true;
                    timeUpResolution();
                }
            }
        }));
        matchTimer.setCycleCount(MATCH_SECONDS);
        matchTimer.play();
    }

    private void stopMatchTimer() {
        if (matchTimer != null) { matchTimer.stop(); matchTimer = null; }
    }

    private void updateTimerLabel(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        timerLabel.setText(String.format("%d:%02d", m, s));
    }

    private void timeUpResolution() {
        GameSession.sendStats(myProgress, lastWpm, lastAcc);
        Platform.runLater(() -> {
            inputField.setEditable(false);
            boolean iWin = myProgress > opponentProgress;
            String reason = iWin
                    ? "Time's up! You had more progress (" + (int)(myProgress * 100) + "% vs " + (int)(opponentProgress * 100) + "%)"
                    : "Time's up! Opponent had more progress (" + (int)(opponentProgress * 100) + "% vs " + (int)(myProgress * 100) + "%)";

            if (Math.abs(myProgress - opponentProgress) < 0.001) {
                resultIcon.setText("\uD83E\uDD1D");
                resultTitle.setText("IT'S A DRAW!");
                resultTitle.setStyle("-fx-font-size: 52px; -fx-font-weight: bold; -fx-text-fill: #E2B714;");
                resultMessage.setText("Time's up! Both of you had equal progress.");
                resultWpm.setText(String.valueOf(lastWpm));
                resultAcc.setText(Math.min(lastAcc, 100) + "%");
                resultOverlay.setVisible(true);
            } else {
                showResultOverlay(iWin, reason);
            }
        });
    }

    @Override
    public void onParagraphReceived(String para) {
        Platform.runLater(() -> {
            currentText = para;
            opponentStatusLabel.setText(GameSession.opponentName);
            opponentStatusLabel.setStyle("-fx-text-fill: #e2b714;");
            inputField.setEditable(false);

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
        });
    }

    @Override
    public void onOpponentStats(double progress, int wpm, int accuracy) {
        Platform.runLater(() -> {
            opponentProgress = progress;
            opponentCarContainer.setLayoutX(40.0 + (progress * 1000.0));
            opponentWpmLabel.setText("WPM: " + wpm);
            opponentAccLabel.setText("Accuracy: " + accuracy + "%");
        });
    }

    @Override
    public void onRaceFinished(String winnerName) {
        Platform.runLater(() -> {
            if (!isRaceFinished) {
                isRaceFinished = true;
                stopWpmTimer();
                stopMatchTimer();
                showResultOverlay(false, winnerName + " finished first!");
            }
        });
    }

    @Override
    public void onOpponentLeft() {
        Platform.runLater(() -> {
            if (!isRaceFinished) {
                isRaceFinished = true;
                stopMatchTimer();
                resultIcon.setText("\uD83D\uDEAA");
                resultTitle.setText("OPPONENT LEFT");
                resultTitle.setStyle("-fx-font-size: 52px; -fx-font-weight: bold; -fx-text-fill: #ff4757;");
                resultMessage.setText(GameSession.opponentName + " has left the match.");
                resultWpm.setText("-");
                resultAcc.setText("-");
                resultOverlay.setVisible(true);
                GameSession.disconnect();
            }
        });
    }

    @Override
    public void onError(String message) {
        Platform.runLater(() -> {
            if (!isRaceFinished) opponentStatusLabel.setText("Error: " + message);
        });
    }

    @Override
    public void onCountdownStart() {
        Platform.runLater(() -> {
            countdownLabel.setVisible(true);
            countdownLabel.setText("3");

            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(() -> countdownLabel.setText("2"));
                    Thread.sleep(1000);
                    Platform.runLater(() -> countdownLabel.setText("1"));
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        countdownLabel.setText("GO!");
                        countdownLabel.setStyle("-fx-font-size: 90px; -fx-font-weight: bold; -fx-text-fill: #e2b714;");
                    });
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        countdownLabel.setVisible(false);
                        inputField.setEditable(true);
                        startMatchTimer();
                    });
                } catch (InterruptedException ignored) {}
            }).start();
        });
    }

    private void showResultOverlay(boolean iWon, String message) {
        inputField.setEditable(false);
        if (iWon) {
            resultIcon.setText("\uD83C\uDFC6");
            resultTitle.setText("YOU WON!");
            resultTitle.setStyle("-fx-font-size: 52px; -fx-font-weight: bold; -fx-text-fill: #00E5FF;");
        } else {
            resultIcon.setText("\u274C");
            resultTitle.setText("YOU LOST!");
            resultTitle.setStyle("-fx-font-size: 52px; -fx-font-weight: bold; -fx-text-fill: #E2B714;");
        }
        resultMessage.setText(message);
        resultWpm.setText(String.valueOf(lastWpm));
        resultAcc.setText(Math.min(lastAcc, 100) + "%");
        resultOverlay.setVisible(true);
    }

    private void startWpmTimer() {
        stopWpmTimer();
        wpmTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (isRunning && !isRaceFinished) {
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed > 0) {
                    lastWpm = (int) ((correctKeyStrokes / 5.0) / ((elapsed / 1000.0) / 60.0));
                    wpmLabel.setText("WPM: " + lastWpm);
                    accuracyLabel.setText("Accuracy: " + Math.min(lastAcc, 100) + "%");
                    GameSession.sendStats(myProgress, lastWpm, lastAcc);
                }
            }
        }));
        wpmTimer.setCycleCount(Timeline.INDEFINITE);
        wpmTimer.play();
    }

    private void stopWpmTimer() {
        if (wpmTimer != null) { wpmTimer.stop(); wpmTimer = null; }
    }

    @FXML
    protected void leaveMatch() {
        isRaceFinished = true;
        stopWpmTimer();
        stopMatchTimer();
        GameSession.sendLeave();
        new Thread(() -> {
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> {
                GameSession.disconnect();
                switchScene("menu-view.fxml", "TypeRush - Menu");
            });
        }).start();

    }
}