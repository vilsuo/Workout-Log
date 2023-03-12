
package com.mycompany.domain;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ExerciseSet {
    
    private final ReadOnlyIntegerWrapper id;
    private final IntegerProperty workingSets;
    private final IntegerProperty repetitions;
    private final DoubleProperty workingWeight;

    public ExerciseSet(int id, int workingSets, int repetitions, double workingWeight) {
        this.id = new ReadOnlyIntegerWrapper(id);
        this.workingSets = new SimpleIntegerProperty(workingSets);
        this.repetitions = new SimpleIntegerProperty(repetitions);
        this.workingWeight = new SimpleDoubleProperty(workingWeight);
    }
    
    public ReadOnlyIntegerProperty idProperty() {
        return id;
    }

    public int getId() {
        return id.get();
    }
    
    
    public IntegerProperty workingSetsProperty() {
        return workingSets;
    }
    
    public final int getWorkingSets() {
        return workingSets.get();
    }
    
    public final void setWorkingSets(int workingSets) {
        this.workingSets.set(workingSets);
    }
    
    
    public IntegerProperty repetitionsProperty() {
        return repetitions;
    }
    
    public final int getRepetitions() {
        return repetitions.get();
    }
    
    public final void setRepetitions(int repetitions) {
        this.repetitions.set(repetitions);
    }
    
    
    public DoubleProperty workingWeightProperty() {
        return workingWeight;
    }
    
    public final double getWorkingWeight() {
        return workingWeight.get();
    }
    
    public final void setWorkingWeight(double workingWeight) {
        this.workingWeight.set(workingWeight);
    }
    
    
    @Override
    public String toString() {
        return "\tid=" + id.get() + ": " + workingSets.get() + " x " + repetitions.get() + " @" + workingWeight.get();
    }
}