package com.example.TYPERUSH;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.layout.HBox;

public class MultiplayerGameController extends BaseController implements ProgressListener {

    @FXML private StackPane myCarContainer, opponentCarContainer;
    @FXML private Label wpmLabel, accuracyLabel, opponentStatusLabel, opponentWpmLabel, opponentAccLabel;
    @FXML private Label myNameLabel;
    @FXML private TextFlow targetTextFlow;
    @FXML private TextField inputField;
    @FXML private Label countdownLabel;
    // overlay
    @FXML private StackPane resultOverlay;
    @FXML private Label resultIcon, resultTitle, resultMessage, resultWpm, resultAcc;

    private String currentText = "";
    private List<Label> charLabels = new ArrayList<>();
    private boolean isRaceFinished = false;
    private boolean isRunning = false;
    private long startTime;
    private int totalKeyStrokes = 0, correctKeyStrokes = 0;
    private int lastWpm = 0, lastAcc = 100;
    private int previousInputLength = 0;
    private Timeline wpmTimer;

    @FXML
    public void initialize() {
        previousInputLength = 0;
        inputField.setEditable(false);
        myNameLabel.setText(GameSession.localPlayerName);

        if (GameSession.isHost) {
            opponentStatusLabel.setText("Waiting for opponent to join...");
            currentText = "As a final act of love, I will never reach you out again. But I will become everything I told you about. I won't chase you. I won't beg for you a closure. Instead, I will pour all that love into myself. I'll build the life which I promised I'll build with you. And maybe one day, you'll hear my name and you'll realize what walked away from you.";
            GameSession.server = new GameServer(currentText, this);
            GameSession.server.start();
        }
        else {
            opponentStatusLabel.setText("Connecting to host...");
            GameSession.client = new GameClient(GameSession.joinIp, this);
            GameSession.client.start();
        }
    }

    @FXML
    protected void handleTyping() {
        if (isRaceFinished) return;

        String input = inputField.getText();
        int inputLength = input.length();

        if (inputLength > currentText.length()) {
            inputField.setText(input.substring(0, currentText.length()));
            inputField.positionCaret(currentText.length());
            input = inputField.getText();
            inputLength = input.length();
        }

        if(inputLength == 0){
            previousInputLength = 0;
        }

        if(inputLength > previousInputLength && inputLength <= currentText.length()){
            char typedChar = input.charAt(inputLength - 1);
            char targetChar = currentText.charAt(inputLength - 1);

            if(typedChar == targetChar){
                SoundManager.getInstance().playCorrect();
            }
            else{
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
        boolean hasError = false;

        for (int i = 0; i < charLabels.size(); i++) {
            Label l = charLabels.get(i);
            if (i < inputLength) {
                if (!hasError && input.charAt(i) == currentText.charAt(i)) {
                    l.setStyle("-fx-background-color: rgba(46, 204, 113, 0.3); -fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
                    prefixMatch++;
                } else {
                    l.setStyle("-fx-background-color: rgba(255, 71, 87, 0.4); -fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
                    hasError = true;
                }
            } else {
                l.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
            }
        }

        correctKeyStrokes = prefixMatch;
        double myRatio = (double) correctKeyStrokes / currentText.length();
        myCarContainer.setLayoutX(40.0 + (myRatio * 1000.0));

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 0) {
            lastWpm = (int) ((correctKeyStrokes / 5.0) / ((elapsed / 1000.0) / 60.0));
            lastAcc = (int) (((double) correctKeyStrokes / totalKeyStrokes) * 100);
            wpmLabel.setText("WPM: " + lastWpm);
            accuracyLabel.setText("Accuracy: " + (lastAcc > 100 ? 100 : lastAcc) + "%");
        }

        GameSession.sendStats(myRatio, lastWpm, lastAcc);

        if (inputLength >= currentText.length()) {
            if (!isRaceFinished) {
                isRaceFinished = true;
                stopWpmTimer();
                GameSession.sendFinish();
                SoundManager.getInstance().playFinish();
                showResultOverlay(true);
            }
        }
    }

    @Override
    public void onParagraphReceived(String para) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    @Override
    public void onOpponentStats(double progress, int wpm, int accuracy) {
        Platform.runLater(() -> {
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
                showResultOverlay(false);
            }
        });
    }

    @Override
    public void onOpponentLeft() {
        Platform.runLater(() -> {
            if (!isRaceFinished) {
                isRaceFinished = true;
                resultIcon.setText("🚪");
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
            if (!isRaceFinished) {
                opponentStatusLabel.setText("Error: " + message);
            }
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
                    });
                } catch (InterruptedException e) {}
            }).start();
        });
    }

    private void showResultOverlay(boolean iWon) {
        inputField.setEditable(false);

        if (iWon) {
            resultIcon.setText("🏆");
            resultTitle.setText("YOU WON!");
            resultTitle.setStyle("-fx-font-size: 52px; -fx-font-weight: bold; -fx-text-fill: #00E5FF;");
            resultMessage.setText("Congratulations " + GameSession.localPlayerName + "! You finished first!");
        } else {
            resultIcon.setText("❌");
            resultTitle.setText("YOU LOST!");
            resultTitle.setStyle("-fx-font-size: 52px; -fx-font-weight: bold; -fx-text-fill: #E2B714;");
            resultMessage.setText(GameSession.opponentName + " finished first. Better luck next time!");
        }

        resultWpm.setText(String.valueOf(lastWpm));
        resultAcc.setText((lastAcc > 100 ? 100 : lastAcc) + "%");
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
                    accuracyLabel.setText("Accuracy: " + (lastAcc > 100 ? 100 : lastAcc) + "%");
                    double myRatio = (double) correctKeyStrokes / currentText.length();
                    GameSession.sendStats(myRatio, lastWpm, lastAcc);
                }
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

    @FXML
    protected void leaveMatch() {
        isRaceFinished = true;
        stopWpmTimer();
        GameSession.sendLeave();

        new Thread(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            Platform.runLater(() -> {
                GameSession.disconnect();
                switchScene("menu-view.fxml", "TypeRush - Menu");
            });
        }).start();
    }
}