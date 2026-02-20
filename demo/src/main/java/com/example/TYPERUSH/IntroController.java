package com.example.TYPERUSH;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.Random;

public class IntroController extends BaseController {
    @FXML private StackPane rootPane;
    @FXML private Label promptLabel;
    private Random random = new Random();

    @FXML
    public void initialize() {
        for (int i = 0; i < 40; i++) {
            spawnFloatingLetter();
        }

        FadeTransition pulse = new FadeTransition(Duration.seconds(1.0), promptLabel);
        pulse.setFromValue(1.0);
        pulse.setToValue(0.3);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        Platform.runLater(() -> {
            rootPane.getScene().setOnKeyPressed(event -> {
                rootPane.getScene().setOnKeyPressed(null);
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.8), rootPane);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> switchScene("menu-view.fxml", "TypeRush Game   "));
                fadeOut.play();
            });
        });
    }

    private void spawnFloatingLetter() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ<>/{}[]#$01";
        Text letter = new Text(String.valueOf(alphabet.charAt(random.nextInt(alphabet.length()))));
        letter.setFont(Font.font("Courier New", random.nextInt(40) + 65));
        letter.setFill(Color.web("#888888"));
        letter.setOpacity(random.nextDouble() * 0.5 + 0.3);
        letter.setEffect(new GaussianBlur(random.nextDouble() * 5));

        rootPane.getChildren().add(0, letter);
        letter.setTranslateX(random.nextInt(1200) - 600);
        letter.setTranslateY(random.nextInt(800) - 400);

        TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(random.nextInt(10) + 10), letter);
        floatAnim.setByY(-800);
        floatAnim.setCycleCount(Animation.INDEFINITE);
        floatAnim.play();
    }
}