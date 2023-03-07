package com.mycompany.controllers;

import com.mycompany.domain.ExerciseBase;
import com.mycompany.domain.SetBase;
import com.mycompany.domain.SetBaseCell;
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

public class SetBaseListController {
    
    @FXML private Label exerciseNameLbl;
    
    @FXML private ListView<SetBase> setBaseList;
    
    @FXML private Button editButton;
    @FXML private Button removeButton;
    
    private final ObjectProperty<ExerciseBase> exerciseBase = new SimpleObjectProperty<>();
    
    public ObjectProperty<ExerciseBase> exerciseBaseProperty() {
        return exerciseBase;
    }
    
    public final void setExerciseBase(ExerciseBase exerciseBase) {
        exerciseBaseProperty().set(exerciseBase);
    }

    public void initialize() {
        setBaseList.setCellFactory(param -> new SetBaseCell());
        
        exerciseBase.addListener((obs, oldExerciseBase, newExerciseBase) -> {
            if (newExerciseBase != null) {
                setBaseList.setItems(newExerciseBase.getSets());
                exerciseNameLbl.setText(newExerciseBase.getExerciseName().label);
            }
        });
        
        editButton.disableProperty().bind(
                Bindings.isNull(setBaseList.getSelectionModel().selectedItemProperty()));
        
        removeButton.disableProperty().bind(
                Bindings.isNull(setBaseList.getSelectionModel().selectedItemProperty()));
    }

    @FXML
    private void newSetBase() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SetBaseEditor.fxml"));
        Parent root = loader.load();

        SetBaseEditorController controller = loader.getController();
        
        controller.setBaseProperty().addListener((obs, oldSetBase, newSetBase) -> {
            if (newSetBase != null) {
                setBaseList.getItems().add(newSetBase);
            }
        });

        showEditorWindow(root);
    }


    @FXML
    private void editSetBase() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SetBaseEditor.fxml"));
        Parent root = loader.load();

        SetBaseEditorController controller = loader.getController();

        SetBase selectedItem = setBaseList.getSelectionModel().getSelectedItem();
        controller.setSetBase(selectedItem);
        
        showEditorWindow(root);
    }
    
    @FXML
    private void removeSetBase() {
        // remove button is active only if selected index is valid
        final int selectedIdx = setBaseList.getSelectionModel().getSelectedIndex();
        
        int newSelectedIndex =
            (selectedIdx == setBaseList.getItems().size() - 1)
                ? selectedIdx - 1
                : selectedIdx;
        
        setBaseList.getItems().remove(selectedIdx);
        
        if (newSelectedIndex >= 0) {
            setBaseList.getSelectionModel().select(newSelectedIndex);
            
        } else {
            setBaseList.getSelectionModel().clearSelection();
        }
    }

    private void showEditorWindow(Parent root) {
        Stage stage = new Stage();
        stage.setTitle(exerciseNameLbl.getText());
        stage.initOwner(setBaseList.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }
}