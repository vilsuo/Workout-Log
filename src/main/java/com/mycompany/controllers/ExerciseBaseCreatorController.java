package com.mycompany.controllers;

import com.mycompany.dao.ExerciseInfoDaoImpl;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.domain.Exercise;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class ExerciseBaseCreatorController {
    
    @FXML private ComboBox categoryCB;
    @FXML private ComboBox exerciseBaseCB;
    
    @FXML private Button okButton;

    private final ObjectProperty<Exercise> exerciseBase = new SimpleObjectProperty<>();
    
    public ObjectProperty<Exercise> exerciseBaseProperty() {
        return exerciseBase;
    }
    
    private ExerciseInfoDaoImpl database = new ExerciseInfoDaoImpl("jdbc:h2:./todo-database");
    
    private Map<String, List<String>> createCategories() {
        Map<String, List<String>> categoryMap = new HashMap<>();
        
        // populate categoryMap...
        String chest = "Chest";
        String shoulders = "Shoulders";
        String legs = "Legs";
        String back = "Back";
        
        categoryMap.putIfAbsent(chest, new ArrayList<>());
        categoryMap.get(chest).add("Bench Press");
        
        categoryMap.putIfAbsent(shoulders, new ArrayList<>());
        categoryMap.get(shoulders).add("Overhead Press");
        
        categoryMap.putIfAbsent(legs, new ArrayList<>());
        categoryMap.get(legs).add("Barbell Squat");
        categoryMap.get(legs).add("Deadlift");
        
        categoryMap.putIfAbsent(back, new ArrayList<>());
        categoryMap.get(back).add("Chin up");
        categoryMap.get(back).add("Pull up");
        
        return categoryMap;
    }
    
    public void initialize() {
        ObservableMap<String, List<String>> observableMap =
            FXCollections.observableMap(
                createCategories()
            );
        
        observableMap.addListener(new MapChangeListener() {
            @Override
            public void onChanged(MapChangeListener.Change change) {
                
                // write change to database?
                System.out.println("Detected a change! ");
            }
        });
        
        
        
        ObservableList<String> categoryList = FXCollections.observableArrayList(
            observableMap.keySet()
        );
        
        FXCollections.sort(categoryList);
        
        categoryCB.getItems().addAll(categoryList);
        
        categoryCB.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldCategory, newCategory) -> {
                exerciseBaseCB.getItems().setAll(
                    observableMap.get(newCategory.toString())
                );
            }
        );
        
        okButton.disableProperty().bind(
            Bindings.isNull(
                exerciseBaseCB.getSelectionModel().selectedItemProperty()
            )
        );
    }
    
    @FXML
    private void edit() {
        
    }
    
    // temporary...
    @FXML
    private void listAll() {
        try {
            database.getItems()
                    .forEach(exercise -> System.out.println(exercise));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    @FXML
    private void submit() {
        String exerciseName = exerciseBaseCB.getSelectionModel()
                                            .getSelectedItem().toString();
        exerciseBase.set(new Exercise(exerciseName));
        
        close();
    }
    
    @FXML
    private void cancel() {
        close();
    }
    
    private void close() {
        categoryCB.getScene().getWindow().hide();
    }
}
