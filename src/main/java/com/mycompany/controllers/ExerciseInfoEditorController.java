
package com.mycompany.controllers;

import com.mycompany.dao.ExerciseInfoDaoImpl;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.cells.ExerciseInfoEditingCell;
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
    
    private ExerciseInfoDaoImpl database;
    
    @FXML private TableView<ExerciseInfo> exerciseInfoTV;
    @FXML private TableColumn<ExerciseInfo, String> nameColumn;
    @FXML private TableColumn<ExerciseInfo, String> categoryColumn;
    
    @FXML private TextField nameTF;
    @FXML private TextField categoryTF;
    
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    
    private ObservableList<ExerciseInfo> exerciseInfos;
    
    private final ButtonType yesButton = new ButtonType("Yes");
    private final ButtonType noButton = new ButtonType("No");
    private final ButtonType backButton = new ButtonType("Back");

    public void initialize() {
        setUpTableViewColumns();
        setUpTableViewData();
        
        BooleanBinding textFieldsNotFilled = new BooleanBinding() {
            {
                super.bind(nameTF.textProperty(), categoryTF.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return (nameTF.getText().isBlank()
                        || categoryTF.getText().isBlank());
            }
        };
        
        // disable 'add' if entry is blank
        addButton.disableProperty().bind(textFieldsNotFilled);
        
        // disable 'delete' if no items are selected
        deleteButton.disableProperty().bind(
            exerciseInfoTV.getSelectionModel().selectedItemProperty().isNull()
        );
    }
    
    private void setUpTableViewColumns() {
        nameColumn.setCellFactory(param -> new ExerciseInfoEditingCell());
        nameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ExerciseInfo, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<ExerciseInfo, String> event) {
                String newName = event.getNewValue();
                
                if (newName.isBlank()) {
                    showErrorAlert(
                        "Field '" + nameColumn.getText() + "' can not be blank!"
                    );
                    exerciseInfoTV.refresh();
                    return;
                    
                } else if (isExerciseNameTaken(newName)) {
                    showErrorAlert(
                        "Exercise with name a '" + newName + "' already exists!"
                    );
                    return;
                }
                
                try {
                    ExerciseInfo edited = event.getTableView().getItems()
                                        .get(event.getTablePosition().getRow());
                
                    database.updateName(edited.getId(), newName);
                    edited.setName(newName);
                    
                } catch (Exception e){
                    // log: update name was not successful...
                    System.out.println("update name was not successful");
                }
            }
        });
        
        categoryColumn.setCellFactory(param -> new ExerciseInfoEditingCell());
        categoryColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ExerciseInfo, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<ExerciseInfo, String> event) {
                String newCategory = event.getNewValue();
                if (newCategory.isBlank()) {
                    showErrorAlert(
                        "Field '" + categoryColumn.getText() + "' can not be blank!"
                    );
                    exerciseInfoTV.refresh();
                    return;
                }
                
                try {
                    ExerciseInfo edited = event.getTableView().getItems()
                                        .get(event.getTablePosition().getRow());
                
                    database.updateCategory(edited.getId(), newCategory);
                    edited.setCategory(newCategory);
                    
                } catch (Exception e){
                    // log: update category was not successful...
                    System.out.println("update category was not successful");
                }
            }
        });
    }
    
    private void setUpTableViewData() {
        Callback<ExerciseInfo, Observable[]> extractor =
            (ExerciseInfo exerciseInfo) -> new Observable[] {
                exerciseInfo.idProperty(),
                exerciseInfo.nameProperty(),
                exerciseInfo.categoryProperty()
            };
        exerciseInfos = FXCollections.observableList(getData(), extractor);
        exerciseInfoTV.setItems(exerciseInfos);
    }
    
    private List<ExerciseInfo> getData() {
        List<ExerciseInfo> data = new ArrayList<>();
        try {
            database = new ExerciseInfoDaoImpl();
            data = database.getItems();
            
        } catch (Exception e) {
            // log: loading database was not successful...
            System.out.println("loading database was not successful");
        }
        return data;
    }
    
    private boolean isExerciseNameTaken(String exerciseName) {
        long count = exerciseInfoTV.getItems().stream()
                                .map(exerciseInfo -> exerciseInfo.getName())
                                .filter(name -> name.equals(exerciseName))
                                .count();
        return (count != 0);
    }
    
    @FXML
    private void add() {
        String name = nameTF.getText();
        String catecory = categoryTF.getText();
        try {
            int generatedKey = database.create(name, catecory);
            if (generatedKey != -1) {
                exerciseInfos.add(
                    new ExerciseInfo(generatedKey, name, catecory)
                );
                
                nameTF.clear();
                categoryTF.clear();
                
            } else {
                showErrorAlert(
                    "Exercise with name a '" + name + "' already exists!"
                );
            }
            
        } catch (Exception e) {
            // log: adding was not successful...
            System.out.println("Error in add(): " + e.getMessage());
        }
    }
    
    @FXML
    private void delete() {
        ExerciseInfo selectedItem = exerciseInfoTV.getSelectionModel()
                                                  .getSelectedItem();
        Optional<ButtonType> optional = showDeleteAlert(selectedItem);
        if (optional.get() == yesButton) {
            try {
                int exerciseInfoId = selectedItem.getId();
                
                database.remove(exerciseInfoId);
                exerciseInfoTV.getItems().remove(
                    exerciseInfoTV.getSelectionModel().getSelectedIndex()
                );
                
                // TODO!!!
                // remove exerciseSets with exerciseInfoId and exercises with the sets
                
                System.out.println("deleted successfully");
                
            } catch (Exception e) {
                // log: deleting exercise was not successful..
                System.out.println("deleting exercise was not successful");
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
        alert.setContentText("Do you want to delete the exercise? Deleting the "
                           + "exercise will also delete all entries from the "
                           + "database.");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yesButton, noButton, backButton);
        
        return alert.showAndWait();
    }
}
