package com.mycompany.application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * TODO
 * - change Exercise.name to ExerciseInfo
 * - make it so that exerciseInfo name & category can not be updated to whitespace & existing name/category
 * - add icons to buttons (edit, new, save...)
 * 
 * - extractors needed?
 * - observableArrayList / observableList?
 */
public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            //Parent root = (Parent)FXMLLoader.load(getClass().getResource("/fxml/ExerciseBaseList.fxml"));
            Parent root = (Parent)FXMLLoader.load(getClass().getResource("/fxml/ExerciseInfoEditor.fxml"));
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