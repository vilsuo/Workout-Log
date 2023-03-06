
package com.mycompany.controllers;

import com.mycompany.domain.ExerciseBase;
import com.mycompany.domain.SetBase;
import com.mycompany.domain.types.Exercise;
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

public class ExerciseBaseListController {
    
    //@FXML private Label dayNameLbl;
    
    @FXML private ListView<ExerciseBase> exerciseBaseList;
    
    @FXML private Button editButton;
    @FXML private Button removeButton;
    
    public void initialize() {
        Callback<ExerciseBase, Observable[]> extractor = (ExerciseBase ExerciseBase) -> new Observable[] {
            ExerciseBase.setsProperty()
        };
        
        ObservableList<ExerciseBase> exerciseBases = FXCollections.observableArrayList(extractor);
         
        ExerciseBase bench = new ExerciseBase(Exercise.BENCH_PRESS);
        bench.addSet(new SetBase(1, 5, 75));
        bench.addSet(new SetBase(1, 3, 80));
        bench.addSet(new SetBase(5, 5, 85));
        
        ExerciseBase squat = new ExerciseBase(Exercise.BARBELL_SQUAT);
        squat.addSet(new SetBase(1, 5, 75));
        squat.addSet(new SetBase(1, 3, 85));
        squat.addSet(new SetBase(10, 3, 95));
        
        exerciseBases.addAll(bench, squat);
        
        exerciseBaseList.setItems(exerciseBases);
        
        // edit and remove buttons are disabled if an item is not selected in the listview
        editButton.disableProperty().bind(
                Bindings.isNull(exerciseBaseList.getSelectionModel().selectedItemProperty()));
        
        removeButton.disableProperty().bind(
                Bindings.isNull(exerciseBaseList.getSelectionModel().selectedItemProperty()));
    }
    
    @FXML
    private void newExerciseBase() throws Exception {
        /*
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SetBaseList.fxml"));
        Parent root = loader.load();

        SetBaseListController controller = loader.getController();
        
        controller.exerciseBaseProperty().addListener((obs, oldExerciseBase, newExerciseBase) -> {
            if (newExerciseBase != null) {
                exerciseBaseList.getItems().add(newExerciseBase);
            }
        });

        showEditorWindow(root);
        */
    }
    
    @FXML
    private void editExerciseBase() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SetBaseList.fxml"));
        Parent root = loader.load();

        SetBaseListController controller = loader.getController();

        ExerciseBase selectedItem = exerciseBaseList.getSelectionModel().getSelectedItem();
        controller.setExerciseBase(selectedItem);
        
        showSetBaseListWindow(root, selectedItem.getExercise().label);
    }
    
    @FXML
    private void removeExerciseBase() throws Exception {
        // remove button is active only if selected index is valid
        final int selectedIdx = exerciseBaseList.getSelectionModel().getSelectedIndex();
        
        int newSelectedIndex =
            (selectedIdx == exerciseBaseList.getItems().size() - 1)
                ? selectedIdx - 1
                : selectedIdx;
        
        exerciseBaseList.getItems().remove(selectedIdx);
        
        if (newSelectedIndex >= 0) {
            exerciseBaseList.getSelectionModel().select(newSelectedIndex);
        } else {
            exerciseBaseList.getSelectionModel().clearSelection();
        }
    }
    
    private void showSetBaseListWindow(Parent root, String title) {
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        //stage.setTitle(exerciseNameLbl.getText());
        stage.initOwner(exerciseBaseList.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL); 
        stage.setScene(scene);
        stage.showAndWait();
    }
}
