
package com.mycompany.cells;

import com.mycompany.application.App;
import com.mycompany.dao.ManagerImpl;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.scene.control.DateCell;

public class WorkoutDateCell extends DateCell {
    private final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    
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
}
