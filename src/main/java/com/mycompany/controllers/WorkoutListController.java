
package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Exercise;
import com.mycompany.domain.Workout;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class WorkoutListController {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    
    @FXML private DatePicker datePicker;
    
    @FXML private ListView<Workout> workoutListView;
    
    @FXML private Button editButton;
    @FXML private Button removeButton;
    
    public void initialize() {
        setUpProperties();
        
        final Callback<DatePicker, DateCell> dayCellFactory = 
            (final DatePicker dp) -> new DateCell() {
                
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    try {
                        if (manager.getWorkoutDates().contains(Date.valueOf(item))) {
                            setStyle("-fx-background-color: #ffc0cb;");   
                        }
                    } catch (SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            };
        datePicker.setDayCellFactory(dayCellFactory);
        
        datePicker.valueProperty().addListener((obs, oldLocalDate, newLocalDate) -> {
            try {
                if (newLocalDate != null) {
                    ObservableList workoutList = FXCollections.observableArrayList(
                        manager.getWorkoutsByDate(Date.valueOf(newLocalDate))
                    );
                    workoutListView.getItems().setAll(workoutList);
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        });
        
        datePicker.setValue(LocalDate.now());
    }
    
    private void setUpProperties() {
        editButton.disableProperty().bind(
            Bindings.isNull(workoutListView.getSelectionModel().selectedItemProperty())
        );
        
        removeButton.disableProperty().bind(
            Bindings.isNull(workoutListView.getSelectionModel().selectedItemProperty())
        );
    }
    
    @FXML
    private void newWorkout() throws Exception {
        String name = "temporary workout name!";
        Date date = Date.valueOf(datePicker.getValue());
        int workoutOrderNumber = workoutListView.getItems().size() + 1;
        int workoutId = manager.createWorkout(name, date, workoutOrderNumber);
        
        workoutListView.getItems().add(
            new Workout(
                workoutId,
                date,
                workoutOrderNumber
            )
        );
    }
    
    @FXML
    private void editWorkout() throws Exception {
        String resource = "/fxml/ExerciseList.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent root = loader.load();
        
        ExerciseListController controller = loader.getController();
        Workout selectedWorkout = workoutListView.getSelectionModel().getSelectedItem();
        controller.setWorkout(selectedWorkout);
        
        showWindow(root, "Exercise List Editor");
    }
    
    @FXML
    private void removeWorkout() throws Exception {
        //1. remove WorkoutToExercise
        
        //2. remove ExerciseToExerciseSet
        
        //3./4. remove Exercise/ExerciseSet
        
    }
    
    private void showWindow(Parent root, String title) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initOwner(workoutListView.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL); 
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    @FXML
    private void previousDate() {
        if (datePicker.getValue() != null) {
            datePicker.setValue(datePicker.getValue().minusDays(1));
        }
    }
    
    @FXML
    private void nextDate() {
        if (datePicker.getValue() != null) {
            datePicker.setValue(datePicker.getValue().plusDays(1));
        }
    }
}
