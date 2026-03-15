package com.example.TYPERUSH;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.InetAddress;

public class MultiplayerController extends BaseController {

    @FXML private TextField ipInputField;
    @FXML private TextField nameField;   // ← NEW: name input field
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        try {
            String myIP = InetAddress.getLocalHost().getHostAddress();
            statusLabel.setText(myIP);
        } catch (Exception e) {
            statusLabel.setText("Could not get IP");
        }
    }

    @FXML
    protected void onHostClicked() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            nameField.setPromptText("⚠ Enter your name first!");
            return; // ← don't proceed if name is empty
        }

        GameSession.isHost = true;
        GameSession.localPlayerName = name; // ← use typed name
        switchScene("multiplayer-game-view.fxml", "TypeRush - Hosting Game");
    }

    @FXML
    protected void onJoinClicked() {
        String name = nameField.getText().trim();
        String friendIP = ipInputField.getText().trim();

        if (name.isEmpty()) {
            nameField.setPromptText("⚠ Enter your name first!");
            return; // ← don't proceed if name is empty
        }
        if (friendIP.isEmpty()) {
            return;
        }

        GameSession.isHost = false;
        GameSession.joinIp = friendIP;
        GameSession.localPlayerName = name; // ← use typed name
        switchScene("multiplayer-game-view.fxml", "TypeRush - Joining Game");
    }

    @FXML
    protected void backToMenu() {
        switchScene("menu-view.fxml", "TypeRush - Main Menu");
    }
}