
package com.mycompany.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

    @FXML private BorderPane root;
    
    @FXML
    private void openExerciseInfoEditor() throws IOException {
        String resource = "/fxml/ExerciseInfoEditor.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent parent = loader.load();
        showWindowWithModal(parent);
        
        // refresh...
        
    }
    
    @FXML
    private void openStatistics() throws IOException {
        String resource = "/fxml/CategoryStatistics.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent parent = loader.load();
        showWindowWithoutModal(parent);
    }
 
    private void showWindowWithModal(Parent parent) {
        Stage stage = new Stage();
        stage.setTitle("Exercise Info Editor");
        stage.initOwner(root.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);

        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    private void showWindowWithoutModal(Parent parent) {
        Stage stage = new Stage();
        stage.setTitle("Exercise Info Editor");
        stage.initOwner(root.getScene().getWindow());
        
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }
}