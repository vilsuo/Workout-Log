package com.mycompany.application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;


public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = (Parent)FXMLLoader.load(getClass().getResource("/fxml/ExerciseBaseList.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}