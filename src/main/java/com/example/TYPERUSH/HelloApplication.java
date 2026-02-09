package com.example.TYPERUSH;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        UserManager.loadUsers();
        showLoginScene();
        stage.setTitle("TYPERUSH - Adaptive Typing Pro");
        stage.show();
    }

    public static void showLoginScene() throws IOException { updateScene("login-view.fxml"); }
    public static void showGameScene() throws IOException { updateScene("game-view.fxml"); }
    public static void showProfileScene() throws IOException { updateScene("profile-view.fxml"); }

    private static void updateScene(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(HelloApplication.class.getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(false);
        primaryStage.setMaximized(true); // Force Fullscreen
    }

    public static void main(String[] args) { launch(); }
}