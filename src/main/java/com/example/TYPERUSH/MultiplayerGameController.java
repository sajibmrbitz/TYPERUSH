package com.example.TYPERUSH;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;

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
    private boolean isRaceFinished = false;
    private boolean isRunning = false;
    private long startTime;
    private int totalKeyStrokes = 0, correctKeyStrokes = 0;
    private int lastWpm = 0, lastAcc = 100;
    private int previousInputLength = 0;

    @FXML
    public void initialize() {
        previousInputLength = 0;
        inputField.setEditable(false);
        myNameLabel.setText(GameSession.localPlayerName);

        if (GameSession.isHost) {
            opponentStatusLabel.setText("Waiting for opponent to join...");
            currentText = "The quick brown fox jumps over the lazy dog in a real time multiplayer race.";
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
        }

        totalKeyStrokes++;

        int prefixMatch = 0;
        boolean hasError = false;

        for (int i = 0; i < targetTextFlow.getChildren().size(); i++) {
            Label l = (Label) targetTextFlow.getChildren().get(i);
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

        if (prefixMatch == currentText.length()) {
            if (!isRaceFinished) {
                isRaceFinished = true;
                GameSession.sendFinish();
                SoundManager.getInstance().playFinish();
                showResultOverlay(true);
            }
        }
    }

    @Override
    public void onParagraphReceived(String para) {
        Platform.runLater(() -> {
            currentText = para;
            opponentStatusLabel.setText(GameSession.opponentName);
            opponentStatusLabel.setStyle("-fx-text-fill: #e2b714;");
            inputField.setEditable(false);

            targetTextFlow.getChildren().clear();
            for (int i = 0; i < currentText.length(); i++) {
                Label charLabel = new Label(String.valueOf(currentText.charAt(i)));
                charLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 24px; -fx-font-family: 'Courier New';");
                targetTextFlow.getChildren().add(charLabel);
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

    @FXML
    protected void leaveMatch() {
        isRaceFinished = true;
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