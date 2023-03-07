
package com.mycompany.controllers;

import com.mycompany.domain.ExerciseBase;
import com.mycompany.domain.SetBase;
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
         
        ExerciseBase bench = new ExerciseBase("Bench Press");
        bench.addSet(new SetBase(1, 5, 75));
        bench.addSet(new SetBase(1, 3, 80));
        bench.addSet(new SetBase(5, 5, 85));
        
        ExerciseBase squat = new ExerciseBase("Barbell Squat");
        squat.addSet(new SetBase(1, 2, 3));
        squat.addSet(new SetBase(2, 3, 4));
        squat.addSet(new SetBase(3, 4, 5));
        squat.addSet(new SetBase(4, 5, 6));
        squat.addSet(new SetBase(5, 6, 7));
        squat.addSet(new SetBase(6, 7, 8));
        squat.addSet(new SetBase(7, 8, 9));
        squat.addSet(new SetBase(8, 9, 10));
        
        exerciseBases.addAll(bench, squat);
        
        exerciseBaseList.setItems(exerciseBases);
        
        // edit and remove buttons are disabled if an item is not selected in the listview
        editButton.disableProperty().bind(
            Bindings.isNull(
                exerciseBaseList.getSelectionModel().selectedItemProperty()
            )
        );
        
        removeButton.disableProperty().bind(
            Bindings.isNull(
                exerciseBaseList.getSelectionModel().selectedItemProperty()
            )
        );
    }
    
    @FXML
    private void newExerciseBase() throws Exception {
        String resource = "/fxml/ExerciseBaseCreator.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent root = loader.load();
        
        ExerciseBaseCreatorController controller = loader.getController();
        
        controller.exerciseBaseProperty().addListener(
            (obs, oldExerciseBase, newExerciseBase) -> {
                if (newExerciseBase != null) {
                    exerciseBaseList.getItems().add(newExerciseBase);
                }
            }
        );

        showWindow(root);
    }
    
    @FXML
    private void editExerciseBase() throws Exception {
        String resource = "/fxml/SetBaseList.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent root = loader.load();
        SetBaseListController controller = loader.getController();

        ExerciseBase selectedItem = exerciseBaseList.getSelectionModel()
                                                    .getSelectedItem();
        controller.setExerciseBase(selectedItem);
        
        showWindow(root);
    }
    
    @FXML
    private void removeExerciseBase() throws Exception {
        final int selectedIdx = exerciseBaseList.getSelectionModel()
                                                .getSelectedIndex();
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
    
    private void showWindow(Parent root) {
        Stage stage = new Stage();
        stage.initOwner(exerciseBaseList.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL); 
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
