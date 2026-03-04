package com.example.TYPERUSH;

import javafx.fxml.FXML;

public class TutorLevelController extends BaseController {

    @FXML
    protected void setBeginner() {
        GameController.setDifficulty("BEGINNER");
        startTutor();
    }

    @FXML
    protected void setIntermediate() {
        GameController.setDifficulty("INTERMEDIATE");
        startTutor();
    }

    @FXML
    protected void setPro() {
        GameController.setDifficulty("PRO");
        startTutor();
    }

    @FXML
    protected void back() {
        switchScene("menu-view.fxml", "TypeRush - Main Menu");
    }

    private void startTutor() {
        switchScene("game-view.fxml", "TypeRush - Typing Session");
    }
}