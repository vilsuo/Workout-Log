
package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.cells.DragAndDropListCell;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.domain.ExerciseSet;
import com.mycompany.domain.Workout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/* TODO!

*/
public class ExerciseListController {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    
    @FXML private TextField workoutTextField;
    
    @FXML private ListView<Exercise> exerciseListView;
    
    private ObservableList<Exercise> copiedExerciseList;
    
    @FXML private Button saveButton;
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
    
    private final List<Integer> removedExercisesIdList = new ArrayList<>();
    private final Map<Integer, List<Integer>> removedExerciseSetsIdMap =
        new HashMap<>();
    
    private final BooleanProperty changeMadeProperty =
        new SimpleBooleanProperty(false);
    
    private final ButtonType yesButton = new ButtonType("Yes");
    private final ButtonType noButton = new ButtonType("No");
    private final ButtonType cancelButton = new ButtonType("Cancel");
    
    public void initialize() {
        exerciseListView.setCellFactory(
            param -> new DragAndDropListCell(Exercise.class)
        );
        
        Callback<Exercise, Observable[]> extractor =
        (Exercise exercise) -> new Observable[] {
            exercise.idProperty(),
            exercise.exerciseInfoProperty(),
            exercise.exerciseSetListProperty(),
            exercise.orderNumberProperty()
        };

        copiedExerciseList = FXCollections.observableArrayList(extractor);
        
        setUpListeners();
        setUpProperties();
    }
    
    private void setUpListeners() {
        workoutProperty().addListener(
            (obs, oldWorkout, newWorkout) -> {
                if (newWorkout != null) {
                    workoutTextField.setText(newWorkout.getName());
                    
                    // copy exercises
                    for (Exercise exercise : newWorkout.getExerciseList()) {
                        ExerciseInfo copiedExerciseInfo = new ExerciseInfo(
                            exercise.getExerciseInfo().getId(),
                            exercise.getExerciseInfo().getName(),
                            exercise.getExerciseInfo().getCategory()
                        );
                        
                        Exercise copiedExercise = new Exercise(
                            exercise.getId(),
                            copiedExerciseInfo,
                            exercise.getOrderNumber()
                        );
                        
                        for (ExerciseSet exerciseSet : exercise.getExerciseSetList()) {
                            ExerciseSet copiedExerciseSet = new ExerciseSet(
                                exerciseSet.getId(),
                                exerciseSet.getWorkingSets(),
                                exerciseSet.getRepetitions(),
                                exerciseSet.getWorkingWeight(),
                                exerciseSet.getOrderNumber()
                            );
                            copiedExercise.addExerciseSet(copiedExerciseSet);
                        }
                        
                        copiedExerciseList.add(copiedExercise);
                    }
                    exerciseListView.setItems(copiedExerciseList);
                }
                
                copiedExerciseList.addListener(new ListChangeListener() {

                    @Override
                    public void onChanged(ListChangeListener.Change change) {
                        changeMadeProperty.set(true);
                    }
                });
            }
        );
        
        workoutTextField.textProperty().addListener(
            (obs, oldValue, newValue) -> {
                if (!newValue.equals(workout.get().getName())) {
                    changeMadeProperty.set(true);
                }
            }
        );
    }
    
