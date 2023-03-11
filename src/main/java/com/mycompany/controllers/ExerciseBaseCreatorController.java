package com.mycompany.controllers;

import com.mycompany.cells.ExerciseInfoNameDisplayCell;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ExerciseBaseCreatorController {
    
    private final String databasePath = "jdbc:h2:./workoutLog-database";
    private ExerciseInfoDaoImpl database;
    
    @FXML private ComboBox categoryCB;
    @FXML private ComboBox nameCB;
    
    @FXML private Button addButton;

    private final ObjectProperty<Exercise> exerciseBase = new SimpleObjectProperty<>();
    
    ObservableMap<String, List<ExerciseInfo>> observableMap;
    
    public ObjectProperty<Exercise> exerciseBaseProperty() {
        return exerciseBase;
    }
    
    public void initialize() {
        createCategories();
        
        categoryCB.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldCategory, newCategory) -> {
                if (newCategory != null) {
                    ObservableList<ExerciseInfo> observableList =
                    FXCollections.observableList(
                        observableMap.get((String) newCategory)
                    );
                
                    FXCollections.sort(observableList, (item1, item2) -> {
                            return item1.getName().compareTo(item2.getName());
                        }
                    ); 
                    nameCB.getItems().setAll(observableList);
                }
            }
        );
        
        Callback<ListView<ExerciseInfo>, ListCell<ExerciseInfo>> cb = param -> {
            return new ExerciseInfoNameDisplayCell();
        };
        
        // set combobox to display names instead of ExerciseInfo objects
        nameCB.setCellFactory(cb);
        
        // set combobox button cell also to displays names
        nameCB.setButtonCell(cb.call(null));
        
        nameCB.disableProperty().bind(
            Bindings.isNull(
                categoryCB.getSelectionModel().selectedItemProperty()
            )
        );
        
        addButton.disableProperty().bind(
            Bindings.isNull(
                nameCB.getSelectionModel().selectedItemProperty()
            )
        );
    }
    
    private void createCategories() {
        observableMap = FXCollections.observableHashMap();
        Map<String, List<ExerciseInfo>> map = new HashMap<>();
        
        List<ExerciseInfo> data = getData();
        for (ExerciseInfo item : data) {
            if (!map.containsKey(item.getCategory())) {
                map.put(item.getCategory(), new ArrayList<>());
            }
            map.get(item.getCategory()).add(item);
        }
        observableMap.putAll(map);
        
        ObservableList<String> categoryList = FXCollections.observableArrayList(
            observableMap.keySet()
        );
        
        FXCollections.sort(categoryList);
        categoryCB.getItems().setAll(categoryList);
    }
    
    private List<ExerciseInfo> getData() {
        List<ExerciseInfo> data = new ArrayList<>();
        try {
            database = new ExerciseInfoDaoImpl(databasePath);
            data = database.getItems();
            
        } catch (Exception e) {
            // log: loading database was not successful...
            System.out.println("loading database was not successful");
        }
        return data;
    }
    
    @FXML
    private void edit() throws Exception {
        // clear selections
        categoryCB.getSelectionModel().clearSelection();
        nameCB.getSelectionModel().clearSelection();
        
        String resource = "/fxml/ExerciseInfoEditor.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent root = loader.load();
        showEditorWindow(root);
        
        // refresh...
        createCategories();
    }
    
    @FXML
    private void submit() {
        String exerciseName = ((ExerciseInfo)nameCB.getSelectionModel()
                                            .getSelectedItem()).getName();
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
    
    private void showEditorWindow(Parent root) {
        Stage stage = new Stage();
        stage.setTitle("Exercise Info Editor");
        stage.initOwner(categoryCB.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    /*
    // temporary...
    @FXML
    private void listAll() {
        String cb = (String) categoryCB.getSelectionModel().getSelectedItem();
        ExerciseInfo name = (ExerciseInfo) nameCB.getSelectionModel().getSelectedItem();
        
        System.out.println("category: " + cb != null ? cb : "null");
        System.out.println("name: " + name != null ? name : "null");
        
        try {
            database.getItems()
                    .forEach(exercise -> System.out.println(exercise));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    */
}
