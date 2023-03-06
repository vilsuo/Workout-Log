
package com.mycompany.domain;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class SetBase {
    
    private final IntegerProperty workingSets = new SimpleIntegerProperty();
    private final IntegerProperty repetitions = new SimpleIntegerProperty();
    private final DoubleProperty workingWeight = new SimpleDoubleProperty();

    public SetBase(int workingSets, int repetitions, double workingWeight) {
        setWorkingSets(workingSets);
        setRepetitions(repetitions);
        setWorkingWeight(workingWeight);
    }
    
    public final IntegerProperty workingSetsProperty() {
        return workingSets;
    }
    
    public final int getWorkingSets() {
        return workingSetsProperty().get();
    }
    
    public final void setWorkingSets(final int workingSets) {
        workingSetsProperty().set(workingSets);
    }
    
    public IntegerProperty repetitionsProperty() {
        return repetitions;
    }
    
    public final int getRepetitions() {
        return repetitionsProperty().get();
    }
    
    public final void setRepetitions(final int repetitions) {
        repetitionsProperty().set(repetitions);
    }
    
    public DoubleProperty workingWeightProperty() {
        return workingWeight;
    }
    
    public final double getWorkingWeight() {
        return workingWeightProperty().get();
    }
    
    public final void setWorkingWeight(final double workingWeight) {
        workingWeightProperty().set(workingWeight);
    }
    
    @Override
    public String toString() {
        return getWorkingSets() + "*" + getRepetitions() + " @" + getWorkingWeight();
    }
}