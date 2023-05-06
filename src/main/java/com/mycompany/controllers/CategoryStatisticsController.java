
package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.charts.LabeledPieChart;
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
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/*
TODO
- add refresh option for category check boxes (use needToRecalculate)
*/
public class CategoryStatisticsController {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);

    @FXML private BarChart categorySetsBarChart;
    @FXML private BarChart categoryExercisesBarChart;
    
    @FXML private LabeledPieChart categorySetsPieChart;
    @FXML private LabeledPieChart categoryExercisesPieChart;
    @FXML private VBox pieChartContainer;
    
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    
    @FXML private Button calculateButton;
    
    @FXML private ChoiceBox choiceBox;
    
    @FXML private ScrollPane categoryScrollPane;
    
    private final BooleanProperty needToRecalculate =
        new SimpleBooleanProperty(true);
    
    private Map<String, Boolean> selectedCategoriesMap = new HashMap<>();
    
    private boolean selectAll = true;
    
    public void initialize() {
        initializeControls();
        setUpProperties();
        try {
            setUpCategoryCheckBoxes();
            
        } catch (SQLException e) {
            System.out.println(
                "Error in CategoryStatisticsController.setUpCategoryList(): "
                + e.getMessage()
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
                                setStyle("-fx-background-color: red;");
                            }
                        }
                    };
                }
            };
        endDatePicker.setDayCellFactory(dayCellFactory);
    }
    
    private void setUpProperties() {
        endDatePicker.disableProperty().bind(
            Bindings.or(
                startDatePicker.valueProperty().isNull(),
                startDatePicker.disableProperty()
            )
        );
        
        calculateButton.disableProperty().bind(
            Bindings.or(
                endDatePicker.valueProperty().isNull(),
                endDatePicker.disableProperty()
            )
        );
    }
    
    private void setUpCategoryCheckBoxes() throws SQLException {
        List<String> categoryList = manager.getAllExerciseInfoCategories();
        Collections.sort(categoryList);
        
        VBox vb = new VBox();
        vb.setSpacing(10);
        vb.setPadding(new Insets(10));
        
        List<CheckBox> categoryCheckBoxes = new ArrayList<>();
        
        Button btnToggle = new Button("Toggle all");
        btnToggle.setOnAction(event -> {
            categoryCheckBoxes.forEach(checkBox -> checkBox.setSelected(selectAll));
            selectAll = !selectAll;
        });
        
        vb.getChildren().add(btnToggle);
        
        for (String category : categoryList) {
            final CheckBox categoryCheckBox = new CheckBox(category);
            categoryCheckBoxes.add(categoryCheckBox);
            
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
        setUpCharts();
    }
    
    private void setUpCharts() throws SQLException {
        final LocalDate startLocalDate = startDatePicker.getValue();
        final LocalDate endLocalDate = endDatePicker.getValue();
        
        final String choice = (String) choiceBox.getValue();
        
        List<String> formattedLocalDateList =
            CustomLocalDateFormatter.getFormattedLocalDatesBetween(
                startLocalDate, endLocalDate, choice
            );
        
        List<Workout> workoutList = manager.getWorkoutsBetweenDates(
            Date.valueOf(startLocalDate), Date.valueOf(endLocalDate)
        );
        
        //<category, <date, #categorySets>>
        Map<String, Map<String, Integer>> totalCategorySetsMap = new HashMap<>();
        
        //<category, <date, #categoryExercises>>
        Map<String, Map<String, Integer>> totalCategoryExercisesMap = new HashMap<>();
        
        for (Workout workout : workoutList) {
            LocalDate workoutLocalDate = workout.getDate().toLocalDate();
            String dateString = CustomLocalDateFormatter.formatLocalDate(
                workoutLocalDate, choice
            );
            
            for (Exercise exercise : workout.getExerciseList()) {
                String category = exercise.getExerciseInfo().getCategory();
                
                int totalSets = exercise.getExerciseSetList().stream()
                    .mapToInt(exerciseSet -> exerciseSet.getWorkingSets())
                    .reduce(0, Integer::sum);
                
                addToMap(totalCategorySetsMap, category, dateString, totalSets);
                
                // adds one even if exercise exists in a workout but does
                // not have any sets
                addToMap(totalCategoryExercisesMap, category, dateString, 1);
            }   
        }
        
        setUpBarChart(totalCategorySetsMap, formattedLocalDateList, this.categorySetsBarChart);
        setUpBarChart(totalCategoryExercisesMap, formattedLocalDateList, this.categoryExercisesBarChart);
        
        createLabeledPieChart(totalCategorySetsMap, this.categorySetsPieChart, "Total sets");
        createLabeledPieChart(totalCategoryExercisesMap, this.categoryExercisesPieChart, "Total exercises");
    }
    
    private void addToMap(final Map<String, Map<String, Integer>> mapMap,
            String mapKey, String mapMapKey, int totalSets) {
        
        mapMap.putIfAbsent(mapKey, new HashMap<>());
        
        mapMap.get(mapKey).put(
            mapMapKey,
            mapMap.get(mapKey).getOrDefault(mapMapKey, 0) + totalSets
        );
    }
    
    private void setUpBarChart(final Map<String, Map<String, Integer>> data,
            List<String> formattedLocalDateList, BarChart barChart) {
        
        ObservableList seriesList = FXCollections.observableArrayList();
        for (String category : selectedCategoriesMap.keySet()) {
            if (!selectedCategoriesMap.getOrDefault(category, false)) {
                continue;
            }
            
            XYChart.Series totalSeries = new XYChart.Series();
            totalSeries.setName(category);
            
            final Map<String, Integer> dateMap = data.get(category);
            for (String dateString : formattedLocalDateList) {
                int total = 0;
                if (dateMap != null) {
                    total = dateMap.getOrDefault(dateString, 0);
                }
                
                totalSeries.getData().add(
                    new XYChart.Data(dateString, total)
                );
            }
            
            seriesList.add(totalSeries);
        }
        // this way there are no two animations going on at the same time
        // (clear data and set data)
        barChart.setData(seriesList);
    }
    
    private void createLabeledPieChart(final Map<String, Map<String, Integer>> data,
            LabeledPieChart chart, String title) {
        
        chart.getData().clear();
        
        pieChartContainer.layout();
        
        int totalSetsNotTracked = 0;
        for (String category : selectedCategoriesMap.keySet()) {
            if (!selectedCategoriesMap.getOrDefault(category, false)) {
                totalSetsNotTracked += data.getOrDefault(category, new HashMap<>())
                    .values()
                    .stream()
                    .reduce(0, Integer::sum);
                
            } else {
                int categorySets = data.getOrDefault(category, new HashMap<>())
                    .values()
                    .stream()
                    .reduce(0, Integer::sum);
                
                addPieChartData(chart, category, categorySets);
            }
        }
        if (totalSetsNotTracked != 0) {
            addPieChartData(chart, "Other", totalSetsNotTracked);
        }
        
        chart.setTitle(title + " (" + ((int) chart.getTotal()) + ")");
    }
    
    private void addPieChartData(PieChart pChart, String name, int value) {
        final Data data = new Data(name + " (" + value + ")", value);
        pChart.getData().add(data);
    }
}
