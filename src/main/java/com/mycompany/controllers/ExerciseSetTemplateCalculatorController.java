
package com.mycompany.controllers;

import com.mycompany.domain.ExerciseSet;
import com.mycompany.domain.templates.SmolovJrTemplate;
import com.mycompany.domain.templates.TrainingProgram;
import com.mycompany.domain.templates.Wendler531Template;
import com.mycompany.utilities.InputValidator;
import java.util.Arrays;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ExerciseSetTemplateCalculatorController {
    
    @FXML private VBox root;
    
    @FXML private ComboBox templateComboBox;
    
    @FXML private ComboBox weekComboBox;
    @FXML private ComboBox dayComboBox;
    @FXML private ComboBox incrementComboBox;
    
    @FXML private TextField oneRepetitionMaxTextField;
    @FXML private Button addButton;
    
    private ListProperty<ExerciseSet> exerciseSetList =
        new SimpleListProperty<>();
    
    public ListProperty<ExerciseSet> exerciseSetListProperty() {
        return exerciseSetList;
    }

    public void initialize() {
        setUpListeners();
        setUpComboBoxValues();
    }
    
    private void setUpComboBoxValues() {
        templateComboBox.getItems().setAll(
            Arrays.asList(TrainingProgram.WENDLER_531, TrainingProgram.SMOLOV_JR)
        );
        templateComboBox.getSelectionModel().select(0);
        
        weekComboBox.getItems().setAll(
            FXCollections.observableList(Arrays.asList("1", "2", "3", "4"))
        );
        weekComboBox.getSelectionModel().select(0);
        
        dayComboBox.getItems().setAll(
            FXCollections.observableList(Arrays.asList("1", "2", "3", "4"))
        );
        dayComboBox.getSelectionModel().select(0);
        
        incrementComboBox.getItems().setAll(
            FXCollections.observableList(Arrays.asList("1.25", "2.5", "5.0"))
        );
        incrementComboBox.getSelectionModel().select(1);
    }
    
    private void setUpListeners() {
        oneRepetitionMaxTextField.textProperty().addListener(
            (obs, oldValue, newValue) -> {
                if (newValue != null) {
                    if (InputValidator.isNonNegativeDouble(newValue)) {
                        addButton.setDisable(false);
                        return;
                    }
                }
                addButton.setDisable(true);
            }
        );
        
        
        // In the Wendler531 training program, the day number only affects which
        // exercise is selected.
        templateComboBox.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldValue, newValue) -> {
                if (newValue != null) {
                    TrainingProgram selectedTp = (TrainingProgram) newValue;
                    boolean isWendler = selectedTp == TrainingProgram.WENDLER_531;
                    
                    dayComboBox.setDisable(isWendler);
                    incrementComboBox.setDisable(isWendler);
                }
            }
        );
    }
    
    @FXML
    private void addSets() {
        TrainingProgram selectedTp =
            (TrainingProgram) templateComboBox.getSelectionModel()
                                              .getSelectedItem();
        
        int weekNumber = Integer.parseInt(
            (String) weekComboBox.getSelectionModel().getSelectedItem()
        );
        
        double oneRepetitionMax = Double.parseDouble(
            oneRepetitionMaxTextField.getText()
        );
       
        if (selectedTp == TrainingProgram.WENDLER_531) {
            setWendler531(oneRepetitionMax, weekNumber);
            
        } else if (selectedTp == TrainingProgram.SMOLOV_JR) {
            setSmolovJR(oneRepetitionMax, weekNumber);
        }
        
        //close();
    }
    
    private void setWendler531(double oneRepetitionMax, int weekNumber) {
        exerciseSetList.set(
            Wendler531Template.createExerciseSetList(
                oneRepetitionMax, weekNumber
            )
        );
    }
    
    private void setSmolovJR(double oneRepetitionMax, int weekNumber) {
        int dayNumber = Integer.parseInt(
            (String) dayComboBox.getSelectionModel().getSelectedItem()
        );
        
        double weeklyIncrement = Double.parseDouble(
            (String) incrementComboBox.getSelectionModel().getSelectedItem()
        );
        
        exerciseSetList.set(
            SmolovJrTemplate.createExeciseSetList(
                oneRepetitionMax, weeklyIncrement, weekNumber, dayNumber
            )
        );
    }
    
    private void close() {
        root.getScene().getWindow().hide();
    }
}
