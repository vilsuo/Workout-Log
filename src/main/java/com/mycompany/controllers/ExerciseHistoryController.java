
package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.history.ExerciseRecordEntry;
import com.mycompany.domain.ExerciseSet;
import com.mycompany.domain.Workout;
import com.mycompany.history.ExerciseInfoHistoryBuilder;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

/*
TODO
- add option to add more lines to graph
- add minRepCount textfield

- make sure the min rep count label has an integer value

- implement total volume/total sets charts

*/
public class ExerciseHistoryController {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    
    @FXML private ComboBox exerciseCategoryComboBox;
    @FXML private ComboBox exerciseNameComboBox;
    
    @FXML private ScrollPane maximumRepetitionCountScrollPane;
    
    @FXML private Button calculateButton;
    
    @FXML private Label calculatedExerciseInfoLabel;
    @FXML private ListView historyListView;
    @FXML private TableView recordsTableView;
    
    @FXML private LineChart progressionLineChart;
    
    private final Map<String, Boolean> selectedMaximumRepetitionCountsMap = new HashMap<>();
    
    private ObservableMap<String, Map<String, Integer>> exerciseInfoMap;
    
    public void initialize() {
        setUpExerciseData();
        setUpMaximumRepetitionCountCheckBoxes();
        setUpListeners();
        setUpProperties();
    }
    
    private void setUpExerciseData() {
        // add exercise info categories to exerciseCategoryComboBox and sets up
        // a look up table for exercise info category to exercise info name
        List<ExerciseInfo> data = loadExerciseInfoList();
        Map<String, Map<String , Integer>> map = new HashMap<>();
        for (ExerciseInfo item : data) {
            if (!map.containsKey(item.getCategory())) {
                map.put(item.getCategory(), new HashMap<>());
            }
            map.get(item.getCategory()).put(item.getName(), item.getId());
        }
        
        exerciseInfoMap = FXCollections.observableHashMap();
        exerciseInfoMap.putAll(map);
        
        ObservableList<String> categoryList = FXCollections.observableArrayList(
            exerciseInfoMap.keySet()
        );
        FXCollections.sort(categoryList);
        
        exerciseCategoryComboBox.getItems().setAll(categoryList);
    }
    
    private List<ExerciseInfo> loadExerciseInfoList() {
        try {
            return manager.getAllExerciseInfos();
            
        } catch (SQLException e) {
            System.out.println(
                "Error in ExerciseHistoryController.loadExerciseInfoList(): "
                + e.getMessage()
            );
        }
        return new ArrayList<>();
    }
    
    private void setUpMaximumRepetitionCountCheckBoxes() {
        HBox hb = new HBox();
        hb.setPadding(new Insets(10));
        hb.setSpacing(5);
        
        final List<String> maximumRepetitionCountOptions = new ArrayList<>(
            Arrays.asList(
                "1", "2", "3", "4", "5", "6", "8", "10", "12", "15", "20"
            )
        );
        
        for (String str : maximumRepetitionCountOptions) {
            final CheckBox maximumRepetitionCountCheckBox = new CheckBox(str);
            maximumRepetitionCountCheckBox.selectedProperty().addListener(
                (obs, oldValue, newValue) -> {
                    selectedMaximumRepetitionCountsMap.put(str, newValue);
                }
            );
            hb.getChildren().add(maximumRepetitionCountCheckBox);
        }
        maximumRepetitionCountScrollPane.setContent(hb);
    }
    
