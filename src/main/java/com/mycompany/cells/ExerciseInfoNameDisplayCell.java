
package com.mycompany.cells;

import com.mycompany.domain.ExerciseInfo;
import javafx.scene.control.ListCell;

public class ExerciseInfoNameDisplayCell extends ListCell<ExerciseInfo>{
    
    @Override
    public void updateItem(ExerciseInfo item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
            setText(null);
        } else {
            setText(item.getName());
        }
    }
}
