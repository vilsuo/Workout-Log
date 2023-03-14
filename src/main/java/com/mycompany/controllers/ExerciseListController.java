
package com.mycompany.controllers;

import com.mycompany.dao.ExerciseDaoImpl;
import com.mycompany.domain.Exercise;
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
    
    private final ExerciseDaoImpl exerciseDatabase = new ExerciseDaoImpl();
    
    @FXML private ListView<Exercise> exerciseListView;
    
    @FXML private Button editButton;
    @FXML private Button removeButton;
    
    public void initialize() {
        setUpData();
        setUpListeners();
    }
    
    private void setUpData() {
        Callback<Exercise, Observable[]> extractor = (Exercise exercise) -> new Observable[] {
            exercise.idProperty(),
            exercise.exerciseInfoProperty(),
            exercise.exerciseSetListProperty()
        };
        
        ObservableList<Exercise> exercises = FXCollections.observableList(loadExerciseList(), extractor);
        exerciseListView.setItems(exercises);
    }
    
    private List<Exercise> loadExerciseList() {
        try {
            return exerciseDatabase.getTableItems();
        } catch (Exception e) {
            System.out.println("Error in ExerciseListController.getData(): " + e.getMessage());
        }
        return new ArrayList<>();
    }
    
    private void setUpListeners() {
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
        controller.exerciseProperty().addListener(
            (obs, oldExercise, newExercise) -> {
                if (newExercise != null) {
                    exerciseListView.getItems().add(newExercise);
                }
            }
        );

        showWindow(root, "Exercise Creator");
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
    }
    
    @FXML
    private void removeExercise() throws Exception {
        final int selectedListIndex = exerciseListView.getSelectionModel().getSelectedIndex();
        int newSelectedListIndex = (selectedListIndex == exerciseListView.getItems().size() - 1)
            ? (selectedListIndex - 1)
            : selectedListIndex;
        
        // TODO!!!
        // removeItem exercise from database and the sets associated with it
        
        
        try {
            Exercise selectedExercise = (Exercise) exerciseListView.getSelectionModel().getSelectedItem();
            exerciseDatabase.removeItem(selectedExercise.getId());
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
