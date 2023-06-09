package com.mycompany.controllers;

import com.mycompany.domain.ExerciseSet;
import com.mycompany.utilities.InputValidator;
import com.mycompany.utilities.NumberGenerator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class ExerciseSetEditorController {
    
    @FXML private Label workingSetsLabel;
    @FXML private Label repetitionsLabel;
    @FXML private Label workingWeightLabel;
    
    @FXML private TextField workingSetsTF;
    @FXML private TextField repetitionsTF;
    @FXML private TextField workingWeightTF;
    
    @FXML private Button okButton;
    
    private final ObjectProperty<ExerciseSet> exerciseSet =
        new SimpleObjectProperty<>();

    public ObjectProperty<ExerciseSet> exerciseSetProperty() {
        return exerciseSet;
    }
    
    public final void setExerciseSet(ExerciseSet exerciseSet) {
        this.exerciseSet.set(exerciseSet);
    }

    public void initialize() {
        setUpListeners();
        setUpProperties();
        setUpShortCuts();
    }
    
    private void setUpListeners() {
        exerciseSetProperty().addListener(
            (obs, oldExerciseSet, newExerciseSet) -> {
                if (newExerciseSet != null) {
                    workingSetsTF.setText(
                        String.valueOf(newExerciseSet.getWorkingSets())
                    );
                    repetitionsTF.setText(
                        String.valueOf(newExerciseSet.getRepetitions())
                    );
                    workingWeightTF.setText(
                        String.valueOf(newExerciseSet.getWorkingWeight())
                    );
                }
            }
        );
    }
    
    private void setUpProperties() {
        okButton.disableProperty().bind(
            Bindings.or(
                workingSetsTF.textProperty().isEmpty(),
                Bindings.or(
                    repetitionsTF.textProperty().isEmpty(),
                    workingWeightTF.textProperty().isEmpty()
                )
            )
        );
    }
    
    private void setUpShortCuts() {
        // define enter shortcuts
        workingSetsTF.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                repetitionsTF.requestFocus();
            }
        });
        
        repetitionsTF.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                workingWeightTF.requestFocus();
            }
        });
        
        workingWeightTF.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                okButton.requestFocus();
            }
        });
    }

    @FXML
    private void submit() {
        if (!areInputsValid()) {
            return;
        }
        
        int workingSets = Integer.parseInt(workingSetsTF.getText());
        int repetitions = Integer.parseInt(repetitionsTF.getText());
        double workingWeight = Double.parseDouble(workingWeightTF.getText());
            
        if (exerciseSet.get() == null) {
            // add new
            try {
                int tempId = NumberGenerator.generateNextNegativeNumber();
                int tempOrderNumber = -1;
                
                setExerciseSet(
                    new ExerciseSet(
                        tempId,
                        workingSets,
                        repetitions,
                        workingWeight,
                        tempOrderNumber
                    )
                );
            } catch (Exception e) {
                System.out.println("Error in ExerciseSetEditorController.submit() add new: " + e.getMessage());
            }
        } else {
            // edit existing ExerciseSet
            try {
                exerciseSet.get().setWorkingSets(workingSets);
                exerciseSet.get().setRepetitions(repetitions);
                exerciseSet.get().setWorkingWeight(workingWeight);
            } catch (Exception e) {
                System.out.println("Error in ExerciseSetEditorController.submit() edit: " + e.getMessage());
            }
        }
        close();
    }
    
    @FXML
    private void cancel() {
        close();
    }
    
    private void close() {
        workingSetsTF.getScene().getWindow().hide();
    }
    
    private boolean areInputsValid() {
        boolean isWorkingSetsInputValid = InputValidator.isNonNegativeInteger(
            workingSetsTF.getText()
        );
        if (!isWorkingSetsInputValid) {
            showErrorAlert(
                workingSetsLabel.getText(),
                "Input must be non-negative integer value"
            );
            return false;
        }
        
        boolean isRepetitionsInputValid = InputValidator.isNonNegativeInteger(
            repetitionsTF.getText()
        );
        if (!isRepetitionsInputValid) {
            showErrorAlert(
                repetitionsLabel.getText(),
                "Input must be non-negative integer value"
            );
            return false;
        }
        
        boolean isWorkingWeightInputValid = InputValidator.isNonNegativeDouble(
            workingWeightTF.getText()
        );
        if (!isWorkingWeightInputValid) {
            showErrorAlert(
                workingWeightLabel.getText(),
                "Input must be non-negative integer/decimal value"
            );
            return false;
        }
        
        return true;
    }
    
    private void showErrorAlert(String textFieldText, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(
            "Invalid input given for field " + textFieldText
        );
        alert.setContentText(message);
        alert.showAndWait();
    }
}