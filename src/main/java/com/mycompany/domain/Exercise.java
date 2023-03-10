
package com.mycompany.domain;

import javafx.beans.Observable;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

public class Exercise {
    
    private SimpleStringProperty exerciseName;
    private SimpleListProperty<SetBase> sets;

    public Exercise(String exerciseName) {
        this.exerciseName = new SimpleStringProperty(exerciseName);
        
        // list created so it fires updates if SetBase changes:
        Callback<SetBase, Observable[]> extractor =
            (SetBase setBase) -> new Observable[] {
                setBase.workingSetsProperty(),
                setBase.repetitionsProperty(),
                setBase.workingWeightProperty()
            };
        
        ObservableList<SetBase> observableList =
            FXCollections.observableArrayList(extractor);
        
        sets = new SimpleListProperty<>(observableList);
    }
    
    public Exercise(String exerciseName, SetBase set) {
        this(exerciseName);
        addSet(set);
    }
    
    public void addSet(SetBase set) {
        sets.get().add(set);
    }
    
    
    public SimpleListProperty<SetBase> setsProperty() {
        return sets;
    }    
    
    public final ObservableList<SetBase> getSets() {
        return sets.get();
    }
    
    public final void setSets(final ObservableList<SetBase> sets) {
        this.sets.set(sets);
    }
    
    
    public SimpleStringProperty exerciseNameProperty() {
        return this.exerciseName;
    }
    
    public final String getExerciseName() {
        return exerciseName.get();
    }
    
    public final void setExerciseName(String exerciseName) {
        this.exerciseName.set(exerciseName);
    }
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.exerciseName.get());
        
        for (SetBase setBase : sets.get()) {
            sb.append("\n");
            sb.append(setBase);
        }
        sb.append("\n");
        
        return sb.toString();
    }
}
