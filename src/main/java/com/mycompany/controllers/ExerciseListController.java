
package com.mycompany.controllers;

import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseSet;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ExerciseListController {
    
    //@FXML private Label dayNameLbl;
    
    @FXML private ListView<Exercise> exerciseListView;
    
    @FXML private Button editButton;
    @FXML private Button removeButton;
    
    public void initialize() {
        Callback<Exercise, Observable[]> extractor = (Exercise exercise) -> new Observable[] {
            exercise.idProperty(),
            exercise.exerciseInfoProperty(),
            exercise.exerciseSetListProperty()
        };
        
        ObservableList<Exercise> exercises = FXCollections.observableArrayList(extractor);
        
        /*
        Exercise bench = new Exercise("Bench Press");
        bench.addExerciseSet(new ExerciseSet(1, 5, 75));
        bench.addExerciseSet(new ExerciseSet(1, 3, 80));
        bench.addExerciseSet(new ExerciseSet(5, 5, 85));
        
        Exercise squat = new Exercise("Barbell Squat");
        squat.addExerciseSet(new ExerciseSet(1, 2, 3));
        squat.addExerciseSet(new ExerciseSet(2, 3, 4));
        squat.addExerciseSet(new ExerciseSet(3, 4, 5));
        squat.addExerciseSet(new ExerciseSet(4, 5, 6));
        squat.addExerciseSet(new ExerciseSet(5, 6, 7));
        squat.addExerciseSet(new ExerciseSet(6, 7, 8));
        squat.addExerciseSet(new ExerciseSet(7, 8, 9));
        squat.addExerciseSet(new ExerciseSet(8, 9, 10));
        
        exercises.addAll(bench, squat);
        */
        
        exerciseListView.setItems(exercises);
        
        // edit and remove buttons are disabled if an item is not selected in the listview
        editButton.disableProperty().bind(
            Bindings.isNull(
                exerciseListView.getSelectionModel().selectedItemProperty()
            )
        );
        
        removeButton.disableProperty().bind(
            Bindings.isNull(
                exerciseListView.getSelectionModel().selectedItemProperty()
            )
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

        Exercise selectedExercise = exerciseListView.getSelectionModel()
                                                    .getSelectedItem();
        controller.setExercise(selectedExercise);
        
        showWindow(root, "Exercise SetList Editor");
    }
    
    @FXML
    private void removeExercise() throws Exception {
        final int selectedListIndex = exerciseListView.getSelectionModel()
                                                      .getSelectedIndex();
        int newSelectedListIndex =
            (selectedListIndex == exerciseListView.getItems().size() - 1)
                ? selectedListIndex - 1
                : selectedListIndex;
        
        // TODO!!!
        // remove exercise from database and the sets associated with it
        
        exerciseListView.getItems().remove(selectedListIndex);
        
        if (newSelectedListIndex >= 0) {
            exerciseListView.getSelectionModel().select(newSelectedListIndex);
            
        } else {
            exerciseListView.getSelectionModel().clearSelection();
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
