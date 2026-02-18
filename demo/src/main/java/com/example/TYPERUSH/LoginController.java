package com.example.TYPERUSH;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML protected void handleLogin() throws IOException {
        if (UserManager.login(usernameField.getText(), passwordField.getText())) {
            HelloApplication.showGameScene();
        } else { statusLabel.setText("Invalid Credentials!"); }
    }

    @FXML protected void handleSignup() {
        if (UserManager.signup(usernameField.getText(), passwordField.getText())) {
            statusLabel.setStyle("-fx-text-fill: #2ecc71;");
            statusLabel.setText("Signup Success! Now Login.");
        } else { statusLabel.setText("User already exists!"); }
    }
}