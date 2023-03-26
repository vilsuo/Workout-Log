
package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.cells.DragAndDropListCell;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Exercise;
import com.mycompany.domain.Workout;
import java.sql.SQLException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/* TODO!
- drag and drop update order number

*/
public class ExerciseListController {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    
    @FXML private ListView<Exercise> exerciseListView;
    
    @FXML private Button editButton;
    @FXML private Button removeButton;
    
    private final ObjectProperty<Workout> workout =
        new SimpleObjectProperty();
    
    public ObjectProperty<Workout> workoutProperty() {
        return workout;
    }
    
    public void setWorkout(Workout workout) {
        workoutProperty().set(workout);
    }
    
    public void initialize() {
        exerciseListView.setCellFactory(
            param -> new DragAndDropListCell(Exercise.class)
        );
        setUpProperties();
    }
    
    private void setUpProperties() {
        workoutProperty().addListener(
            (obs, oldWorkout, newWorkout) -> {
                if (newWorkout != null) {
                    exerciseListView.setItems(newWorkout.getExerciseList());
                }
            }
        );
        
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
        controller.exerciseInfoProperty().addListener(
            (obs, oldExerciseInfo, newExerciseInfo) -> {
                if (newExerciseInfo != null) {
                    try {
                        int workoutId = workout.get().getId();
                        int newExerciseOrderNumber =
                            exerciseListView.getItems().size() + 1;
                        
                        Exercise newExercise = manager.createExercise(
                            workoutId, newExerciseInfo, newExerciseOrderNumber
                        );
                        
                        workout.get().addExercise(newExercise);
                        
                    } catch (SQLException ex) {
                        System.out.println("Error in ExerciseListController.newExercise(): ");
                        ex.printStackTrace();
                    }
                }
            }
        );

        showWindow(root, "Exercise Creator");
        
        // refresh in case of renames
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
        final int selectedListIndex = exerciseListView.getSelectionModel()
                                                      .getSelectedIndex();
        int newSelectedListIndex =
            (selectedListIndex == exerciseListView.getItems().size() - 1)
            ? (selectedListIndex - 1)
            : selectedListIndex;
        
        try {
            Exercise selectedExercise = exerciseListView.getSelectionModel()
                                                        .getSelectedItem();
            manager.removeExercise(selectedExercise.getId());
            exerciseListView.getItems().remove(selectedListIndex);
            
            if (newSelectedListIndex >= 0) {
                exerciseListView.getSelectionModel()
                                .select(newSelectedListIndex);
            } else {
                exerciseListView.getSelectionModel().clearSelection();
            }
        } catch (Exception e) {
            System.out.println(
                "Error in ExerciseListController.removeExercise(): " + e.getMessage()
            );
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
