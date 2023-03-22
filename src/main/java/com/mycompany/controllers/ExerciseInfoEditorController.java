
package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.cells.ExerciseInfoEditingCell;
import com.mycompany.dao.ExerciseManagerImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class ExerciseInfoEditorController {
    
    private ExerciseManagerImpl manager = new ExerciseManagerImpl(App.DATABASE_PATH);
    
    @FXML private TableView<ExerciseInfo> exerciseInfoTableView;
    @FXML private TableColumn<ExerciseInfo, String> nameColumn;
    @FXML private TableColumn<ExerciseInfo, String> categoryColumn;
    
    @FXML private TextField nameTextField;
    @FXML private TextField categoryTextField;
    
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    
    private final ButtonType yesButton = new ButtonType("Yes");
    private final ButtonType noButton = new ButtonType("No");
    private final ButtonType backButton = new ButtonType("Back");

    public void initialize() {
        setUpProperties();
        setUpTableViewColumns();
        setUpTableViewData();
    }
    
    private void setUpProperties() {
        BooleanBinding textFieldsNotFilled = new BooleanBinding() {
            {
                super.bind(nameTextField.textProperty(), categoryTextField.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return (nameTextField.getText().isBlank() || categoryTextField.getText().isBlank());
            }
        };
        
        // disable 'add' if entry is blank
        addButton.disableProperty().bind(textFieldsNotFilled);
        
        // disable 'delete' if no items are selected
        deleteButton.disableProperty().bind(
            exerciseInfoTableView.getSelectionModel().selectedItemProperty().isNull()
        );
    }
    
    private void setUpTableViewColumns() {
        nameColumn.setCellFactory(param -> new ExerciseInfoEditingCell());
        categoryColumn.setCellFactory(param -> new ExerciseInfoEditingCell());
         
        nameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ExerciseInfo, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<ExerciseInfo, String> event) {
                String newName = event.getNewValue();
                if (newName.isBlank()) {
                    showErrorAlert("Field '" + nameColumn.getText() + "' can not be blank!");
                    exerciseInfoTableView.refresh();
                    return;
                    
                // exerciseInfoTableView is always up to date
                } else if (isExerciseNameTaken(newName)) {
                    showErrorAlert("Exercise with name a '" + newName + "' already exists!");
                    exerciseInfoTableView.refresh();
                    return;
                }
                
                try {
                    ExerciseInfo editedExerciseInfo = event.getTableView().getItems().get(event.getTablePosition().getRow());
                
                    manager.updateExerciseInfoName(editedExerciseInfo.getId(), newName);
                    editedExerciseInfo.setName(newName);
                    
                } catch (Exception e){
                    System.out.println("Error in ExerciseInfoEditorController.setUpTableViewColumns() nameColumn: " + e.getMessage());
                }
            }
        });
        
        categoryColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ExerciseInfo, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<ExerciseInfo, String> event) {
                String newCategory = event.getNewValue();
                if (newCategory.isBlank()) {
                    showErrorAlert("Field '" + categoryColumn.getText() + "' can not be blank!");
                    exerciseInfoTableView.refresh();
                    return;
                }
                
                try {
                    ExerciseInfo editedExerciseInfo = event.getTableView().getItems().get(event.getTablePosition().getRow());
                
                    manager.updateExerciseInfoCategory(editedExerciseInfo.getId(), newCategory);
                    editedExerciseInfo.setCategory(newCategory);
                    
                } catch (Exception e){
                    System.out.println(
                        "Error in ExerciseInfoEditorController.setUpTableViewColumns() categoryColumn: " + e.getMessage()
                    );
                }
            }
        });
    }
    
    private void setUpTableViewData() {
        Callback<ExerciseInfo, Observable[]> extractor = (ExerciseInfo exerciseInfo) -> new Observable[] {
            exerciseInfo.idProperty(),
            exerciseInfo.nameProperty(),
            exerciseInfo.categoryProperty()
        };
        ObservableList<ExerciseInfo> exerciseInfoList = FXCollections.observableList(loadExerciseInfoList(), extractor);
        exerciseInfoTableView.setItems(exerciseInfoList);
    }
    
    private List<ExerciseInfo> loadExerciseInfoList() {
        List<ExerciseInfo> data = new ArrayList<>();
        try {
            data = manager.getAllExerciseInfos();
        } catch (Exception e) {
            System.out.println("error in ExerciseInfoEditorController.loadExerciseInfoList(): " + e.getMessage());
        }
        return data;
    }
    
    private boolean isExerciseNameTaken(String exerciseName) {
        long count = exerciseInfoTableView.getItems().stream()
                                .map(exerciseInfo -> exerciseInfo.getName())
                                .filter(name -> name.equals(exerciseName))
                                .count();
        return (count != 0);
    }
    
    @FXML
    private void add() {
        String name = nameTextField.getText();
        String catecory = categoryTextField.getText();
        try {
            // try to create a new ExerciseInfo
            int generatedKey = manager.createExerciseInfo(name, catecory);
            if (generatedKey != -1) {
                // created successfully, now add the item also to the table view
                exerciseInfoTableView.getItems().add(new ExerciseInfo(generatedKey, name, catecory));
                nameTextField.clear();
                categoryTextField.clear();
            } else {
                showErrorAlert("Exercise with name a '" + name + "' already exists!");
            }
            
        } catch (Exception e) {
            System.out.println("Error in ExerciseInfoEditorController.add(): " + e.getMessage());
        }
    }
    
    @FXML
    private void delete() {
        ExerciseInfo selectedItem = exerciseInfoTableView.getSelectionModel().getSelectedItem();
        Optional<ButtonType> optional = showDeleteAlert(selectedItem);
        if (optional.get() == yesButton) {
            try {
                int exerciseInfoId = selectedItem.getId();
                
                // remove ExerciseInfo and all Exercises and ExerciseSets associated with it
                manager.removeExerciseInfo(exerciseInfoId);
                
                // remove ExerciseInfo also from the table
                exerciseInfoTableView.getItems().remove(exerciseInfoTableView.getSelectionModel().getSelectedIndex());
                
            } catch (Exception e) {
                System.out.println(
                    "Error in ExerciseInfoEditorController.delete(): " + e.getMessage()
                );
            }
        }
    }
    
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private Optional<ButtonType> showDeleteAlert(ExerciseInfo exerciseInfo) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(exerciseInfo.getName());
        alert.setContentText("Do you want to delete the item? Deleting the item"
                           + " will also delete all exercise records associated"
                           + " with it.");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yesButton, noButton, backButton);
        
        return alert.showAndWait();
    }
}
