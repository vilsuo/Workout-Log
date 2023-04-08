
package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Exercise;
import com.mycompany.domain.Workout;
import com.mycompany.utilities.Statistics.CustomLocalDateFormatter;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;

/*
TODO

- highligh bars on hower
- implment choicebox to show selected categories

- show ticks/labels correctly

- draw graph?
- show piechart (about what?)
*/

public class CategoryStatisticsController {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);

    @FXML private BarChart barChart;
    
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    
    @FXML private Button calculateButton;
    
    @FXML private ChoiceBox choiceBox;
    
    public void initialize() {
        setUpProperties();
        
        startDatePicker.setValue(LocalDate.of(2023, 3, 1));
        endDatePicker.setValue(LocalDate.now());
                
        final Callback<DatePicker, DateCell> dayCellFactory = 
            new Callback<DatePicker, DateCell>() {
                @Override
                public DateCell call(final DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            super.updateItem(item, empty);
                           
                            if (item.isBefore(startDatePicker.getValue())) {
                                setDisable(true);
                                setStyle("-fx-background-color: #ffc0cb;");
                            }
                        }
                    };
                }
            };
        endDatePicker.setDayCellFactory(dayCellFactory);
    }
    
    private void setUpProperties() {
        endDatePicker.disableProperty().bind(
            startDatePicker.valueProperty().isNull()
        );
        
        calculateButton.disableProperty().bind(
            endDatePicker.valueProperty().isNull()
        );
    }
    
    @FXML
    private void onCalculateButtonPressed() throws SQLException {
        createChart();
    }
    
    private void createChart() throws SQLException {
        barChart.getData().clear();
        
        LocalDate startLocalDate = startDatePicker.getValue();
        LocalDate endLocalDate = endDatePicker.getValue();
        
        List<String> categoryList = manager.getAllExerciseInfoCategories();
        List<Workout> workoutList = manager.getWorkoutsBetweenDates(
            Date.valueOf(startLocalDate), Date.valueOf(endLocalDate)
        );
        
        //<category, <date, #sets>>
        Map<String, Map<String, Integer>> data = setUpData(workoutList);
        List<String> formattedLocalDateList =
            CustomLocalDateFormatter.getFormattedLocalDatesBetween(
                startLocalDate, endLocalDate, (String) choiceBox.getValue()
            );
        
        for (String category : categoryList) {
            XYChart.Series series = new XYChart.Series();
            series.setName(category);
            
            Map<String, Integer> dateMap = data.get(category);
            for (String dateString : formattedLocalDateList) {
                int totalSets = 0;
                if (dateMap != null) {
                    totalSets = dateMap.getOrDefault(dateString, 0);
                }
                series.getData().add(
                    new XYChart.Data(
                        dateString,
                        totalSets
                    )
                );
            }
            
            barChart.getData().add(series);
            //darkenSeriesOnHover(series);
        }
    }
    
    private Map<String, Map<String, Integer>> setUpData(List<Workout> workoutList) throws SQLException {
        //<category, <date, #sets>>
        Map<String, Map<String, Integer>> data = new HashMap<>();
        
        for (Workout workout : workoutList) {
            LocalDate workoutLocalDate = workout.getDate().toLocalDate();
            String dateString = CustomLocalDateFormatter.formatLocalDate(
                workoutLocalDate, (String) choiceBox.getValue()
            );
            
            for (Exercise exercise : workout.getExerciseList()) {
                String category = exercise.getExerciseInfo().getCategory();
                
                int totalSets = exercise.getExerciseSetList().stream()
                    .mapToInt(exerciseSet -> exerciseSet.getWorkingSets())
                    .reduce(0, Integer::sum);
                
                data.putIfAbsent(category, new HashMap<>());
                
                data.get(category).put(
                    dateString,
                    data.get(category).getOrDefault(dateString, 0) + totalSets
                );
            }   
        }
        return data;
    }
    
    /*
    private void darkenSeriesOnHover(XYChart.Series<String, Number> series) {
        Node seriesNode = series.getNode();
        if (seriesNode != null && seriesNode instanceof Path){
            Path seriesPath = (Path) seriesNode;
            seriesPath.setOnMouseEntered(event -> {
                updatePath(seriesPath, ((Color) seriesPath.getStroke()).darker(), 2 * seriesPath.getStrokeWidth(), true);
            });
            seriesPath.setOnMouseExited(event -> {
                updatePath(seriesPath, ((Color) seriesPath.getStroke()).brighter(), 0.5 * seriesPath.getStrokeWidth(), false);
            });
        }
        
    }
    
    private void updatePath(Path seriesPath, Paint strokeColor, double strokeWidth, boolean toFront){
        seriesPath.setStroke(strokeColor);
        seriesPath.setStrokeWidth(strokeWidth);
        if (!toFront){
            return;
        }
        seriesPath.toFront();
    }
    */
}
