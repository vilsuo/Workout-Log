package com.mycompany.application;

import com.mycompany.dao.ExerciseSetDaoImpl;
import java.util.Scanner;
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

    private static final Scanner scanner = new Scanner(System.in);
    private static final ExerciseSetDaoImpl database = new ExerciseSetDaoImpl();
    
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = (Parent)FXMLLoader.load(getClass().getResource("/fxml/ExerciseList.fxml"));
            //Parent root = (Parent)FXMLLoader.load(getClass().getResource("/fxml/ExerciseInfoEditor.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //launch(args);
        
        while (true) {
            System.out.println("");
            System.out.println("Enter command:");
            System.out.println("1) add exercise set");
            System.out.println("2) update exercise set");
            System.out.println("3) remove exercise set");
            System.out.println("4) list all exercise sets");
            System.out.println("x) quit");

            System.out.print("> ");
            String command = scanner.nextLine();
            
            if (command.equals("x")) {
                break;
            }
            
            switch (command) {
                case "1":
                    addExerciseSet();
                    break;
                
                case "2":
                    updateExerciseSet();
                    break;
                
                case "3":
                    removeExerciseSet();
                    break;
                
                case "4":
                    listExerciseSets();
                    break;
            }
        }

        System.out.println("Thank you!");
    }
    
    private static void addExerciseSet() {
        System.out.println("add:");
        System.out.println("give sets, reps, weight");
        int sets = Integer.parseInt(scanner.nextLine());
        int reps = Integer.parseInt(scanner.nextLine());
        double weight = Double.parseDouble(scanner.nextLine());
        
        try {
            database.create(sets, reps, weight);
        } catch (Exception e) {
            System.out.println("Error in add:" + e.getMessage());
        }
    }
    
    private static void updateExerciseSet() {
        System.out.println("update:");
        System.out.println("give id, sets, reps, weight");
        int id = Integer.parseInt(scanner.nextLine());
        int sets = Integer.parseInt(scanner.nextLine());
        int reps = Integer.parseInt(scanner.nextLine());
        double weight = Double.parseDouble(scanner.nextLine());
        
        try {
            database.update(id, sets, reps, weight);
        } catch (Exception e) {
            System.out.println("Error in update:" + e.getMessage());
        }
    }
    
    private static void removeExerciseSet() {
        System.out.println("remove:");
        System.out.println("give id");
        int id = Integer.parseInt(scanner.nextLine());
        
        try {
            database.remove(id);
        } catch (Exception e) {
            System.out.println("Error in remove:" + e.getMessage());
        }
    }
    
    private static void listExerciseSets() {
        System.out.println("list:");
        
        try {
            database.list().forEach(
                exerciseSet -> System.out.println(exerciseSet)
            );
        } catch (Exception e) {
            System.out.println("Error in list:" + e.getMessage());
        }
    }
}