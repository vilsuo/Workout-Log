
package com.mycompany.controllers;

import com.mycompany.domain.ExerciseSet;
import com.mycompany.domain.templates.SmolovJrTemplate;
import com.mycompany.domain.templates.TrainingProgram;
import com.mycompany.domain.templates.Wendler531Template;
import com.mycompany.utilities.InputValidator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ExerciseSetTemplateCalculatorController {
    
    @FXML private ComboBox templateComboBox;
    
    @FXML private ComboBox weekComboBox;
    @FXML private ComboBox dayComboBox;
    @FXML private ComboBox incrementComboBox;
    
    @FXML private TextField oneRepetitionMaxTextField;
    @FXML private Button addButton;
    
    private final ListProperty<ExerciseSet> exerciseSetList =
        new SimpleListProperty<>();
    
    public ListProperty<ExerciseSet> exerciseSetListProperty() {
        return exerciseSetList;
    }
    
    private Map<TrainingProgram, List<String>> tPWeekCBValuesMap = new HashMap<>();

    public void initialize() {
        setUpComboBoxValues();
        setUpListeners();
    }
    
    private void setUpComboBoxValues() {
        templateComboBox.getItems().setAll(
            Arrays.asList(
                TrainingProgram.WENDLER_531,
                TrainingProgram.SMOLOV_JR
            )
        );
        templateComboBox.getSelectionModel().select(0);
        
        // Wendler531 cycle is 4 weeks, while SmolovJr only 3 weeks
        tPWeekCBValuesMap.put(
            TrainingProgram.WENDLER_531, Arrays.asList("1", "2", "3", "4")
        );
        tPWeekCBValuesMap.put(
            TrainingProgram.SMOLOV_JR, Arrays.asList("1", "2", "3")
        );
        
        weekComboBox.getItems().setAll(
            FXCollections.observableList(
                tPWeekCBValuesMap.get(
                    TrainingProgram.WENDLER_531
                )
            )
        );
        weekComboBox.getSelectionModel().select(0);
        
        // Wendler531 and SmolovJr both have 4 weekly training days
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
                    
                    weekComboBox.getItems().setAll(
                        tPWeekCBValuesMap.get(selectedTp)
                    );
                    weekComboBox.getSelectionModel().select(0);
                    
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
        
        close();
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
        addButton.getScene().getWindow().hide();
    }
}
