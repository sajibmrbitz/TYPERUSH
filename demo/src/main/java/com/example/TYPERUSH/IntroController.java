package com.example.TYPERUSH;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.io.IOException;
import java.util.Random;

public class IntroController {

    @FXML private StackPane rootPane;
    @FXML private Label promptLabel;

    private Random random = new Random();
    private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ<>/{}[]#$01";

    @FXML
    public void initialize() {
        // 1. Generate 40 floating letters in the background
        for (int i = 0; i < 40; i++) {
            spawnFloatingLetter();
        }

        // 2. Make the "Press Any Key" text pulse
        FadeTransition pulse = new FadeTransition(Duration.seconds(1.0), promptLabel);
        pulse.setFromValue(1.0);
        pulse.setToValue(0.3);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        // 3. Listen for ANY key press to fade out and switch scenes
        javafx.application.Platform.runLater(() -> {
            rootPane.getScene().setOnKeyPressed(event -> {

                // Disable further key presses so the transition doesn't trigger twice
                rootPane.getScene().setOnKeyPressed(null);

                // Create the fade-out animation for the entire screen
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.8), rootPane);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);

                // Switch to the login scene AFTER the fade finishes
                fadeOut.setOnFinished(e -> {
                    try {
                        HelloApplication.showLoginScene();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                // Start the fade out animation
                fadeOut.play();
            });
        });
    }

    private void spawnFloatingLetter() {
        char c = alphabet.charAt(random.nextInt(alphabet.length()));
        Text letter = new Text(String.valueOf(c));

        // 1. INCREASED SIZE: Now picks a random size between 35 and 75
        letter.setFont(Font.font("Courier New", random.nextInt(40) + 65));

        letter.setFill(Color.web("#888888"));

        // 2. INCREASED OPACITY: Now picks an opacity between 0.3 and 0.8
        letter.setOpacity(random.nextDouble() * 0.5 + 0.3);

        letter.setEffect(new GaussianBlur(random.nextDouble() * 5));

        // Add to pane so it sits behind the label
        rootPane.getChildren().add(0, letter);

        letter.setTranslateX(random.nextInt(1200) - 600);
        letter.setTranslateY(random.nextInt(800) - 400);

        TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(random.nextInt(10) + 10), letter);
        floatAnim.setByY(-800);
        floatAnim.setByX(random.nextInt(100) - 50);
        floatAnim.setCycleCount(Animation.INDEFINITE);

        floatAnim.jumpTo(Duration.seconds(random.nextDouble() * 10));
        floatAnim.play();
    }
}