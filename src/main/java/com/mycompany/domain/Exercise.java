
package com.mycompany.domain;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

public final class Exercise implements Comparable<Exercise>{
    
    private final ReadOnlyIntegerWrapper id;
    private ObjectProperty<ExerciseInfo> exerciseInfo;
    private ListProperty<ExerciseSet> exerciseSetList;
    private IntegerProperty orderNumber;

    public Exercise(int id, ExerciseInfo exerciseInfo, List<ExerciseSet> exerciseSetList, int orderNumber) {
        this.id = new ReadOnlyIntegerWrapper(id);
        this.exerciseInfo = new SimpleObjectProperty(exerciseInfo);
        
        Callback<ExerciseSet, Observable[]> extractor =
            (ExerciseSet exerciseSet) -> new Observable[] {
                exerciseSet.idProperty(),
                exerciseSet.workingSetsProperty(),
                exerciseSet.repetitionsProperty(),
                exerciseSet.workingWeightProperty(),
                exerciseSet.orderNumberProperty()
            };
        
        ObservableList<ExerciseSet> observableList =
            FXCollections.observableList(exerciseSetList, extractor);
        
        this.exerciseSetList = new SimpleListProperty<>(observableList);
        
        this.orderNumber = new SimpleIntegerProperty(orderNumber);
    }
    
    public Exercise(int id, ExerciseInfo exerciseInfo, int orderNumber) {
        this(id, exerciseInfo, new ArrayList<>(), orderNumber);
    }
    
    public Exercise(int id, ExerciseInfo exerciseInfo, ExerciseSet exerciseSet, int orderNumber) {
        this(id, exerciseInfo, orderNumber);
        
        addExerciseSet(exerciseSet);
    }
    
    
    public void addExerciseSet(ExerciseSet exerciseSet) {
        exerciseSetList.get().add(exerciseSet);
    }
    
    
    public ReadOnlyIntegerProperty idProperty() {
        return id;
    }

    public int getId() {
        return id.get();
    }
    
    
    public ObjectProperty exerciseInfoProperty() {
        return exerciseInfo;
    }
    
    public final ExerciseInfo getExerciseInfo() {
        return exerciseInfo.get();
    }
    
    public final void setExerciseInfo(ExerciseInfo exerciseInfo) {
        this.exerciseInfo.set(exerciseInfo);
    }
    
    
    public ListProperty<ExerciseSet> exerciseSetListProperty() {
        return exerciseSetList;
    }    
    
    public final ObservableList<ExerciseSet> getExerciseSetList() {
        return exerciseSetList.get();
    }
    
    public final void setExerciseSetList(final ObservableList<ExerciseSet> exerciseSetList) {
        this.exerciseSetList.set(exerciseSetList);
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
    public int compareTo(Exercise other) {
        return getOrderNumber() - other.getOrderNumber();
    }
   
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("id=");
        sb.append(getId());
        sb.append(", #");
        sb.append(orderNumber.get());
        sb.append("\ninfo={");
        sb.append(exerciseInfo.get());
        sb.append("}\nsets={");
        
        for (ExerciseSet exerciseSet : exerciseSetList.get()) {
            sb.append("\n");
            sb.append(exerciseSet);
        }
        sb.append("\n}");
        
        return sb.toString();
    }
}
