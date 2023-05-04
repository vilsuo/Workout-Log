
package com.mycompany.controllers;

import com.mycompany.application.App;
import com.mycompany.cells.DragAndDropListCell;
import com.mycompany.cells.WorkoutDateCell;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Workout;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/*
TODO
- refresh when returning from exercise info editing window
*/
public class WorkoutListController {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    
    private List<Date> dateList = new ArrayList<>();
    
    @FXML private DatePicker datePicker;
    @FXML private ListView<Workout> workoutListView;
    @FXML private Button editButton;
    @FXML private Button removeButton;
    
    public void initialize() {
        workoutListView.setCellFactory(
            param -> new DragAndDropListCell(Workout.class)
        );
        
        setUpDatePicker();
        setUpButtonProperties();
    }
    
    private void setUpDatePicker() {
        try {
            dateList = manager.getAllWorkoutDates();
        } catch (SQLException e) {
            System.out.println(
                "Error in WorkoutListController.initialize(): " + e.getMessage()
            );
        }
        
        datePicker.setDayCellFactory(
            param -> new WorkoutDateCell(dateList)
        );
        
        datePicker.valueProperty().addListener(
            (obs, oldLocalDate, newLocalDate) -> {
                if (newLocalDate != null) {
                    Date newDate = Date.valueOf(newLocalDate);
                    workoutListView.setItems(createWorkoutList(newDate));
                }
            }
        );
            
        datePicker.setValue(LocalDate.now());
    }
    
    public void refreshCurrentWorkout() {
        if (datePicker.getValue() != null) {
            Date currentDate = Date.valueOf(datePicker.getValue());
            workoutListView.setItems(createWorkoutList(currentDate));
        }
    }
    
    private ObservableList<Workout> createWorkoutList(Date newDate) {
        Callback<Workout, Observable[]> extractor =
            (Workout workout) -> new Observable[] {
                workout.idProperty(),
                workout.nameProperty(),
                workout.exerciseListProperty(),
                workout.dateProperty(),
                workout.orderNumberProperty()
            };
        
        try {
            ObservableList<Workout> workoutList =
                FXCollections.observableList(
                    manager.getWorkoutsByDate(newDate), extractor
                );
            
            // update workout order numbers when removing and after drag & drop
            workoutList.addListener(
                new ListChangeListener() {
                    @Override
                    public void onChanged(ListChangeListener.Change change) {
                        while (change.next()) {
                            if (change.wasRemoved() || change.wasReplaced()) {
                                try {
                                    // update order numbers in database
                                    manager.updateWorkoutOrderNumbers(
                                        workoutList
                                    );
                                    
                                    // update order numbers in screen
                                    for (int i = 0; i < workoutList.size(); ++i) {
                                        workoutList.get(i).setOrderNumber(i + 1);
                                    }
                                    
                                } catch (SQLException e) {
                                    System.out.println(
                                        "Error updating workout order numbers: "
                                        + e.getMessage()
                                    );
                                }
                            }
                        }
                    }
                }
            );
            return workoutList;
            
        } catch (SQLException e) {
            System.out.println(
                "Error in WorkoutListController.createWorkoutList: "
                + e.getMessage()
            );
        }
        return FXCollections.observableArrayList();
    }
    
    private void setUpButtonProperties() {
        editButton.disableProperty().bind(
            Bindings.isNull(
                workoutListView.getSelectionModel().selectedItemProperty()
            )
        );
        
        removeButton.disableProperty().bind(
            Bindings.isNull(
                workoutListView.getSelectionModel().selectedItemProperty()
            )
        );
    }
    
    @FXML
    private void newWorkout() throws Exception {
        Date workoutDate = Date.valueOf(datePicker.getValue());
        int workoutOrderNumber = workoutListView.getItems().size() + 1;
        String workoutName = "Workout " + workoutOrderNumber;
        
        Workout workout = manager.createWorkout(
            workoutName, workoutDate, workoutOrderNumber
        );
        
        if (workout != null) {
            workoutListView.getItems().add(workout);
            dateList.add(workout.getDate());
        } else {
            System.out.println("Error in WorkoutListController.newWorkout()");
        }
    }
    
    @FXML
    private void editWorkout() throws Exception {
        String resource = "/fxml/ExerciseList.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent root = loader.load();
        
        ExerciseListController controller = loader.getController();
        Workout selectedWorkout =
            workoutListView.getSelectionModel().getSelectedItem();
        controller.setWorkout(selectedWorkout);
        
        showWindow(root);
    }
    
    @FXML
    private void removeWorkout() throws Exception {
        Optional<ButtonType> optional = showRemoveAlert();
        
        if (optional.isPresent() && optional.get() == ButtonType.OK) {
            final int selectedListIndex =
                workoutListView.getSelectionModel().getSelectedIndex();
            int newSelectedListIndex =
                (selectedListIndex == workoutListView.getItems().size() - 1)
                ? (selectedListIndex - 1)
                : selectedListIndex;

            try {
                Workout selectedWorkout =
                    workoutListView.getSelectionModel().getSelectedItem();
                manager.removeWorkout(selectedWorkout.getId());
                workoutListView.getItems().remove(selectedWorkout);
                dateList.remove(selectedWorkout.getDate());

                if (newSelectedListIndex >= 0) {
                    workoutListView.getSelectionModel()
                                   .select(newSelectedListIndex);
                } else {
                    workoutListView.getSelectionModel().clearSelection();
                }
            } catch (Exception e) {
                System.out.println(
                    "Error in WorkoutListContoller.removeWorkout(): "+ e.getMessage()
                );
            }
        }
    }
    
    private void showWindow(Parent root) {
        Stage stage = new Stage();
        stage.initOwner(workoutListView.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL); 
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    private Optional<ButtonType> showRemoveAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        
        alert.setHeaderText(
            workoutListView.getSelectionModel().getSelectedItem().getName()
        );
        alert.setContentText("Remove the workout?");
        
        return alert.showAndWait();
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
