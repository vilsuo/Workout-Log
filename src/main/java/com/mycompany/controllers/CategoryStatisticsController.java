
package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Exercise;
import com.mycompany.domain.Workout;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        LocalDate startLocalDate = startDatePicker.getValue();
        LocalDate endLocalDate = endDatePicker.getValue();
        
        List<String> categoryList = manager.getAllExerciseInfoCategories();
        List<Workout> workoutList = manager.getWorkoutsBetweenDates(
            Date.valueOf(startLocalDate), Date.valueOf(endLocalDate)
        );
        
        Map<String, Map<String, Integer>> data = setUpData(workoutList);
        
        barChart.getData().clear();
        List<String> formattedLocalDateList = new ArrayList<>();
        
        
        for (String category : categoryList) {
            XYChart.Series series = new XYChart.Series();
            series.setName(category);
            
            Map<String, Integer> dateMap = data.get(category);
            for (String dateString : formattedLocalDateList) {
                if (dateMap != null) {
                    series.getData().add(
                        new XYChart.Data(
                            dateString,
                            dateMap.getOrDefault(dateString, 0)
                        )
                    );
                }
            }
            
            barChart.getData().add(series);
            //darkenSeriesOnHover(series);
        }
    }
    
    private Map<String, Map<String, Integer>> setUpData(List<Workout> workoutList) throws SQLException {
        Map<String, Map<String, Integer>> data = new HashMap<>();
        
        for (Workout workout : workoutList) {
            LocalDate workoutLocalDate = workout.getDate().toLocalDate();
            
            String dateString = formatLocalDateBasedOnChoice(workoutLocalDate);
            
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
    
    private String formatLocalDateBasedOnChoice(LocalDate localDate) {
        String choice = (String) choiceBox.getValue();
        
        String pattern = "";
        if (choice.equals("Week")) {
            pattern = "ww/YYYY";
            
        } else if (choice.equals("Month")) {
            pattern = "MM/yyyy";
        }
        
        return localDate.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    private List<String> getFormattedLocalDatesBetween(LocalDate startLocalDate, LocalDate endLocalDate) {
        List<String> formattedLocalDateList = new ArrayList<>();
        String choice = (String) choiceBox.getValue();
        
        String pattern = "";
        if (choice.equals("Week")) {
            pattern = "ww/YYYY";
            
            int i = 0;
            while (startLocalDate.plusWeeks(i).isBefore(endLocalDate)) {
                formattedLocalDateList.add(
                    startLocalDate.plusWeeks(i).format(
                        DateTimeFormatter.ofPattern(pattern)
                    )
                );
                ++i;
            }
        } else if (choice.equals("Month")) {
            pattern = "MM/yyyy";
            int i = 0;
            while (startLocalDate.plusWeeks(i).isBefore(endLocalDate)) {
                formattedLocalDateList.add(
                    startLocalDate.plusMonths(i).format(
                        DateTimeFormatter.ofPattern(pattern)
                    )
                );
                ++i;
            }
        }
        return formattedLocalDateList;
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
