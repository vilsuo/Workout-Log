
package com.mycompany.history;

import java.sql.Date;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ExerciseProgressionEntry {
    
    private final DoubleProperty weight;
    private final ObjectProperty<Date> date;
    
    public ExerciseProgressionEntry(double weight, Date date) {
        this.weight = new SimpleDoubleProperty(weight);
        this.date = new SimpleObjectProperty(date);
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
