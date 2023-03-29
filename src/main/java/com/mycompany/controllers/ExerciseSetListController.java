package com.mycompany.controllers;

import com.mycompany.cells.DragAndDropListCell;
import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseSet;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ExerciseSetListController {
    
    @FXML private Label exerciseInfoNameLabel;
    
    @FXML private ListView<ExerciseSet> exerciseSetListView;
    
    @FXML private Button editButton;
    @FXML private Button removeButton;
    
    private final ObjectProperty<Exercise> exercise =
        new SimpleObjectProperty<>();
    
    public final void setExercise(Exercise exercise) {
        this.exercise.set(exercise);
    }
    
    private List<Integer> removedExerciseSetsIdList =
        new ArrayList<>();
    
    public void setRemovedExerciseSetsIdList(List<Integer> removedExerciseSetsIdList) {
        this.removedExerciseSetsIdList = removedExerciseSetsIdList;
    }
    
    public void initialize() {
        exerciseSetListView.setCellFactory(
            param -> new DragAndDropListCell(ExerciseSet.class)
        );
        setUpListeners();
        setUpProperties();
    }
    
    private void setUpListeners() {
        exercise.addListener((obs, oldExercise, newExercise) -> {
            if (newExercise != null) {
                exerciseSetListView.setItems(
                    newExercise.getExerciseSetList()
                );
                
                exerciseInfoNameLabel.setText(
                    newExercise.getExerciseInfo().getName()
                );
            }
        });
    }
    
    private void setUpProperties() {
        editButton.disableProperty().bind(
            Bindings.isNull(
                exerciseSetListView.getSelectionModel().selectedItemProperty()
            )
        );
        
        removeButton.disableProperty().bind(
            Bindings.isNull(
                exerciseSetListView.getSelectionModel().selectedItemProperty()
            )
        );
    }

    @FXML
    private void newExerciseSet() throws Exception {
        String resource = "/fxml/ExerciseSetEditor.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent root = loader.load();

        ExerciseSetEditorController controller = loader.getController();
        
        controller.exerciseSetProperty().addListener(
            (obs, oldExerciseSet, newExerciseSet) -> {
                if (newExerciseSet != null) {
                    exerciseSetListView.getItems().add(newExerciseSet);
                }
            }
        );

        showEditorWindow(root);
    }

    @FXML
    private void editExerciseSet() throws Exception {
        String resource = "/fxml/ExerciseSetEditor.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent root = loader.load();

        ExerciseSet editedExerciseSet = 
            exerciseSetListView.getSelectionModel().getSelectedItem();
        
        ExerciseSetEditorController controller = loader.getController();
        controller.setExerciseSet(editedExerciseSet);
        
        showEditorWindow(root);
    }
    
    @FXML
    private void removeExerciseSet() {
        final int selectedListViewIndex =
            exerciseSetListView.getSelectionModel().getSelectedIndex();
        
        int newSelectedListViewIndex =
            (selectedListViewIndex == (exerciseSetListView.getItems().size() - 1))
                ? (selectedListViewIndex - 1)
                : selectedListViewIndex;
        
        ExerciseSet removedExerciseSet =
            exerciseSetListView.getItems().remove(selectedListViewIndex);
        
        // if removed exercise set is not newly created, save it so it can be
        // later possibly deleted from the database
        if (removedExerciseSet.getId() > 0) {
            removedExerciseSetsIdList.add(removedExerciseSet.getId());
        }
        
        if (newSelectedListViewIndex >= 0) {
            exerciseSetListView.getSelectionModel()
                               .select(newSelectedListViewIndex);
        } else {
            exerciseSetListView.getSelectionModel().clearSelection();
        }
    }
    
    private void close() {
        exerciseSetListView.getScene().getWindow().hide();
    }

    private void showEditorWindow(Parent root) {
        Stage stage = new Stage();
        stage.initOwner(exerciseSetListView.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }
}