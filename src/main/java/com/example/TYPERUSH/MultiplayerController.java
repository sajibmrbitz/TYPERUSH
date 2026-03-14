package com.example.TYPERUSH;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.InetAddress;

public class MultiplayerController extends BaseController {

    @FXML private TextField ipInputField;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        try {
            String myIP = InetAddress.getLocalHost().getHostAddress();
            statusLabel.setText("Your IP Address: " + myIP);
        } catch (Exception e) {
            statusLabel.setText("Could not get IP");
        }
    }

    @FXML
    protected void onHostClicked() {
        // I clicked Host, so I am the Server
        GameSession.isHost = true;
        GameSession.localPlayerName = "Host Player";
        switchScene("multiplayer-game-view.fxml", "TypeRush - Hosting Game");
    }

    @FXML
    protected void onJoinClicked() {
        String friendIP = ipInputField.getText().trim();
        if (friendIP.isEmpty()) return;

        // I clicked Join, so I am the Client
        GameSession.isHost = false;
        GameSession.joinIp = friendIP;
        GameSession.localPlayerName = "Joiner Player";
        switchScene("multiplayer-game-view.fxml", "TypeRush - Joining Game");
    }

    @FXML
    protected void backToMenu() {
        switchScene("menu-view.fxml", "TypeRush - Main Menu");
    }
}