package com.example.TYPERUSH;

import javafx.fxml.FXML;

public class MenuController extends BaseController {

    @FXML
    protected void tutorPart() {
        switchScene("tutor-levels-view.fxml", "Select Difficulty");
    }

    @FXML
    protected void racing_part() {
        GameController.setDifficulty("INTERMEDIATE");
        GameController.setTutorMode(false);
        switchScene("game-view.fxml", "TypeRush - Racing");
    }

    @FXML
    protected void history_part() {
        switchScene("profile-view.fxml", "Type History");
    }

    @FXML
    protected void multiplayer_part() {
        switchScene("multiplayer-setup-view.fxml", "TypeRush - Multiplayer Lobby");
    }
    @FXML
    protected void aboutPage() {
        switchScene("aboutpage.fxml", "TypeRush - aboutpage");
    }
    @FXML

    protected void backToMenu() {
        switchScene("menu-view.fxml", "TypeRush - Main Menu");
    }
    @FXML
    protected void openIfatFacebook() {
        try {
            java.awt.Desktop.getDesktop().browse(
                    new java.net.URI("https://web.facebook.com/mahtab.hasan.ifat/")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void openSajibFacebook() {
        try {
            java.awt.Desktop.getDesktop().browse(
                    new java.net.URI("https://www.facebook.com/madvirus.sajib.991")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void openGithubRepo() {
        try {
            java.awt.Desktop.getDesktop().browse(
                    new java.net.URI("https://github.com/sajibmrbitz/TYPERUSH")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}