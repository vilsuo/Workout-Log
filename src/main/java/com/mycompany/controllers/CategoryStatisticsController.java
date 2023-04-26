
package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Exercise;
import com.mycompany.domain.Workout;
import com.mycompany.utilities.Statistics.CustomLocalDateFormatter;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.util.Callback;

/*
TODO
- add select/deselect button for check boxes

- remove dublicate code from setUpBar/PieData

- implement zooming/scrolling to chart?

- make percentages to pie chart instead of actual number of sets
- add the piechart label to pop up in correct position
*/

public class CategoryStatisticsController {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);

    @FXML private BarChart barChart;
    @FXML private ScrollPane pieChartScrollPane;
    
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    
    @FXML private Button calculateButton;
    
    @FXML private ChoiceBox choiceBox;
    
    @FXML private ScrollPane categoryScrollPane;
    
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
        
        VBox vb = new VBox();
        vb.setSpacing(10);
        vb.setPadding(new Insets(10));
        
        for (String category : categoryList) {
            final CheckBox categoryCheckBox = new CheckBox(category);
            
            categoryCheckBox.selectedProperty().addListener(
                (obs, oldValue, newValue) -> {
                    selectedCategoriesMap.put(category, newValue);
                }
            );
            
            selectedCategoriesMap.put(category, false);
            vb.getChildren().add(categoryCheckBox);
        }
        categoryScrollPane.setContent(vb);
    }
    
    @FXML
    private void onCalculateButtonPressed() throws SQLException {
        createBarChart();
        createPieChart();
    }
    
    private void createBarChart() throws SQLException {
        LocalDate startLocalDate = startDatePicker.getValue();
        LocalDate endLocalDate = endDatePicker.getValue();
        
        List<Workout> workoutList = manager.getWorkoutsBetweenDates(
            Date.valueOf(startLocalDate), Date.valueOf(endLocalDate)
        );
        
        //<category, <date, #sets>>
        Map<String, Map<String, Integer>> data = setUpBarData(workoutList);
        
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
    
    private void createPieChart() throws SQLException {
        LocalDate startLocalDate = startDatePicker.getValue();
        LocalDate endLocalDate = endDatePicker.getValue();
        
        List<Workout> workoutList = manager.getWorkoutsBetweenDates(
            Date.valueOf(startLocalDate), Date.valueOf(endLocalDate)
        );
        
        //<date, <category, #sets>>
        Map<String, Map<String, Integer>> data = setUpPieData(workoutList);
        
        List<String> formattedLocalDateList =
            CustomLocalDateFormatter.getFormattedLocalDatesBetween(
                startLocalDate, endLocalDate, (String) choiceBox.getValue()
            );
        
        TilePane tilePane = new TilePane();
        for (String date : formattedLocalDateList) {
            ObservableList obsList = FXCollections.observableArrayList();
            
            int totalSetsInDateGroup = 0;
            Map<String, Integer> categoryMap = data.getOrDefault(date, new HashMap<>());
            for (String category : categoryMap.keySet()) {
                // category is not selected
                if (!selectedCategoriesMap.getOrDefault(category, false)) {
                    continue;
                }
                
                int nSets = categoryMap.getOrDefault(category, 0);
                totalSetsInDateGroup += nSets;
                obsList.add(new PieChart.Data(category, nSets));
            }
            
            PieChart chart = new PieChart(obsList);
            chart.setTitle(date);
            chart.setLegendVisible(false);
            
            final Label pieChartLabel = new Label();
            
            final Pane pane = new Pane();
            pane.getChildren().addAll(chart, pieChartLabel);
            
            final int totalSets = totalSetsInDateGroup;
            for (final PieChart.Data pieData : chart.getData()) {
                pieData.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    event -> {
                        if (totalSets != 0) {
                            pieChartLabel.setTranslateX(event.getX() + pane.getWidth() / 2);
                            pieChartLabel.setTranslateY(event.getY() + pane.getHeight() / 2);
                            
                            pieChartLabel.setText(
                                100 * pieData.getPieValue() / totalSets + "%"
                            );
                            
                            Arc arc = (Arc) pieData.getNode();
                        }
                        
                    }
                );
                
            }
            tilePane.getChildren().add(pane);
        }
        
        pieChartScrollPane.setContent(tilePane);
    }
    
    private Map<String, Map<String, Integer>> setUpBarData(List<Workout> workoutList) throws SQLException {
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
    
    private Map<String, Map<String, Integer>> setUpPieData(List<Workout> workoutList) throws SQLException {
        //<date, <category, #sets>>
        Map<String, Map<String, Integer>> data = new HashMap<>();
        
        for (Workout workout : workoutList) {
            LocalDate workoutLocalDate = workout.getDate().toLocalDate();
            String dateString = CustomLocalDateFormatter.formatLocalDate(
                workoutLocalDate, (String) choiceBox.getValue()
            );
            
            data.putIfAbsent(dateString, new HashMap<>());
            
            for (Exercise exercise : workout.getExerciseList()) {
                String category = exercise.getExerciseInfo().getCategory();
                
                int totalSets = exercise.getExerciseSetList().stream()
                    .mapToInt(exerciseSet -> exerciseSet.getWorkingSets())
                    .reduce(0, Integer::sum);
                
                data.get(dateString).put(
                    category,
                    data.get(dateString).getOrDefault(category, 0) + totalSets
                );
            }   
        }
        
        return data;
    }
}
