package com.example.TYPERUSH;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ProfileController extends BaseController {
    // Button style constants for active/inactive graph toggle
    private static final String BTN_ACTIVE =
            "-fx-background-color: #00E5FF; -fx-border-color: #00E5FF; -fx-border-width: 1; " +
                    "-fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #0D1117; " +
                    "-fx-font-weight: bold; -fx-padding: 6 18; -fx-cursor: hand;";
    private static final String BTN_INACTIVE =
            "-fx-background-color: rgba(22,27,34,0.85); -fx-border-color: #444c56; -fx-border-width: 1; " +
                    "-fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #555e68; " +
                    "-fx-padding: 6 18; -fx-cursor: hand;";




    @FXML private TableView<RaceResult> historyTable;
    @FXML private TableColumn<RaceResult, String> dateCol;
    @FXML private TableColumn<RaceResult, Integer> wpmCol, accCol, wordsCol;
    @FXML private TableColumn<RaceResult, Double> timeCol;

    @FXML private StackPane mainGraphContainer;
    @FXML private ToggleButton btnShowAcc, btnShowWpm;
    private LineChart<String, Number> accChart;
    private LineChart<String, Number> wpmChart;
    private ToggleGroup graphToggleGroup;

    @FXML private HBox activityGridContainer;

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

        buildActivityGrid(allResults);

        dateCol.setSortType(TableColumn.SortType.ASCENDING);
        historyTable.getSortOrder().add(dateCol);
        historyTable.sort();
        Platform.runLater(() -> {
            ScrollPane sp = (ScrollPane) historyTable.getScene().lookup("ScrollPane");
            if (sp != null) {
                sp.getContent().setOnScroll(event -> {
                    double deltaY = event.getDeltaY() * 3;
                    double height = sp.getContent().getBoundsInLocal().getHeight();
                    double vvalue = sp.getVvalue();
                    sp.setVvalue(vvalue - deltaY / height);
                });
            }
        });
    }
    @FXML protected void showAccuracyGraph() {
        if (mainGraphContainer != null && accChart != null) {
            mainGraphContainer.getChildren().clear();
            mainGraphContainer.getChildren().add(accChart);
        }
        if (btnShowAcc != null) btnShowAcc.setStyle(BTN_ACTIVE);
        if (btnShowWpm != null) btnShowWpm.setStyle(BTN_INACTIVE);
    }

    @FXML protected void showWpmGraph() {
        if (mainGraphContainer != null && wpmChart != null) {
            mainGraphContainer.getChildren().clear();
            mainGraphContainer.getChildren().add(wpmChart);
        }
        if (btnShowWpm != null) btnShowWpm.setStyle(BTN_ACTIVE);
        if (btnShowAcc != null) btnShowAcc.setStyle(BTN_INACTIVE);
    }

    private void buildActivityGrid(List<RaceResult> allResults) {
        if (activityGridContainer == null) return;
        activityGridContainer.getChildren().clear();

        Map<LocalDate, Integer> dailyCounts = new HashMap<>();
        for (RaceResult r : allResults) {
            try {
                LocalDate d = LocalDate.parse(r.getDateTime().substring(0, 10));
                dailyCounts.put(d, dailyCounts.getOrDefault(d, 0) + 1);
            } catch (Exception e) {

            }
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(364);

        int daysFromSunday = startDate.getDayOfWeek().getValue() % 7;
        startDate = startDate.minusDays(daysFromSunday);

        LocalDate current = startDate;

        while (!current.isAfter(endDate) || current.getDayOfWeek().getValue() % 7 != 0) {
            VBox weekCol = new VBox(4);
            for (int i = 0; i < 7; i++) {
                if (current.isAfter(endDate)) {
                    Rectangle rx = new Rectangle(12, 12, Color.TRANSPARENT);
                    weekCol.getChildren().add(rx);
                } else {
                    int count = dailyCounts.getOrDefault(current, 0);
                    Color c = getColorForCount(count);

                    Rectangle rect = new Rectangle(12, 12, c);
                    rect.setArcWidth(4);
                    rect.setArcHeight(4);

                    Tooltip tip = new Tooltip(count + " races on " + current.toString());
                    Tooltip.install(rect, tip);

                    weekCol.getChildren().add(rect);
                }
                current = current.plusDays(1);
            }
            activityGridContainer.getChildren().add(weekCol);
        }
    }

    private Color getColorForCount(int count) {
        if (count == 0) return Color.web("#2d333b");
        if (count < 3) return Color.web("#544605");
        if (count < 6) return Color.web("#8a7106");
        if (count < 10) return Color.web("#bd9b08");
        return Color.web("#e2b714");
    }

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

        CategoryAxis xAxisAcc = new CategoryAxis(); NumberAxis yAxisAcc = new NumberAxis(0, 100, 10);
        accChart = new LineChart<>(xAxisAcc, yAxisAcc);
        accChart.setLegendVisible(false); XYChart.Series<String, Number> accSeries = new XYChart.Series<>();


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