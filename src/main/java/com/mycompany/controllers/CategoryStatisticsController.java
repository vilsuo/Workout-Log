
package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.charts.HoverBarChart;
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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

/*
TODO

- highligh bars on hower
- implment choicebox to show selected categories

- draw graph?
- show piechart (about what?)
*/

public class CategoryStatisticsController {
    
    @FXML private BorderPane borderPane;
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);

    private BarChart barChart;
    
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    
    @FXML private Button calculateButton;
    
    @FXML private ChoiceBox choiceBox;
    
    public void initialize() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
        barChart = new HoverBarChart<String, Number>(xAxis, yAxis);
        
        borderPane.setCenter(barChart);
        
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
                
                XYChart.Data<String, Number> dataPoint = new XYChart.Data(dateString, totalSets);
                series.getData().add(dataPoint);
                
            }
            
            barChart.getData().add(series);
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
}
