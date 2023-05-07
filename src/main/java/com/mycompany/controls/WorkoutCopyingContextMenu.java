
package com.mycompany.controls;

import com.mycompany.application.App;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Workout;
import java.sql.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;

public class WorkoutCopyingContextMenu extends ContextMenu {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    
    public WorkoutCopyingContextMenu(
            ListView<Workout> workoutListView, final DatePicker datePicker,
            List<Date> dateList, Date dateToShowContextMenuOf) {
        
        super();
        
        // context menu does not show if it is empty,
        // this is just temporary menu item
        getItems().add(new MenuItem("temporary item"));
        
        // Called just prior to the Window being shown.
        setOnShowing(
            contextMenuEvent -> {
                try {
                    ObservableList<Workout> workoutListBySelectedDate =
                        FXCollections.observableList(
                            manager.getWorkoutsByDate(dateToShowContextMenuOf)
                        );
                    
                    getItems().clear();
                    for (final Workout workout : workoutListBySelectedDate) {
                        MenuItem menuItem = new MenuItem(
                            "Copy '" + workout.getName() + "'"
                        );
                        
                        menuItem.setOnAction(
                            menuItemEvent -> {
                                try {
                                    int orderNumberToSet = workoutListBySelectedDate.size() + 1;
                                    Date dateToCopyTo = Date.valueOf(datePicker.getValue());
                                    
                                    Workout copiedWorkout = manager.copyWorkout(
                                        workout.getId(),
                                        dateToCopyTo,
                                        orderNumberToSet
                                    );
                                    
                                    dateList.add(dateToCopyTo);
                                    workoutListView.getItems().add(copiedWorkout);
                                    
                                } catch (Exception e) {
                                    System.out.println(
                                        "Error in WorkoutCopyingContextMenu MenuItemEvent: "
                                        + e.getMessage()
                                    );
                                }
                            }
                        );
                        getItems().add(menuItem);
                    }
                    
                } catch (Exception e) {
                    System.out.println(
                        "Error in WorkoutContextMenu setOnShowing: "
                        + e.getMessage()
                    );
                }
            }
        );
    }
}
