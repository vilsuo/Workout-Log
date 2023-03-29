
package com.mycompany.cells;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import javafx.scene.control.DateCell;

public class WorkoutDateCell extends DateCell {
    private List<Date> dateList;

    public WorkoutDateCell(List<Date> dateList) {
        this.dateList = dateList;
    }
    
    @Override
    public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        
        if (item != null && dateList.contains(Date.valueOf(item))) {
            setStyle("-fx-background-color: #ffc0cb;");   
        }
    }
}
