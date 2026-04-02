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
import java.util.List;

public class ProfileController extends BaseController {


    @FXML private TableView<RaceResult> historyTable;
    @FXML private TableColumn<RaceResult, String> dateCol;
    @FXML private TableColumn<RaceResult, Integer> wpmCol, accCol, wordsCol;
    @FXML private TableColumn<RaceResult, Double> timeCol;

    @FXML private StackPane mainGraphContainer;
    @FXML private ToggleButton btnShowAcc, btnShowWpm;
    private LineChart<String, Number> accChart;
    private LineChart<String, Number> wpmChart;
    private ToggleGroup graphToggleGroup;

    // Statistics Labels (All Time & Today)
    @FXML private Label allTimeTime, allTimeLessons, allTimeTopSpeed, allTimeAvgSpeed, allTimeTopAcc, allTimeAvgAcc;
    @FXML private Label todayTime, todayLessons, todayTopSpeed, todayAvgSpeed, todayTopAcc, todayAvgAcc;

    @FXML public void initialize() {

        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        wpmCol.setCellValueFactory(new PropertyValueFactory<>("wpm"));
        accCol.setCellValueFactory(new PropertyValueFactory<>("accuracy"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeSeconds"));
        wordsCol.setCellValueFactory(new PropertyValueFactory<>("wordCount"));


        graphToggleGroup = new ToggleGroup();
        if (btnShowAcc != null && btnShowWpm != null) {
            btnShowAcc.setToggleGroup(graphToggleGroup);
            btnShowWpm.setToggleGroup(graphToggleGroup);
            btnShowAcc.setSelected(true);
        }


        List<RaceResult> allResults = UserManager.getAllResults();
        List<RaceResult> todayResults = UserManager.getTodaysResults();

        loadData(allResults);

        calculateAndSetStats(allResults, true);
        calculateAndSetStats(todayResults, false);


        dateCol.setSortType(TableColumn.SortType.ASCENDING);
        historyTable.getSortOrder().add(dateCol);
        historyTable.sort();
    }

    // ==========================================
    // Graph Switching Logic
    // ==========================================
    @FXML protected void showAccuracyGraph() {
        if (mainGraphContainer != null && accChart != null) {
            mainGraphContainer.getChildren().clear();
            mainGraphContainer.getChildren().add(accChart);
        }
    }

    @FXML protected void showWpmGraph() {
        if (mainGraphContainer != null && wpmChart != null) {
            mainGraphContainer.getChildren().clear();
            mainGraphContainer.getChildren().add(wpmChart);
        }
    }

    // ==========================================
    // Statistics Calculation Logic
    // ==========================================
    private void calculateAndSetStats(List<RaceResult> results, boolean isAllTime) {
        int lessons = results.size();
        double totalTime = 0;
        int topSpeed = 0, topAcc = 0;
        double totalSpeed = 0, totalAcc = 0;

        for (RaceResult r : results) {
            totalTime += r.getTimeSeconds();
            if (r.getWpm() > topSpeed) topSpeed = r.getWpm();
            totalSpeed += r.getWpm();
            if (r.getAccuracy() > topAcc) topAcc = r.getAccuracy();
            totalAcc += r.getAccuracy();
        }

        String timeStr = formatTime(totalTime);
        String avgSpeed = lessons > 0 ? String.format("%.1fwpm", totalSpeed / lessons) : "0.0wpm";
        String topSpeedStr = topSpeed + "wpm";
        String avgAccStr = lessons > 0 ? String.format("%.1f%%", totalAcc / lessons) : "0.0%";
        String topAccStr = topAcc + "%";

        if (isAllTime) {
            allTimeTime.setText(timeStr); allTimeLessons.setText(String.valueOf(lessons));
            allTimeTopSpeed.setText(topSpeedStr); allTimeAvgSpeed.setText(avgSpeed);
            allTimeTopAcc.setText(topAccStr); allTimeAvgAcc.setText(avgAccStr);
        } else {
            todayTime.setText(timeStr); todayLessons.setText(String.valueOf(lessons));
            todayTopSpeed.setText(topSpeedStr); todayAvgSpeed.setText(avgSpeed);
            todayTopAcc.setText(topAccStr); todayAvgAcc.setText(avgAccStr);
        }
    }

    private String formatTime(double totalSeconds) {
        int hours = (int) totalSeconds / 3600;
        int minutes = (int) (totalSeconds % 3600) / 60;
        int secs = (int) totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    // ==========================================
    // Table and Data Loading Logic
    // ==========================================
    @FXML protected void showAllTimeData() { loadData(UserManager.getAllResults()); }
    @FXML protected void showTodayData() { loadData(UserManager.getTodaysResults()); }

    @FXML protected void sortByWPM() {
        wpmCol.setSortType(TableColumn.SortType.DESCENDING);
        historyTable.getSortOrder().clear();
        historyTable.getSortOrder().add(wpmCol);
        historyTable.sort();
    }

    private void loadData(List<RaceResult> resultList) {
        ObservableList<RaceResult> data = FXCollections.observableArrayList(resultList);
        historyTable.setItems(data);
        setupCharts(data);
        historyTable.sort();
    }

    private void setupCharts(ObservableList<RaceResult> data) {
        // ১. Accuracy Chart
        CategoryAxis xAxisAcc = new CategoryAxis(); NumberAxis yAxisAcc = new NumberAxis(0, 100, 10);
        accChart = new LineChart<>(xAxisAcc, yAxisAcc);
        accChart.setLegendVisible(false); XYChart.Series<String, Number> accSeries = new XYChart.Series<>();

        // ২. WPM Chart
        CategoryAxis xAxisWpm = new CategoryAxis(); NumberAxis yAxisWpm = new NumberAxis();
        wpmChart = new LineChart<>(xAxisWpm, yAxisWpm);
        wpmChart.setLegendVisible(false); XYChart.Series<String, Number> wpmSeries = new XYChart.Series<>();

        int raceNumber = 1;
        for (RaceResult result : data) {
            accSeries.getData().add(new XYChart.Data<>(String.valueOf(raceNumber), result.getAccuracy()));
            wpmSeries.getData().add(new XYChart.Data<>(String.valueOf(raceNumber), result.getWpm()));
            raceNumber++;
        }
        accChart.getData().add(accSeries); wpmChart.getData().add(wpmSeries);

        if (btnShowWpm != null && btnShowWpm.isSelected()) {
            showWpmGraph();
        } else {
            showAccuracyGraph();
        }
    }

    @FXML protected void backTo_menu() { switchScene("menu-view.fxml", "TypeRush Game"); }
}