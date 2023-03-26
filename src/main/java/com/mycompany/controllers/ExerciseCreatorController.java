package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.ExerciseInfo;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ExerciseCreatorController {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    
    @FXML private ComboBox categoryComboBox;
    @FXML private ComboBox nameComboBox;
    @FXML private Button addButton;
    
    private ObservableMap<String, Map<String, Integer>> exerciseInfoMap;

    private final ObjectProperty<ExerciseInfo> exerciseInfo = new SimpleObjectProperty<>();
    
    public ObjectProperty<ExerciseInfo> exerciseInfoProperty() {
        return exerciseInfo;
    }
    
    public void initialize() {
        setUpData();
        setUpListeners();
    }
    
    private void setUpData() {
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
        
        categoryComboBox.getItems().setAll(categoryList);
    }
    
    private List<ExerciseInfo> loadExerciseInfoList() {
        try {
            return manager.getAllExerciseInfos();
        } catch (Exception e) {
            System.out.println("Error in ExerciseCreatorController.loadExerciseInfoList(): " + e.getMessage());
        }
        return new ArrayList<>();
    }
    
    private void setUpListeners() {
        categoryComboBox.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldCategory, newCategory) -> {
                if (newCategory != null) {
                    ObservableList<String> observableList = FXCollections.observableArrayList(
                        exerciseInfoMap.get((String) newCategory).keySet()
                    );
                    FXCollections.sort(observableList);
                    nameComboBox.getItems().setAll(observableList);
                }
            }
        );
        
        nameComboBox.disableProperty().bind(
            Bindings.isNull(categoryComboBox.getSelectionModel().selectedItemProperty())
        );
        
        addButton.disableProperty().bind(
            Bindings.isNull(nameComboBox.getSelectionModel().selectedItemProperty())
        );
    }
    
    @FXML
    private void edit() throws Exception {
        categoryComboBox.getSelectionModel().clearSelection();
        nameComboBox.getSelectionModel().clearSelection();
        
        String resource = "/fxml/ExerciseInfoEditor.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent root = loader.load();
        showEditorWindow(root);
        
        // refresh...
        setUpData();
    }
    
    @FXML
    private void submit() {
        String category = (String) categoryComboBox.getSelectionModel().getSelectedItem();
        String name = (String) nameComboBox.getSelectionModel().getSelectedItem();
        int exerciseInfoId = exerciseInfoMap.get(category).get(name);
        
        ExerciseInfo newExerciseInfo = new ExerciseInfo(exerciseInfoId, name, category);
        exerciseInfo.set(newExerciseInfo);
    }
    
    @FXML
    private void cancel() {
        close();
    }
    
    private void close() {
        categoryComboBox.getScene().getWindow().hide();
    }
    
    private void showEditorWindow(Parent root) {
        Stage stage = new Stage();
        stage.setTitle("Exercise Info Editor");
        stage.initOwner(categoryComboBox.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
