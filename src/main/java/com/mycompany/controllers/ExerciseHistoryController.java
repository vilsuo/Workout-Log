
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

/*
TODO
- set records if at least the required rep count

- add option to add more lines to graph


- implement total volume/total sets charts

*/
public class ExerciseHistoryController {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    
    @FXML private ComboBox exerciseCategoryComboBox;
    @FXML private ComboBox exerciseNameComboBox;
    
    @FXML private Button calculateButton;
    
    @FXML private Label calculatedExerciseInfoLabel;
    @FXML private ListView historyListView;
    @FXML private TableView recordsTableView;
    
    @FXML private LineChart progressionLineChart;
    
    private ObservableMap<String, Map<String, Integer>> exerciseInfoMap;
    
    public void initialize() {
        setUpExerciseData();
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
            
        } catch (Exception e) {
            System.out.println(
                "Error in ExerciseCreatorController.loadExerciseInfoList(): "
                + e.getMessage()
            );
        }
        return new ArrayList<>();
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
        
        int minRepCount = 1;
        setUpProgressionChart(workoutList, exerciseInfo, minRepCount);
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
        return recordsMap;
    }
    
    private void setUpProgressionChart(final List<Workout> workoutList, final ExerciseInfo exerciseInfo, int minRepCount) {
        Map<Date, Double> progressionMap = calculateProgression(workoutList, exerciseInfo, minRepCount);
        
        progressionLineChart.setTitle("Progression for rep count >= " + minRepCount);
        
        XYChart.Series series = new XYChart.Series<>();
        progressionMap.keySet().stream().sorted().forEach(
            date -> {
                series.getData().add(
                    new XYChart.Data<>(date.toString(), progressionMap.get(date))
                );
            }
        );
        
        progressionLineChart.getData().setAll(series);
    }
    
    private Map<Date, Double> calculateProgression(final List<Workout> workoutList, final ExerciseInfo exerciseInfo, int minRepCount) {
        Map<Date, Double> progressionMap = new HashMap<>();
        
        double lastRecordWeight = 0.0;
        for (final Workout workout : workoutList) {
            
            Date date = workout.getDate();
            for (final Exercise exercise : workout.getExerciseList()) {
                
                if (exercise.getExerciseInfo().equals(exerciseInfo)) {
                    for (final ExerciseSet exerciseSet : exercise.getExerciseSetList()) {
                        
                        int reps = exerciseSet.getRepetitions();
                        double weight = exerciseSet.getWorkingWeight();
                        
                        if (reps >= minRepCount && weight > lastRecordWeight) {
                            
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
