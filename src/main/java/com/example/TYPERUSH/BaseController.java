package com.example.TYPERUSH;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
public class BaseController {
    protected void switchScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            Stage stage = HelloApplication.getPrimaryStage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (IOException e) {
            System.out.println("Scene Error: " + e.getMessage());
        }
    }
}