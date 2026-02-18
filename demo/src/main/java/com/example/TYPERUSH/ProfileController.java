package com.example.TYPERUSH;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.util.Comparator;

public class ProfileController {
    @FXML private TableView<RaceResult> historyTable;
    @FXML private TableColumn<RaceResult, String> dateCol;
    @FXML private TableColumn<RaceResult, Integer> wpmCol, accCol, wordsCol;
    @FXML private TableColumn<RaceResult, Double> timeCol;

    private ObservableList<RaceResult> data;

    @FXML public void initialize() {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        wpmCol.setCellValueFactory(new PropertyValueFactory<>("wpm"));
        accCol.setCellValueFactory(new PropertyValueFactory<>("accuracy"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeSeconds"));
        wordsCol.setCellValueFactory(new PropertyValueFactory<>("wordCount"));

        data = FXCollections.observableArrayList(UserManager.currentUser.getHistory());
        historyTable.setItems(data);
    }

    @FXML protected void sortHistory() {
        data.sort(Comparator.comparingInt(RaceResult::getWpm).reversed());
    }

    @FXML protected void backToGame() throws IOException { HelloApplication.showGameScene(); }
}