    private void setUpProperties() {
        saveButton.disableProperty().bind(Bindings.not(changeMadeProperty));
        
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
    
    // split into smaller/simpler functions?
    @FXML
    private void save() throws Exception {
        Optional<ButtonType> optional = showSaveAlert();
        if (optional.get() == yesButton) {
            // update the workout name
            if (!workoutTextField.getText().equals(workout.get().getName())) {
                manager.updateWorkoutName(
                    workout.get().getId(), workoutTextField.getText()
                );
                workout.get().setName(workoutTextField.getText());
            }
            
            // handle removed exercises
            for (int removedExerciseId : removedExercisesIdList) {
                manager.removeExercise(removedExerciseId);
            }
            
            int nextExerciseOrderNumber = 1;
            for (Exercise exercise : exerciseListView.getItems()) {
                if (exercise.getId() < 0) {
                    // new exercise
                    exercise.setOrderNumber(nextExerciseOrderNumber);
                    
                    int newExerciseId = manager.createExercise(
                        workout.get().getId(),
                        exercise.getExerciseInfo().getId(),
                        exercise.getOrderNumber()
                    );
                    exercise.setId(newExerciseId);
                    
                    int nextExerciseSetOrderNumber = 1;
                    for (ExerciseSet exerciseSet : exercise.getExerciseSetList()) {
                        exerciseSet.setOrderNumber(nextExerciseSetOrderNumber);
                        // all exercise sets are new
                        int newExerciseSetId = manager.createExerciseSet(
                            exerciseSet.getWorkingSets(),
                            exerciseSet.getRepetitions(),
                            exerciseSet.getWorkingWeight(),
                            exerciseSet.getOrderNumber()
                        );
                        exerciseSet.setId(newExerciseSetId);
                        
                        manager.addExerciseSetToExercise(
                            newExerciseId, newExerciseSetId
                        );
                        
                        ++nextExerciseSetOrderNumber;
                    }
                } else {
                    // old exercise
                    if (exercise.getOrderNumber() != nextExerciseOrderNumber) {
                        exercise.setOrderNumber(nextExerciseOrderNumber);

                        manager.updateExercise(
                            exercise.getId(),
                            exercise.getExerciseInfo().getId(),
                            exercise.getOrderNumber()
                        );
                    }
                    
                    // remove deleted exercise sets
                    if (!removedExerciseSetsIdMap.getOrDefault(
                            exercise.getId(), new ArrayList<>()).isEmpty()
                        ) {
                        manager.removeExerciseSets(
                            removedExerciseSetsIdMap.get(exercise.getId())
                        );
                    }
                    
                    int exerciseSetNextOrderNumber = 1;
                    for (ExerciseSet exerciseSet : exercise.getExerciseSetList()) {
                        exerciseSet.setOrderNumber(exerciseSetNextOrderNumber);
                        
                        if (exerciseSet.getId() < 0) {
                            // exercise set is new
                            int newExerciseSetId = manager.createExerciseSet(
                                exerciseSet.getWorkingSets(),
                                exerciseSet.getRepetitions(),
                                exerciseSet.getWorkingWeight(),
                                exerciseSet.getOrderNumber()
                            );
                            exerciseSet.setId(newExerciseSetId);
                            
                            manager.addExerciseSetToExercise(
                                exercise.getId(), newExerciseSetId
                            );
                        } else {
                            // exercise set is possibly edited
                            manager.updateExerciseSet(
                                exerciseSet.getId(),
                                exerciseSet.getWorkingSets(),
                                exerciseSet.getRepetitions(),
                                exerciseSet.getWorkingWeight(),
                                exerciseSet.getOrderNumber()
                            );
                        }
                        ++exerciseSetNextOrderNumber;
                    }
                }
                ++nextExerciseOrderNumber;
            }
            
            workout.get().getExerciseList().setAll(exerciseListView.getItems());
            close();
            
        } else if (optional.get() == noButton) {
            close();
        }
    }
    
    @FXML
    private void cancel() throws Exception {
        if (changeMadeProperty.get()) {
            Optional<ButtonType> optional = showCancelAlert();
            if (optional.get() == yesButton) {
                close();
            }
        } else {
            close();
        }
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

        showWindow(root);
    }
    
    @FXML
    private void editExercise() throws Exception {
        String resource = "/fxml/ExerciseSetList.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent root = loader.load();
        
        Exercise selectedExercise =
            exerciseListView.getSelectionModel().getSelectedItem();
        
        ExerciseSetListController controller = loader.getController();
        controller.setExercise(selectedExercise);
        
        removedExerciseSetsIdMap.putIfAbsent(
            selectedExercise.getId(), new ArrayList<>()
        );
        
        controller.setRemovedExerciseSetsIdList(
            removedExerciseSetsIdMap.get(selectedExercise.getId())
        );
        
        showWindow(root);
    }
    
    @FXML
    private void removeExercise() throws Exception {
        final int selectedListViewIndex =
            exerciseListView.getSelectionModel().getSelectedIndex();
        
        int newSelectedListViewIndex =
            (selectedListViewIndex == exerciseListView.getItems().size() - 1)
            ? (selectedListViewIndex - 1)
            : selectedListViewIndex;
        
        Exercise removedExercise =
            exerciseListView.getItems().remove(selectedListViewIndex);
        
        // if removed exercise is not newly created, save it so it can be later
        // possibly deleted from the database
        if (removedExercise.getId() > 0) {
            removedExercisesIdList.add(removedExercise.getId());
        }
        
        if (newSelectedListViewIndex >= 0) {
            exerciseListView.getSelectionModel()
                            .select(newSelectedListViewIndex);
        } else {
            exerciseListView.getSelectionModel().clearSelection();
        }
    }
    
    private void close() {
        exerciseListView.getScene().getWindow().hide();
    }
    
    private void showWindow(Parent root) {
        Stage stage = new Stage();
        stage.initOwner(exerciseListView.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL); 
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    private Optional<ButtonType> showSaveAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(workout.getName());
        alert.setContentText("Save the changes?");
        
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yesButton, noButton, cancelButton);
        
        return alert.showAndWait();
    }
    
    private Optional<ButtonType> showCancelAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(workout.getName());
        alert.setContentText("Cancel the changes?");
        
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yesButton, noButton);
        
        return alert.showAndWait();
    }
}
