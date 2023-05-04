
package com.mycompany.controls;

import com.mycompany.application.App;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Workout;
import java.sql.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class WorkoutContextMenu extends ContextMenu {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    
    public WorkoutContextMenu(Date date) {
        super();
        
        // context menu does not show if it is empty,
        // this is just temporary menu item
        getItems().add(new MenuItem("temporary item"));
        
        // Called just prior to the Window being shown.
        setOnShowing(
            contextMenuEvent -> {
                try {
                    ObservableList<Workout> workoutList =
                        FXCollections.observableList(
                            manager.getWorkoutsByDate(date)
                        );
                    
                    getItems().clear();
                    for (final Workout workout : workoutList) {
                        MenuItem menuItem = new MenuItem(
                            "Copy '" + workout.getName() + "'"
                        );
                        menuItem.setOnAction(
                            menuItemEvent -> {
                                System.out.println(
                                    "menu item pressed: " + menuItem.getText()
                                );
                            }
                        );
                        getItems().add(menuItem);
                    }
                    
                } catch (Exception e) {
                    System.out.println(
                        "Error in WorkoutPopupDateCell contextMenu.setOnShowing"
                        + e.getMessage()
                    );
                }
            }
        );
    }
}
