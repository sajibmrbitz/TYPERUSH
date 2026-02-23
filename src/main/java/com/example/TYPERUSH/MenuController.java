package com.example.TYPERUSH;

import javafx.fxml.FXML;

public class MenuController extends BaseController {

    @FXML
    protected void tutorPart() {
        // Tutor Mode e click korle difficulty select korar screen e niye jabe
        switchScene("tutor-levels-view.fxml", "Select Difficulty");
    }

    @FXML
    protected void racing_part() {
        // Direct racing e click korle standard (INTERMEDIATE) difficulty set hoye game shuru hobe
        GameController.setDifficulty("INTERMEDIATE");
        switchScene("game-view.fxml", "TypeRush - Racing");
    }

    @FXML
    protected void history_part() {
        // User history ba profile viewing
        switchScene("profile-view.fxml", "Type History");
    }
}