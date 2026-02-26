package com.example.TYPERUSH;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import java.util.Comparator;

public class ProfileController extends BaseController {
    @FXML private TableView<RaceResult> historyTable;
    @FXML private TableColumn<RaceResult, String> dateCol;
    @FXML private TableColumn<RaceResult, Integer> wpmCol, accCol, wordsCol;
    @FXML private TableColumn<RaceResult, Double> timeCol;

    // 1. Inject the StackPane from your FXML
    @FXML private StackPane graphContainer;

    @FXML public void initialize() {
        // Setup Table Columns
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        wpmCol.setCellValueFactory(new PropertyValueFactory<>("wpm"));
        accCol.setCellValueFactory(new PropertyValueFactory<>("accuracy"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeSeconds"));
        wordsCol.setCellValueFactory(new PropertyValueFactory<>("wordCount"));

        ObservableList<RaceResult> data = FXCollections.observableArrayList(UserManager.localHistory);
        historyTable.setItems(data);

        // 2. Call the new method to build the chart
        setupAccuracyChart(data);
    }

    private void setupAccuracyChart(ObservableList<RaceResult> data) {
        // Define the X and Y axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Race Number");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Accuracy (%)");

        // Optional: Force the Y-axis to scale between 0 and 100 for accuracy
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10);

        // Create the LineChart
        LineChart<String, Number> accuracyChart = new LineChart<>(xAxis, yAxis);
        accuracyChart.setTitle("Accuracy Progression");
        accuracyChart.setLegendVisible(false); // Hide legend since we only have one line

        // Create the data series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Accuracy over time");

        // Loop through the data to populate the chart
        int raceNumber = 1;
        for (RaceResult result : data) {
            // X-axis: "1", "2", "3" (Keeps the axis clean compared to long date strings)
            // Y-axis: The accuracy integer
            series.getData().add(new XYChart.Data<>(String.valueOf(raceNumber), result.getAccuracy()));
            raceNumber++;
        }

        // Add data to the chart
        accuracyChart.getData().add(series);

        // Clear the placeholder label from FXML and add the new chart
        graphContainer.getChildren().clear();
        graphContainer.getChildren().add(accuracyChart);

        // Remove the dashed border now that the real chart is here
        graphContainer.setStyle("-fx-border-width: 0;");
    }

    @FXML protected void sortHistory() {
        historyTable.getItems().sort(Comparator.comparingInt(RaceResult::getWpm).reversed());
    }

    @FXML protected void backTo_menu() {
        switchScene("menu-view.fxml", "TypeRush Game");
    }
}