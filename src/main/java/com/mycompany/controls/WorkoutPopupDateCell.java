
package com.mycompany.controls;

import com.mycompany.application.App;
import com.mycompany.dao.ManagerImpl;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;

/*
todo
- can remove datepicker as a class parameter?
*/
public class WorkoutPopupDateCell extends DateCell {
    
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    
    private final DatePicker datePicker;
    private final List<Date> dateList;
    //private final Popup popup = new Popup();
    
    public WorkoutPopupDateCell(final DatePicker datePicker, List<Date> dateList) {
        super();
        
        this.datePicker = datePicker;
        this.dateList = dateList;
        
        itemProperty().addListener(
            (obs, oldValue, newValue) -> {
                if (newValue != null) {
                    final WorkoutContextMenu contextMenu =
                        new WorkoutContextMenu(Date.valueOf(newValue));
                    
                    setContextMenu(contextMenu);
                }
            }
        );
        
        WorkoutPopup popup = new WorkoutPopup();
        
        setOnMouseEntered(
            event -> {
                
                popup.display(
                    datePicker, Date.valueOf(getItem()),
                    event.getScreenX() + this.getWidth(),
                    event.getSceneY() + this.getHeight()
                );
                
                /*
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
                        "Error in WorkoutPopupDateCell setOnMouseEntered"
                        + e.getMessage()
                    );
                }
                */
            }
        );
        
        setOnMouseExited(
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
