
package com.mycompany.controls;

import com.mycompany.application.App;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Workout;
import java.sql.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.stage.Popup;

public class WorkoutPopup extends Popup {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);

    public void display(Node node, Date date, double x, double y) {
        try {
            ObservableList<Workout> workoutList =
                FXCollections.observableList(
                    manager.getWorkoutsByDate(date)
                );

            ListView workoutListView = new ListView();
            workoutListView.setItems(workoutList);

            if (!workoutList.isEmpty()) {
                getContent().add(workoutListView);
                show(node, x, y);
            }

        } catch (Exception e) {
            System.out.println(
                "Error in WorkoutPopup.display: "
                + e.getMessage()
            );
        }
    }
}