    private void setUpListeners() {
        // when category is selected, the name comboBox gets updated
        exerciseCategoryComboBox.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldValue, newValue) -> {
                if (newValue != null) {
                    ObservableList<String> observableList = FXCollections.observableArrayList(
                        exerciseInfoMap.get((String) newValue).keySet()
                    );
                    FXCollections.sort(observableList);
                    exerciseNameComboBox.getItems().setAll(observableList);
                }
            }
        );
    }
    
    private void setUpProperties() {
        exerciseNameComboBox.disableProperty().bind(
            Bindings.isNull(
                exerciseCategoryComboBox.getSelectionModel()
                                        .selectedItemProperty()
            )
        );
        
        calculateButton.disableProperty().bind(
            Bindings.isNull(
                exerciseNameComboBox.getSelectionModel().selectedItemProperty()
            )
        );
    }
    
    @FXML
    private void onCalculateButtonPressed() {
        calculateAndDisplay();
    }
    
    private void calculateAndDisplay() {
        String category = (String) exerciseCategoryComboBox.getSelectionModel()
                .getSelectedItem();
        
        String name = (String) exerciseNameComboBox.getSelectionModel()
                .getSelectedItem();
        
        int exerciseInfoId = exerciseInfoMap.get(category).get(name);
        
        final ExerciseInfo exerciseInfo = new ExerciseInfo(
            exerciseInfoId, name, category
        );
        
        calculatedExerciseInfoLabel.setText(exerciseInfo.toString());
        
        List<Workout> workoutList = new ArrayList<>();
        try {
            workoutList = manager.getAllWorkouts();
        } catch (SQLException e) {
            System.out.println(
                "Error in ExerciseHistoryController.showHistory(): "
                + e.getMessage()
            );
        }
        
        setUpHistoryListView(workoutList, exerciseInfo);
        setUpRecordsTableView(workoutList, exerciseInfo);
        setUpProgressionLineChart(workoutList, exerciseInfo);
    }
    
    private void setUpHistoryListView(final List<Workout> workoutList,
            final ExerciseInfo exerciseInfo) {
        
        historyListView.setItems(
            ExerciseInfoHistoryBuilder.createHistoryStringList(
                workoutList, exerciseInfo
            )
        );
    }
    
    private void setUpRecordsTableView(final List<Workout> workoutList,
            final ExerciseInfo exerciseInfo) {
        
        Map<Integer, ExerciseRecordEntry> recordsMap =
            calculateRecords(workoutList, exerciseInfo);
        
        List<ExerciseRecordEntry> sortedExerciseRecordEntryList
                = recordsMap.values().stream().sorted(
                    (entry1, entry2) -> {
                        return entry1.getRepetitions() - entry2.getRepetitions();
                    }
                ).collect(Collectors.toList());
        
        recordsTableView.setItems(
            FXCollections.observableList(sortedExerciseRecordEntryList)
        );
    }
    
    private Map<Integer, ExerciseRecordEntry> calculateRecords(
            final List<Workout> workoutList, final ExerciseInfo exerciseInfo) {
        
        // <repetitions, ExerciseRecordEnty>
        Map<Integer, ExerciseRecordEntry> recordsMap = new HashMap<>();
        
        // 1. calculate actual exact rep records
        for (Workout workout : workoutList) {
            Date workoutDate = workout.getDate();
            
            for (Exercise exercise : workout.getExerciseList()) {
                
                if (exercise.getExerciseInfo().equals(exerciseInfo)) {
                    
                    for (ExerciseSet exerciseSet : exercise.getExerciseSetList()) {
                        int repetitions = exerciseSet.getRepetitions();
                        double weight = exerciseSet.getWorkingWeight();
                        
                        ExerciseRecordEntry newEntry =
                            new ExerciseRecordEntry(repetitions, weight, workoutDate);
                        
                        if (!recordsMap.containsKey(repetitions)) {
                            recordsMap.put(repetitions, newEntry);
                            
                        } else {
                            ExerciseRecordEntry oldEntry =
                                recordsMap.get(repetitions);
                            
                            if (newEntry.getWeight() > oldEntry.getWeight()) {
                                recordsMap.put(repetitions, newEntry);
                            }
                        }
                    }
                }
            }
        }
        
        // 2. fill in the missing values and if higher rep count has higher 
        // weight, update the lower rep count maxes
        if (recordsMap.size() > 1) {
            int maxRepetitions = recordsMap.keySet().stream()
                .max(Integer::compare).get();
            
            for (int repetitions = maxRepetitions - 1; repetitions > 0; --repetitions) {
                double previousWeight = recordsMap.get(repetitions + 1).getWeight();
                Date previousDate = recordsMap.get(repetitions + 1).getDate();
                
                if (!recordsMap.containsKey(repetitions) || 
                        (previousWeight > recordsMap.get(repetitions).getWeight())) {
                    
                    recordsMap.put(
                        repetitions,
                        new ExerciseRecordEntry(
                            repetitions, 
                            previousWeight,
                            previousDate
                        )
                    );
                }
            }
        }
        return recordsMap;
    }
    
    private void setUpProgressionLineChart(final List<Workout> workoutList, final ExerciseInfo exerciseInfo) {
        progressionLineChart.getData().clear();
        
        final Set<String> progressionLineChartCategoriesSet = new LinkedHashSet<>();
        for (String maximumRepetitionCount : selectedMaximumRepetitionCountsMap.keySet()) {
            // check if repetition count is selected
            if (selectedMaximumRepetitionCountsMap.get(maximumRepetitionCount)) {
                Map<Date, Double> progressionMap = calculateProgression(
                    workoutList, exerciseInfo, Integer.parseInt(maximumRepetitionCount)
                );

                XYChart.Series series = new XYChart.Series<>();
                series.setName(maximumRepetitionCount + "+");
                progressionMap.keySet().stream().sorted().forEach(
                    date -> {
                        series.getData().add(
                            new XYChart.Data<>(date.toString(), progressionMap.get(date))
                        );
                        progressionLineChartCategoriesSet.add(date.toString());
                    }
                );
                
                progressionLineChart.getData().add(series);
            }
        }
        
        // sort x-axis values
        final ObservableList<String> progressionLineChartCategoriesList =
            FXCollections.observableArrayList(progressionLineChartCategoriesSet);
        
        Collections.sort(progressionLineChartCategoriesList);
        
        CategoryAxis  progressionLineChartXAxis = ((CategoryAxis) progressionLineChart.getXAxis());
        progressionLineChartXAxis.setCategories(progressionLineChartCategoriesList);
        
        // to show category labels
        progressionLineChartXAxis.setAutoRanging(true);
    }
    
    /**
     * 
     * @param workoutList list must be sorted by date increasing
     * @param exerciseInfo
     * @param maximumRepetitionCountrising
     * @return 
     */
    private Map<Date, Double> calculateProgression(final List<Workout> workoutList,
            final ExerciseInfo exerciseInfo, int maximumRepetitionCount) {
        
        Map<Date, Double> progressionMap = new HashMap<>();
        
        double lastRecordWeight = 0.0;
        for (final Workout workout : workoutList) {
            
            Date date = workout.getDate();
            for (final Exercise exercise : workout.getExerciseList()) {
                
                if (exercise.getExerciseInfo().equals(exerciseInfo)) {
                    for (final ExerciseSet exerciseSet : exercise.getExerciseSetList()) {
                        
                        int reps = exerciseSet.getRepetitions();
                        double weight = exerciseSet.getWorkingWeight();
                        
                        if (reps >= maximumRepetitionCount && weight > lastRecordWeight) {
                            progressionMap.put(date, weight);
                            lastRecordWeight = weight;
                        }
                    }
                }
            }
        }
        return progressionMap;
    }
}
