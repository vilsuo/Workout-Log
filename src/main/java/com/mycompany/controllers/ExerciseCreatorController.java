package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.utilities.NumberGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class ExerciseCreatorController {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    
    @FXML private ComboBox exerciseCategoryComboBox;
    @FXML private ComboBox exerciseNameComboBox;
    @FXML private Button addButton;
    
    private ObservableMap<String, Map<String, Integer>> exerciseInfoMap;

    private final ObjectProperty<Exercise> exercise =
        new SimpleObjectProperty<>();
    
    public ObjectProperty<Exercise> exerciseProperty() {
        return exercise;
    }
    
    private void setExerciseProperty(Exercise exercise) {
        this.exercise.set(exercise);
    }
    
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
            (obs, oldCategory, newCategory) -> {
                if (newCategory != null) {
                    ObservableList<String> observableList = FXCollections.observableArrayList(
                        exerciseInfoMap.get((String) newCategory).keySet()
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
                exerciseCategoryComboBox.getSelectionModel().selectedItemProperty()
            )
        );
        
        addButton.disableProperty().bind(
            Bindings.isNull(
                exerciseNameComboBox.getSelectionModel().selectedItemProperty()
            )
        );
    }
    
    @FXML
    private void submit() {
        String category = (String) exerciseCategoryComboBox.getSelectionModel().getSelectedItem();
        String name = (String) exerciseNameComboBox.getSelectionModel().getSelectedItem();
        int exerciseInfoId = exerciseInfoMap.get(category).get(name);
        
        final ExerciseInfo newExerciseInfo = new ExerciseInfo(exerciseInfoId, name, category);
        
        int tempId = NumberGenerator.generateNextNegativeNumber();
        int tempOrderNumber = -1;
        
        setExerciseProperty(
            new Exercise(
                tempId,
                newExerciseInfo,
                tempOrderNumber
            )
        );
        
        close();
    }
    
    @FXML
    private void cancel() {
        close();
    }
    
    private void close() {
        exerciseCategoryComboBox.getScene().getWindow().hide();
    }
}
