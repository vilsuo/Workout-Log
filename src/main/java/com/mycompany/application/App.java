package com.mycompany.application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * TODO
 * - ExerciseInfoEditorController.delete()
 *      - remove also ExerciseSets associated with the id
 * 
 * - ExerciseListController.removeExercise()
 *      - remove exercise from database and the sets associated with it
 * 
 * - add icons to buttons (edit, new, save...)
 * 
 * - extractors needed?
 * - observableArrayList / observableList?
 */

public class App extends Application {
    
    public static final String DATABASE_PATH = "jdbc:h2:./workoutLog-database";
    
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = (Parent)FXMLLoader.load(getClass().getResource("/fxml/ExerciseList.fxml"));
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