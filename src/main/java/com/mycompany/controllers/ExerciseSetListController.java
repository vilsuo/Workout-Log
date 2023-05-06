package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.controls.DragAndDropListCell;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.domain.ExerciseSet;
import com.mycompany.domain.Workout;
import com.mycompany.history.ExerciseInfoHistoryBuilder;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    private final String EXERCISE_SET_EDITOR_PATH =
        "/fxml/ExerciseSetEditor.fxml";
    
    @FXML private Label exerciseInfoNameLabel;
    
    @FXML private ListView<ExerciseSet> exerciseSetListView;
    @FXML private ListView exerciseHistoryListView;
    
    @FXML private Button editButton;
    @FXML private Button removeButton;
    
    private final ObjectProperty<Exercise> exercise =
        new SimpleObjectProperty<>();
    
    public final void setExercise(Exercise exercise) {
        this.exercise.set(exercise);
    }
    
    private List<Integer> removedExerciseSetsIdList =
        new ArrayList<>();
    
    public void setRemovedExerciseSetsIdList(
            List<Integer> removedExerciseSetsIdList) {
        
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
                    newExercise.getExerciseInfo().toString()
                );
                
                setUpHistory();
            }
        });
    }
    
    private void setUpHistory() {
        final ExerciseInfo selectedExerciseInfo =
            exercise.get().getExerciseInfo();
        
        try {
            List<Workout> workoutList = manager.getAllWorkouts();
            
            exerciseHistoryListView.setItems(
                ExerciseInfoHistoryBuilder.createHistoryStringList(
                    workoutList, selectedExerciseInfo
                )
            );
            
        } catch (Exception e) {
            System.out.println(
                "Error in ExerciseSetListContoller.setUpHistory(): "
                + e.getMessage()
            );
        }
    }
    
    private void setUpProperties() {
        BooleanBinding exerciseSetNotSelectedBinding = Bindings.isNull(
            exerciseSetListView.getSelectionModel().selectedItemProperty()
        );
        
        editButton.disableProperty().bind(exerciseSetNotSelectedBinding);
        removeButton.disableProperty().bind(exerciseSetNotSelectedBinding);
    }

    @FXML
    private void newExerciseSet() throws Exception {
        String resource = EXERCISE_SET_EDITOR_PATH;
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
        String resource = EXERCISE_SET_EDITOR_PATH;
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

    private void showEditorWindow(Parent parent) {
        Stage stage = new Stage();
        stage.initOwner(exerciseSetListView.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.showAndWait();
    }
}