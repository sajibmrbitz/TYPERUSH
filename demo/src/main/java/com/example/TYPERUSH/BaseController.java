package com.example.TYPERUSH;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;

public class BaseController {

    protected void switchScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent newScreen = loader.load(); // 1. Grab the new visual screen

            Stage stage = HelloApplication.getPrimaryStage();
            stage.setTitle(title);

            // 2. Keep the same Scene, just swap the inside contents!
            stage.getScene().setRoot(newScreen);

        } catch (IOException e) {
            System.err.println("Error loading scene: " + e.getMessage());
        }
    }
}