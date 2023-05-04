
package com.mycompany.cells;

import com.mycompany.application.App;
import com.mycompany.dao.ManagerImpl;
import com.mycompany.domain.Workout;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.stage.Popup;

public class WorkoutPopupDateCell extends DateCell {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    
    private final DatePicker datePicker;
    private List<Date> dateList;
    private final Popup popup = new Popup();

    public WorkoutPopupDateCell(final DatePicker datePicker, List<Date> dateList) {
        this.datePicker = datePicker;
        this.dateList = dateList;
        
        this.setOnMouseEntered(
            event -> {
                try {
                    ObservableList<Workout> workoutList =
                        FXCollections.observableList(
                            manager.getWorkoutsByDate(Date.valueOf(getItem()))
                        );
                    
                    ListView workoutListView = new ListView();
                    workoutListView.setItems(workoutList);

                    if (!workoutList.isEmpty()) {
                        popup.getContent().add(workoutListView);
                        popup.show(
                            datePicker,
                            event.getScreenX() + this.getWidth(),
                            event.getSceneY() + this.getHeight()
                        );
                    }
                    
                } catch (Exception e) {
                    System.out.println(
                        "Error in WorkoutDateCell.WorkoutDateCell: " + e.getMessage()
                    );
                }
            }
        );
        
        this.setOnMouseExited(
            event -> {
                if (popup.isShowing()) {
                    popup.hide();
                }
            }
        );
    }
    
    @Override
    public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        
        if (item != null && dateList.contains(Date.valueOf(item))) {
            setStyle("-fx-background-color: green;");   
        }
    }
}
