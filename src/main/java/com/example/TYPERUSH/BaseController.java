package com.example.TYPERUSH;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import javafx.stage.Stage;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseController {

    protected void switchScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent newScreen = loader.load(); // 1. Grab the new visual screen

            animateButtons(newScreen); // Animate all buttons on the new screen

            Stage stage = HelloApplication.getPrimaryStage();
            stage.setTitle(title);

            // 2. Keep the same Scene, just swap the inside contents!
            stage.getScene().setRoot(newScreen);

        } catch (IOException e) {
            System.err.println("Error loading scene: " + e.getMessage());
        }
    }
    private void animateButtons(Parent root) {
        List<ButtonBase> buttons = new ArrayList<>();
        findButtons(root, buttons);

        for (int i = 0; i < buttons.size(); i++) {
            ButtonBase btn = buttons.get(i);

            // Set initial state (moved down and transparent)
            btn.setTranslateY(40);
            btn.setOpacity(0.0);

            // Slide UP
            TranslateTransition tt = new TranslateTransition(Duration.millis(500), btn);
            tt.setToY(0);

            // Fade IN
            FadeTransition ft = new FadeTransition(Duration.millis(500), btn);
            ft.setToValue(1.0);

            ParallelTransition pt = new ParallelTransition(btn, tt, ft);
            // Stagger the animation timing for each button
            pt.setDelay(Duration.millis(120L * i));
            pt.play();
        }
    }

    private void findButtons(Parent parent, List<ButtonBase> buttons) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof ButtonBase) {
                buttons.add((ButtonBase) node);
            }
            if (node instanceof Parent) {
                findButtons((Parent) node, buttons);
            }
        }
    }
}