package com.example.TYPERUSH;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Comparator;

public class ProfileController extends BaseController {
    @FXML private TableView<RaceResult> historyTable;
    @FXML private TableColumn<RaceResult, String> dateCol;
    @FXML private TableColumn<RaceResult, Integer> wpmCol, accCol, wordsCol;
    @FXML private TableColumn<RaceResult, Double> timeCol;

    @FXML public void initialize() {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        wpmCol.setCellValueFactory(new PropertyValueFactory<>("wpm"));
        accCol.setCellValueFactory(new PropertyValueFactory<>("accuracy"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeSeconds"));
        wordsCol.setCellValueFactory(new PropertyValueFactory<>("wordCount"));


        ObservableList<RaceResult> data = FXCollections.observableArrayList(UserManager.localHistory);
        historyTable.setItems(data);
    }
    @FXML protected void sortHistory() {
        historyTable.getItems().sort(Comparator.comparingInt(RaceResult::getWpm).reversed());
    }

    @FXML protected void backToGame() {
        switchScene("game-view.fxml", "TypeRush Game");
    }
}