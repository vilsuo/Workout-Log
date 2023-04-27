
package com.mycompany.history;

import com.mycompany.domain.Exercise;
import java.sql.Date;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ExerciseHistoryEntry {
    
    private final ObjectProperty<Date> date;
    private final ObjectProperty<Exercise> exercise;

    public ExerciseHistoryEntry(Date date, Exercise exercise) {
        this.date = new SimpleObjectProperty(date);
        this.exercise = new SimpleObjectProperty(exercise);
    }
    
    public final Date getDate() {
        return date.get();
    }
    
    public final void setDate(Date date) {
        this.date.set(date);
    }
    
    public final Exercise getExercise() {
        return exercise.get();
    }
    
    public final void setExercise(Exercise exercise) {
        this.exercise.set(exercise);
    }
}
