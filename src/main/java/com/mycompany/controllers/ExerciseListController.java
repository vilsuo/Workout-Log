
package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.cells.DragAndDropListCell;
import com.mycompany.dao.ExerciseManagerImpl;
import com.mycompany.domain.Exercise;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ExerciseListController {
    
    private final ExerciseManagerImpl manager = new ExerciseManagerImpl(App.DATABASE_PATH);
    
    @FXML private ListView<Exercise> exerciseListView;
    
    @FXML private Button editButton;
    @FXML private Button removeButton;
    
    public void initialize() {
        setUpData();
        setUpProperties();
    }
    
    private void setUpData() {
        Callback<Exercise, Observable[]> extractor = (Exercise exercise) -> new Observable[] {
            exercise.idProperty(),
            exercise.exerciseInfoProperty(),
            exercise.exerciseSetListProperty(),
            exercise.orderNumberProperty()
        };
        
        ObservableList<Exercise> exercises = FXCollections.observableList(loadExerciseList(), extractor);
        exerciseListView.setItems(exercises);
        exerciseListView.setCellFactory(param -> new DragAndDropListCell(Exercise.class));
    }
    
    private List<Exercise> loadExerciseList() {
        try {
            return manager.getAllExercises();
        } catch (Exception e) {
            System.out.println("Error in ExerciseListController.loadExerciseList: " + e.getMessage());
        }
        return new ArrayList<>();
    }
    
    private void setUpProperties() {
        editButton.disableProperty().bind(
            Bindings.isNull(exerciseListView.getSelectionModel().selectedItemProperty())
        );
        
        removeButton.disableProperty().bind(
            Bindings.isNull(exerciseListView.getSelectionModel().selectedItemProperty())
        );
    }
    
    @FXML
    private void newExercise() throws Exception {
        String resource = "/fxml/ExerciseCreator.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent root = loader.load();
        
        ExerciseCreatorController controller = loader.getController();
        controller.exerciseInfoProperty().addListener(
            (obs, oldExerciseInfo, newExerciseInfo) -> {
                if (newExerciseInfo != null) {
                    Exercise newExercise;
                    try {
                        newExercise = manager.createNewExercise(newExerciseInfo, exerciseListView.getItems().size() + 1);
                        exerciseListView.getItems().add(newExercise);
                    } catch (SQLException ex) {
                        System.out.println("Error in ExerciseListController.newExercise(): ");
                        ex.printStackTrace();
                    }
                }
            }
        );

        showWindow(root, "Exercise Creator");
        
        // refresh in case of renames
        setUpData();
    }
    
    @FXML
    private void editExercise() throws Exception {
        String resource = "/fxml/ExerciseSetList.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent root = loader.load();
        
        ExerciseSetListController controller = loader.getController();
        Exercise selectedExercise = exerciseListView.getSelectionModel().getSelectedItem();
        controller.setExercise(selectedExercise);
        
        showWindow(root, "Exercise SetList Editor");
        
        // no need to reload edited exercise?
    }
    
    @FXML
    private void removeExercise() throws Exception {
        final int selectedListIndex = exerciseListView.getSelectionModel().getSelectedIndex();
        int newSelectedListIndex = (selectedListIndex == exerciseListView.getItems().size() - 1)
            ? (selectedListIndex - 1)
            : selectedListIndex;
        
        try {
            Exercise selectedExercise = exerciseListView.getSelectionModel().getSelectedItem();
            manager.removeExercise(selectedExercise.getId());
            exerciseListView.getItems().remove(selectedListIndex);
            
            if (newSelectedListIndex >= 0) {
                exerciseListView.getSelectionModel().select(newSelectedListIndex);
            } else {
                exerciseListView.getSelectionModel().clearSelection();
            }
        } catch (Exception e) {
            System.out.println("Error in ExerciseListController.removeExercise(): " + e.getMessage());
        }
    }
    
    private void showWindow(Parent root, String title) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initOwner(exerciseListView.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL); 
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
