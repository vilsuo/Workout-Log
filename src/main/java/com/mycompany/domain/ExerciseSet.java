
package com.mycompany.domain;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ExerciseSet implements Comparable<ExerciseSet>{
    
    private final ReadOnlyIntegerWrapper id;
    private final IntegerProperty workingSets;
    private final IntegerProperty repetitions;
    private final DoubleProperty workingWeight;
    private final IntegerProperty orderNumber;

    public ExerciseSet(int id, int workingSets, int repetitions, double workingWeight, int orderNumber) {
        this.id = new ReadOnlyIntegerWrapper(id);
        this.workingSets = new SimpleIntegerProperty(workingSets);
        this.repetitions = new SimpleIntegerProperty(repetitions);
        this.workingWeight = new SimpleDoubleProperty(workingWeight);
        this.orderNumber = new SimpleIntegerProperty(orderNumber);
    }
    
    public ReadOnlyIntegerProperty idProperty() {
        return id;
    }

    public int getId() {
        return id.get();
    }
    
    public void setId(int id) {
        this.id.set(id);
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
    
    
    public IntegerProperty orderNumberProperty() {
        return orderNumber;
    }
    
    public final int getOrderNumber() {
        return orderNumber.get();
    }
    
    public final void setOrderNumber(int order) {
        this.orderNumber.set(order);
    }
    
    @Override
    public int compareTo(ExerciseSet other) {
        return getOrderNumber() - other.getOrderNumber();
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        
        if (!(other instanceof ExerciseSet)) {
            return false;
        }
        
        ExerciseSet exerciseSet = (ExerciseSet) other;
        
        return (getId() == exerciseSet.getId())
            && (getWorkingSets() == exerciseSet.getWorkingSets())
            && (getRepetitions() == exerciseSet.getRepetitions())
            && (getWorkingWeight() == exerciseSet.getWorkingWeight())
            && (getOrderNumber() == exerciseSet.getOrderNumber());
    }
    
    @Override
    public String toString() {
        return "\tid=" + id.get() + ": " + workingSets.get() + " x " + repetitions.get()
                       + " @" + workingWeight.get() + " #" + orderNumber.get();
    }
}