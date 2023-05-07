package com.mycompany.application;

import com.mycompany.domain.Workout;
import com.mycompany.events.DoubleClickEventDispatcher;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/*
TODO
- WorkoutListView
    - smolov jr / wendler531 templates

- rep range graph progression
- category workout pie chart (exercises inside one category)
*/
public class App extends Application {
    
    public static final String DATABASE_PATH = "jdbc:h2:./workoutLog-database";
    
    @Override
    public void start(Stage primaryStage) throws SQLException {
        init();
        
        Workout w1 = new Workout(
            1, "Workout 1", Date.valueOf(LocalDate.of(2023, Month.JANUARY, 1)), 1
        );
        Workout w2 = new Workout(
            2, "Workout 1", Date.valueOf(LocalDate.of(2023, Month.MAY, 7)), 2
        );
        
        System.out.println(w1.compareTo(w2));
        
        try {
            Parent root = (Parent)FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
            Scene scene = new Scene(root);
            
            // SETTING CUSTOM EVENT DISPATCHER TO SCENE
            scene.setEventDispatcher(
                new DoubleClickEventDispatcher(scene.getEventDispatcher())
            );
            
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