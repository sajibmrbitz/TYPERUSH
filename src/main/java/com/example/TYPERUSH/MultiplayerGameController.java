package com.example.TYPERUSH;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;

public class MultiplayerGameController extends BaseController implements ProgressListener {

    @FXML private StackPane myCarContainer, opponentCarContainer;
    @FXML private Label wpmLabel, accuracyLabel, opponentStatusLabel, opponentWpmLabel, opponentAccLabel;
    @FXML private TextFlow targetTextFlow;
    @FXML private TextField inputField;

    private String currentText = "";
    private boolean isRaceFinished = false;
    private boolean isRunning = false;
    private long startTime;
    private int totalKeyStrokes = 0, correctKeyStrokes = 0;

    @FXML
    public void initialize() {
        inputField.setEditable(false);

        if (GameSession.isHost) {
            opponentStatusLabel.setText("Waiting for opponent to join...");
            currentText = "The quick brown fox jumps over the lazy dog in a real time multiplayer race.";
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

        String input = inputField.getText();
        int inputLength = input.length();

        if (inputLength > currentText.length()) {
            inputField.setText(input.substring(0, currentText.length()));
            inputField.positionCaret(currentText.length());
            input = inputField.getText();
            inputLength = input.length();
        }

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

        int myWpm = 0; int myAcc = 100;
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 0) {
            myWpm = (int) ((correctKeyStrokes / 5.0) / ((elapsed / 1000.0) / 60.0));
            myAcc = (int) (((double) correctKeyStrokes / totalKeyStrokes) * 100);
            wpmLabel.setText("WPM: " + myWpm);
            accuracyLabel.setText("Accuracy: " + (myAcc > 100 ? 100 : myAcc) + "%");
        }

        GameSession.sendStats(myRatio, myWpm, myAcc);

        if (prefixMatch == currentText.length()) {
            if (!isRaceFinished) {
                isRaceFinished = true;
                GameSession.sendFinish();
                showWinScreen(true);
            }
        }
    }

    @Override
    public void onParagraphReceived(String para) {
        Platform.runLater(() -> {
            currentText = para;
            opponentStatusLabel.setText("Opponent: " + GameSession.opponentName);
            opponentStatusLabel.setStyle("-fx-text-fill: #e2b714;");
            inputField.setEditable(true);

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
                showWinScreen(false);
            }
        });
    }

    // --- NEW: Handle Opponent Leaving cleanly ---
    @Override
    public void onOpponentLeft() {
        Platform.runLater(() -> {
            if (!isRaceFinished) {
                // If they leave in the middle of a race, alert us before kicking us out!
                isRaceFinished = true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Match Ended");
                alert.setHeaderText(null);
                alert.setContentText("The opponent has left the match.");
                alert.showAndWait();

                GameSession.disconnect();
                switchScene("menu-view.fxml", "TypeRush - Menu");
            }
        });
    }

    @Override
    public void onError(String message) {
        Platform.runLater(() -> {
            if (!isRaceFinished) { // Mask the error from crashing JavaFX if we are just looking at the win screen!
                opponentStatusLabel.setText("Error: " + message);
            }
        });
    }

    private void showWinScreen(boolean iWon) {
        inputField.setEditable(false);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Race Finished!");
        alert.setHeaderText(null);

        if (iWon) {
            alert.setContentText("🏆 YOU WON THE RACE!");
        } else {
            alert.setContentText("❌ OPPONENT WON!");
        }

        alert.showAndWait();
        leaveMatch();
    }

    // --- UPDATED: Safely leave and trigger the opponent to leave too ---
    @FXML
    protected void leaveMatch() {
        isRaceFinished = true;
        GameSession.sendLeave(); // Tell opponent we are exiting

        // Wait 100ms on a background thread to ensure the network message sends before deleting the socket
        new Thread(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            Platform.runLater(() -> {
                GameSession.disconnect();
                switchScene("menu-view.fxml", "TypeRush - Menu");
            });
        }).start();
    }
}