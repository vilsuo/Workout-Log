
package com.mycompany.history;

import java.sql.Date;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ExerciseRecordEntry {
    
    private final IntegerProperty repetitions;
    private final DoubleProperty weight;
    private final ObjectProperty<Date> date;

    public ExerciseRecordEntry(int repetitions, double weight, Date date) {
        this.repetitions = new SimpleIntegerProperty(repetitions);
        this.weight = new SimpleDoubleProperty(weight);
        this.date = new SimpleObjectProperty(date);
    }
    
    public final int getRepetitions() {
        return repetitions.get();
    }
    
    public final void setRepetitions(int repetitions) {
        this.repetitions.set(repetitions);
    }
    
    public final double getWeight() {
        return weight.get();
    }
    
    public final void setWeight(double weight) {
        this.weight.set(weight);
    }
    
    public final Date getDate() {
        return date.get();
    }
    
    public final void setDate(Date date) {
        this.date.set(date);
    }
}
