package com.example.TYPERUSH;

import javafx.fxml.FXML;

public class MenuController extends BaseController {
    @FXML
    protected void tutorPart(){
        System.out.println("coming soon");
    }
    @FXML
    protected  void racing_part(){
        switchScene("game-view.fxml","TypeRush-Racing");
    }
    @FXML
    protected  void history_part(){switchScene("profile-view.fxml","Type history");}
}
