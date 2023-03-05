package com.mycompany.controllers;

import com.mycompany.domain.SetBase;
import com.mycompany.utilities.InputValidator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class SetBaseEditorController {
    
    @FXML private Label workingSetsLbl;
    @FXML private Label repetitionsLbl;
    @FXML private Label workingWeightLbl;
    
    @FXML private TextField workingSetsTF;
    @FXML private TextField repetitionsTF;
    @FXML private TextField workingWeightTF;
    
    private final ObjectProperty<SetBase> setBase = new SimpleObjectProperty<>();

    public ObjectProperty<SetBase> setBaseProperty() {
        return setBase;
    }
    
    public final void setSetBase(SetBase setBase) {
        setBaseProperty().set(setBase);
    }

    public void initialize() {
        
        setBaseProperty().addListener((obs, oldSetBase, newSetBase) -> {
            if (newSetBase != null) {
                workingSetsTF.setText(String.valueOf(newSetBase.getWorkingSets()));
                repetitionsTF.setText(String.valueOf(newSetBase.getRepetitions()));
                workingWeightTF.setText(String.valueOf(newSetBase.getWorkingWeight()));
            }
        });
        
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
                submit();
            }
        });
        
    }

    @FXML
    private void submit() {
        if (!areInputsValid()) {
            return;
        }
        
        if (setBase.get() == null) {
            // add a new SetBase
            setSetBase(
                new SetBase(
                    Integer.valueOf(workingSetsTF.getText()),
                    Integer.valueOf(repetitionsTF.getText()),
                    Double.valueOf(workingWeightTF.getText())
                )
            );
        } else {
            // edit selected SetBase
            setBase.get().setWorkingSets(Integer.valueOf(workingSetsTF.getText()));
            setBase.get().setRepetitions(Integer.valueOf(repetitionsTF.getText()));
            setBase.get().setWorkingWeight(Double.valueOf(workingWeightTF.getText()));
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
        boolean isWorkingSetsInputValid = InputValidator.isNonNegativeInteger(workingSetsTF.getText());
        if (!isWorkingSetsInputValid) {
            showError(
                "Invalid input given for field " + workingSetsLbl.getText()
              + " Input must be non-negative integer value."
            );
            return false;
        }
        
        boolean isRepetitionsInputValid = InputValidator.isNonNegativeInteger(repetitionsTF.getText());
        if (!isRepetitionsInputValid) {
            showError(
                "Invalid input given for field " + repetitionsLbl.getText()
              + " Input must be non-negative integer value."
            );
            return false;
        }
        
        boolean isWorkingWeightInputValid = InputValidator.isNonNegativeDouble(workingWeightTF.getText());
        if (!isWorkingWeightInputValid) {
            showError(
                "Invalid input given for field " + workingWeightLbl.getText()
              + " Input must be non-negative integer/decimal value."
            );
            return false;
        }
        
        return true;
    }
    
    public void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}