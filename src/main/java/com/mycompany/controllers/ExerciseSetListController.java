package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.cells.DragAndDropListCell;
import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseSet;
import com.mycompany.dao.ExerciseManagerImpl;
import java.util.HashMap;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ExerciseSetListController {
    
    ExerciseManagerImpl manager = new ExerciseManagerImpl(App.DATABASE_PATH);
    
    @FXML private Label exerciseInfoNameLabel;
    
    @FXML private ListView<ExerciseSet> exerciseSetListView;
    
    @FXML private Button saveButton;
    @FXML private Button editButton;
    @FXML private Button removeButton;
    
    private final ButtonType yesButton = new ButtonType("Yes");
    private final ButtonType noButton = new ButtonType("No");
    private final ButtonType backButton = new ButtonType("Back");
    
    private final ObjectProperty<Exercise> exercise =
        new SimpleObjectProperty<>();
    
    public ObjectProperty<Exercise> exerciseProperty() {
        return exercise;
    }
    
    public final void setExercise(Exercise exercise) {
        exerciseProperty().set(exercise);
    }
    
    private final BooleanProperty changeMadeProperty =
        new SimpleBooleanProperty(false);
    
    private final Map<Integer, ExerciseSet> removedExerciseSetsMap = new HashMap<>();
    
    public void initialize() {
        exerciseSetListView.setCellFactory(param -> new DragAndDropListCell(ExerciseSet.class));
        setUpListeners();
        setUpProperties();
    }
    
    private void setUpListeners() {
        exercise.addListener((obs, oldExercise, newExercise) -> {
            if (newExercise != null) {
                // needed?
                // list created so it fires updates if ExerciseSet changes:
                Callback<ExerciseSet, Observable[]> extractor =
                    (ExerciseSet exerciseSet) -> new Observable[] {
                        exerciseSet.idProperty(),
                        exerciseSet.workingSetsProperty(),
                        exerciseSet.repetitionsProperty(),
                        exerciseSet.workingWeightProperty()
                    };
                
                ObservableList<ExerciseSet> copiedExerciseSetList =
                    FXCollections.observableArrayList(extractor);
                
                for (ExerciseSet exerciseSet : newExercise.getExerciseSetList()) {
                    ExerciseSet copiedExerciseSet = new ExerciseSet(
                        exerciseSet.getId(),
                        exerciseSet.getWorkingSets(),
                        exerciseSet.getRepetitions(),
                        exerciseSet.getWorkingWeight(),
                        exerciseSet.getOrderNumber()
                    );
                    copiedExerciseSetList.add(copiedExerciseSet);
                }
                
                copiedExerciseSetList.addListener(new ListChangeListener() {

                    @Override
                    public void onChanged(ListChangeListener.Change change) {
                        changeMadeProperty.set(true);
                    }
                });
                
                exerciseSetListView.setItems(copiedExerciseSetList);
                exerciseInfoNameLabel.setText(
                    newExercise.getExerciseInfo().getName()
                );
            }
        });
    }
    
    private void setUpProperties() {
        // saving is disabled if no changes has been made
        saveButton.disableProperty().bind(Bindings.not(changeMadeProperty));
        
        // editing is disabled if no items are selected
        editButton.disableProperty().bind(
            Bindings.isNull(
                exerciseSetListView.getSelectionModel().selectedItemProperty()
            )
        );
        
        // removing is disabled if no items are selected
        removeButton.disableProperty().bind(
            Bindings.isNull(
                exerciseSetListView.getSelectionModel().selectedItemProperty()
            )
        );
    }
    
    @FXML
    private void save() throws Exception {
        Optional<ButtonType> optional = showSaveAlert();
        if (optional.get() == yesButton) {
            
            // handle removed ExerciseSets:
            for (int exerciseSetId : removedExerciseSetsMap.keySet()) {
                // if removed set was not new, remove it from the database
                if (exerciseSetId > 0) {
                    // slow... maybe implement method to remove all at a time
                    manager.removeExerciseSet(
                        removedExerciseSetsMap.get(exerciseSetId).getId()
                    );
                }
            }
            
            int orderNumber = 1;
            for (ExerciseSet exerciseSet : exerciseSetListView.getItems()) {
                if (exerciseSet.getId() < 0) {
                    // new exercise set
                    int exerciseSetId = manager.createExerciseSet(
                        exerciseSet.getWorkingSets(),
                        exerciseSet.getRepetitions(),
                        exerciseSet.getWorkingWeight(),
                        orderNumber
                    );
                    exerciseSet.setId(exerciseSetId);
                    manager.addExerciseSetToExercise(exercise.get().getId(), exerciseSet.getId());
                    
                } else {
                    // (edited) exerciseSet
                    // slow... maybe check if
                    // 1) exercise set was not edited
                    // 2) order number did not change
                    // then no need for update
                   manager.updateExerciseSet(
                        exerciseSet.getId(),
                        exerciseSet.getWorkingSets(),
                        exerciseSet.getRepetitions(),
                        exerciseSet.getWorkingWeight(),
                        orderNumber
                    );
                }
                exerciseSet.setOrderNumber(orderNumber);
                ++orderNumber;
            }
            
            exercise.get().getExerciseSetList().setAll(exerciseSetListView.getItems());
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
    private void createNew() throws Exception {
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
    private void edit() throws Exception {
        String resource = "/fxml/ExerciseSetEditor.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent root = loader.load();

        ExerciseSetEditorController controller = loader.getController();

        ExerciseSet selectedItem = 
            exerciseSetListView.getSelectionModel().getSelectedItem();
        
        controller.setExerciseSet(selectedItem);
        
        showEditorWindow(root);
    }
    
    @FXML
    private void remove() {
        final int selectedListViewIndex =
            exerciseSetListView.getSelectionModel().getSelectedIndex();
        
        int newSelectedListViewIndex =
            (selectedListViewIndex == (exerciseSetListView.getItems().size() - 1))
                ? (selectedListViewIndex - 1)
                : selectedListViewIndex;
        
        ExerciseSet removedExerciseSet =
            exerciseSetListView.getItems().remove(selectedListViewIndex);
        
        removedExerciseSetsMap.put(removedExerciseSet.getId(), removedExerciseSet);
        
        if (newSelectedListViewIndex >= 0) {
            exerciseSetListView.getSelectionModel()
                               .select(newSelectedListViewIndex);
        } else {
            exerciseSetListView.getSelectionModel().clearSelection();
        }
    }
    
    private void close() {
        exerciseInfoNameLabel.getScene().getWindow().hide();
    }

    private void showEditorWindow(Parent root) {
        Stage stage = new Stage();
        stage.setTitle(exerciseInfoNameLabel.getText() + " set editor");
        stage.initOwner(exerciseSetListView.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    private Optional<ButtonType> showSaveAlert() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(exerciseInfoNameLabel.getText());
        alert.setContentText("Do you want to save the changes?");
        
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yesButton, noButton, backButton);
        
        return alert.showAndWait();
    }
    
    private Optional<ButtonType> showCancelAlert() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(exerciseInfoNameLabel.getText());
        alert.setContentText("Do you want to cancel the changes?");
        
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yesButton, noButton);
        
        return alert.showAndWait();
    }
}