
package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Exercise;
import com.mycompany.domain.Workout;
import com.mycompany.utilities.Statistics.CustomLocalDateFormatter;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/*
TODO

- highligh bars on hower
- implment choicebox to show selected categories

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
    
    @FXML private VBox categoryVBox;
    
    private final BooleanProperty needToRecalculate =
        new SimpleBooleanProperty(true);
    
    private Map<String, Boolean> selectedCategoriesMap = new HashMap<>();
    
    public void initialize() {
        initializeControls();
        setUpProperties();
        try {
            setUpCategoryCheckBoxes();
            
        } catch (SQLException e) {
            System.out.println(
                "Error in CategoryStatisticsController.setUpCategoryList(): " + e.getMessage()
            );
        }
    }
    
    private void initializeControls() {
        // temporary
        startDatePicker.setValue(LocalDate.of(2023, 3, 1));
        endDatePicker.setValue(LocalDate.now());
        
        ChangeListener cl = (obs, oldObj, newObj) -> {
            needToRecalculate.set(true);    
        };
        
        startDatePicker.valueProperty().addListener(cl);
        endDatePicker.valueProperty().addListener(cl);
        choiceBox.valueProperty().addListener(cl);
        
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
    
    private void setUpCategoryCheckBoxes() throws SQLException {
        List<String> categoryList = manager.getAllExerciseInfoCategories();
        Collections.sort(categoryList);
        
        for (String category : categoryList) {
            final CheckBox categoryCheckBox = new CheckBox(category);
            
            categoryCheckBox.selectedProperty().addListener(
                (obs, oldValue, newValue) -> {
                    selectedCategoriesMap.put(category, newValue);
                }
            );
            
            selectedCategoriesMap.put(category, false);
            categoryVBox.getChildren().add(categoryCheckBox);
        }
    }
    
    @FXML
    private void onCalculateButtonPressed() throws SQLException {
        createChart();
    }
    
    private void createChart() throws SQLException {
        LocalDate startLocalDate = startDatePicker.getValue();
        LocalDate endLocalDate = endDatePicker.getValue();
        
        List<Workout> workoutList = manager.getWorkoutsBetweenDates(
            Date.valueOf(startLocalDate), Date.valueOf(endLocalDate)
        );
        
        //<category, <date, #sets>>
        Map<String, Map<String, Integer>> data = setUpData(workoutList);
        
        List<String> formattedLocalDateList =
            CustomLocalDateFormatter.getFormattedLocalDatesBetween(
                startLocalDate, endLocalDate, (String) choiceBox.getValue()
            );
        
        ObservableList obsList = FXCollections.observableArrayList();
        for (String category : selectedCategoriesMap.keySet()) {
            // category is not selected
            if (!selectedCategoriesMap.getOrDefault(category, false)) {
                continue;
            }
            
            XYChart.Series series = new XYChart.Series();
            series.setName(category);
            
            Map<String, Integer> dateMap = data.get(category);
            for (String dateString : formattedLocalDateList) {
                int totalSets = 0;
                if (dateMap != null) {
                    // check if the given date period does not contain any sets
                    // of the given category
                    totalSets = dateMap.getOrDefault(dateString, 0);
                }
                
                series.getData().add(
                    new XYChart.Data(dateString, totalSets)
                );
            }
            
            obsList.add(series);
        }
        // this way there are no two animations going on at the same time
        // (clear data and set data)
        barChart.setData(obsList);
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
