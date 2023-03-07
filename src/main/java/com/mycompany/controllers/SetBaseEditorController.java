package com.mycompany.controllers;

import com.mycompany.domain.SetBase;
import com.mycompany.utilities.InputValidator;
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

public class SetBaseEditorController {
    
    @FXML private Label exerciseNameLabel;
    
    @FXML private Label workingSetsLbl;
    @FXML private Label repetitionsLbl;
    @FXML private Label workingWeightLbl;
    
    @FXML private TextField workingSetsTF;
    @FXML private TextField repetitionsTF;
    @FXML private TextField workingWeightTF;
    
    @FXML private Button okButton;
    
    private final ObjectProperty<SetBase> setBase =
        new SimpleObjectProperty<>();

    public ObjectProperty<SetBase> setBaseProperty() {
        return setBase;
    }
    
    public final void setSetBase(SetBase setBase) {
        setBaseProperty().set(setBase);
    }

    public void initialize() {
        setBaseProperty().addListener((obs, oldSetBase, newSetBase) -> {
            if (newSetBase != null) {
                workingSetsTF.setText(
                    String.valueOf(newSetBase.getWorkingSets())
                );
                repetitionsTF.setText(
                    String.valueOf(newSetBase.getRepetitions())
                );
                workingWeightTF.setText(
                    String.valueOf(newSetBase.getWorkingWeight())
                );
            }
        });
        
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
        
        // disable submit if a textfild is empty
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

    @FXML
    private void submit() {
        if (!areInputsValid()) {
            return;
        }
        
        int workingSets = Integer.valueOf(workingSetsTF.getText());
        int repetitions = Integer.valueOf(repetitionsTF.getText());
        double workingWeight = Double.valueOf(workingWeightTF.getText());
            
        if (setBase.get() == null) {
            // create a new SetBase
            setSetBase(new SetBase(workingSets, repetitions, workingWeight));
            
        } else {
            // edit existing SetBase
            setBase.get().setWorkingSets(workingSets);
            setBase.get().setRepetitions(repetitions);
            setBase.get().setWorkingWeight(workingWeight);
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
                workingSetsLbl.getText(),
                "Input must be non-negative integer value"
            );
            return false;
        }
        
        boolean isRepetitionsInputValid = InputValidator.isNonNegativeInteger(
            repetitionsTF.getText()
        );
        if (!isRepetitionsInputValid) {
            showErrorAlert(
                repetitionsLbl.getText(),
                "Input must be non-negative integer value"
            );
            return false;
        }
        
        boolean isWorkingWeightInputValid = InputValidator.isNonNegativeDouble(
            workingWeightTF.getText()
        );
        if (!isWorkingWeightInputValid) {
            showErrorAlert(
                workingWeightLbl.getText(),
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