
package com.mycompany.controllers;

import com.mycompany.dao.ExerciseInfoDaoImpl;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.controls.ExerciseInfoEditingCell;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class ExerciseInfoEditorController {
    
    private final String databasePath = "jdbc:h2:./workoutLog-database";
    private ExerciseInfoDaoImpl database;
    
    @FXML private TableView<ExerciseInfo> exerciseInfoTV;
    @FXML private TableColumn<ExerciseInfo, String> nameColumn;
    @FXML private TableColumn<ExerciseInfo, String> categoryColumn;
    
    @FXML private TextField nameTF;
    @FXML private TextField categoryTF;
    
    @FXML private Button addButton;
    
    private ObservableList<ExerciseInfo> exerciseInfos;

    public void initialize() {
        // implements cell editing
        nameColumn.setCellFactory(param -> new ExerciseInfoEditingCell());
        nameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ExerciseInfo, String>>() {
            
            @Override
            public void handle(TableColumn.CellEditEvent<ExerciseInfo, String> event) {
                // check white space...
                
                // check if new value already exists...
                
                event.getTableView()
                     .getItems()
                     .get(event.getTablePosition().getRow())
                     .setName(event.getNewValue());
                
                // update to database...
                System.out.println("edited name");
            }
        });
        
        categoryColumn.setCellFactory(param -> new ExerciseInfoEditingCell());
        categoryColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ExerciseInfo, String>>() {
            
            @Override
            public void handle(TableColumn.CellEditEvent<ExerciseInfo, String> event) {
                // check white space...
                
                // check if new value already exists...
                
                event.getTableView()
                     .getItems()
                     .get(event.getTablePosition().getRow())
                     .setCategory(event.getNewValue());
                
                // update to database...
                System.out.println("edited category");
            }
        });
        
        exerciseInfos = FXCollections.observableList(getData());
        
        exerciseInfoTV.setItems(exerciseInfos);
        
        // temporary...
        exerciseInfos.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                
                while (change.next()) {
                    if (change.wasUpdated()) {
                        // update to database?
                       System.out.println("update!");
                       
                    }
                }
            }
        });
        
        // disable add button if a textfield is empty or contain only whitespaces
        BooleanBinding textFieldsFilled = new BooleanBinding() {
            {
                super.bind(nameTF.textProperty(),categoryTF.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return (nameTF.getText().isBlank()
                        || categoryTF.getText().isBlank());
            }
        };
        
        addButton.disableProperty().bind(textFieldsFilled);
    }
    
    // make this smarter?
    private List<ExerciseInfo> getData() {
        List<ExerciseInfo> data = new ArrayList<>();
        try {
            database = new ExerciseInfoDaoImpl(databasePath);
            data = database.getExerciseInfos();
        } catch (Exception e) {
            
        }
        
        return data;
    }
    
    @FXML
    private void add() {
        // function dispabled if name or category is black
        String name = nameTF.getText();
        String catecory = categoryTF.getText();
        
        try {
            int generatedKey = database.addExerciseInfo(name, catecory);
            if (generatedKey != -1) {
                exerciseInfos.add(new ExerciseInfo(generatedKey, name, catecory));
                System.out.println("added index: " + generatedKey);
                
            } else {
                // show error dialog
                showErrorAlert("Exercise already exists!");
            }
            
            nameTF.clear();
            categoryTF.clear();
            
        } catch (Exception e) {
            System.out.println("Error in add(): " + e.getMessage());
        }
    }
    
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // temporary...
    @FXML
    private void list() {
        try {
            System.out.println("Listing all ExerciseInfos in the database:");
            database.getExerciseInfos().forEach(
                exerciseInfo -> System.out.println(exerciseInfo)
            );
            
        } catch (Exception e) {
            System.out.println("Error in list(): " + e.getMessage());
        }
    }
}
