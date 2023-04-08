package com.mycompany.application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class App extends Application {
    
    public static final String DATABASE_PATH = "jdbc:h2:./workoutLog-database";
    
    @Override
    public void start(Stage primaryStage) throws SQLException {
        init();
        
        try {
            Parent root = (Parent)FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void init() throws SQLException {
        // create tables if not exists
        String createExerciseInfoTable = "CREATE TABLE IF NOT EXISTS ExerciseInfo ("
                    + "id INTEGER AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR NOT NULL, "
                    + "category VARCHAR NOT NULL"
                    + ");";
        
        String createExerciseTable = "CREATE TABLE IF NOT EXISTS Exercise ("
                    + "id INTEGER AUTO_INCREMENT PRIMARY KEY, "
                    + "exerciseInfo_id INTEGER NOT NULL, "
                    + "FOREIGN KEY (exerciseInfo_id) REFERENCES ExerciseInfo(id), "
                    + "orderNumber INTEGER NOT NULL"
                    + ");";
        
        String createWorkoutTable = "CREATE TABLE IF NOT EXISTS Workout ("
                    + "id INTEGER AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR, "
                    + "date DATE NOT NULL, "
                    + "orderNumber INTEGER NOT NULL"
                    + ");";
        
        String createExerciseSetTable = "CREATE TABLE IF NOT EXISTS ExerciseSet ("
                    + "id INTEGER AUTO_INCREMENT PRIMARY KEY, "
                    + "workingSets INTEGER NOT NULL, "
                    + "repetitions INTEGER NOT NULL, "
                    + "workingWeight DOUBLE PRECISION NOT NULL, "
                    + "orderNumber INTEGER NOT NULL"
                    + ");";
        
        String createExerciseToExerciseSetTable = "CREATE TABLE IF NOT EXISTS ExerciseToExerciseSet ("
                    + "exercise_id INTEGER NOT NULL, "
                    + "exerciseSet_id INTEGER NOT NULL, "
                    + "FOREIGN KEY (exercise_id) REFERENCES Exercise(id), "
                    + "FOREIGN KEY (exerciseSet_id) REFERENCES ExerciseSet(id)"
                    + ");";
        
        String createWorkoutToExerciseTable = "CREATE TABLE IF NOT EXISTS WorkoutToExercise ("
                    + "workout_id INTEGER NOT NULL, "
                    + "exercise_id INTEGER NOT NULL, "
                    + "FOREIGN KEY (workout_id) REFERENCES Workout(id), "
                    + "FOREIGN KEY (exercise_id) REFERENCES Exercise(id) "
                    + ");";
        
        String[] ss = new String[]{
            createExerciseInfoTable,
            createExerciseTable,
            createWorkoutTable,
            createExerciseSetTable,
            createExerciseToExerciseSetTable,
            createWorkoutToExerciseTable
        };
        
        Connection connection = DriverManager.getConnection(DATABASE_PATH, "sa", "");
        for (String sql : ss) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql);
            }
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}