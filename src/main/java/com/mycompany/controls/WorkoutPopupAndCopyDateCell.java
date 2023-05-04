
package com.mycompany.controls;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;

public class WorkoutPopupAndCopyDateCell extends DateCell {
    
    private final List<Date> dateList;
    //private final Popup popup = new Popup();
    
    public WorkoutPopupAndCopyDateCell(final DatePicker datePicker, List<Date> dateList) {
        super();
        
        this.dateList = dateList;
        
        itemProperty().addListener(
            (obs, oldValue, newValue) -> {
                if (newValue != null) {
                    final WorkoutContextMenu contextMenu =
                        new WorkoutContextMenu(
                            //Date.valueOf(datePicker.getValue()),
                            datePicker,
                            Date.valueOf(newValue)
                        );
                    
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
