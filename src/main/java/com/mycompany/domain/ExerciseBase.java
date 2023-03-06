
package com.mycompany.domain;

import com.mycompany.domain.types.Exercise;
import javafx.beans.Observable;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

public class ExerciseBase {
    
    private final SimpleObjectProperty<Exercise> exerciseName;
    private SimpleListProperty<SetBase> sets;

    public ExerciseBase(Exercise exerciseName) {
        this.exerciseName = new SimpleObjectProperty<>(exerciseName);
        
        // list created so it fires updates if SetBase changes:
        Callback<SetBase, Observable[]> extractor = (SetBase setBase) -> new Observable[] {
            setBase.workingSetsProperty(),
            setBase.repetitionsProperty(),
            setBase.workingWeightProperty()
        };
        ObservableList<SetBase> observableList = FXCollections.observableArrayList(extractor);
        sets = new SimpleListProperty<>(observableList);
    }
    
    public ExerciseBase(Exercise exerciseName, SetBase set) {
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
    
    public final Exercise getExercise() {
        return exerciseName.get();
    }
    
    public final void setSets(final ObservableList<SetBase> sets) {
        setsProperty().set(sets);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.exerciseName.get().label);
        
        for (SetBase setBase : sets) {
            sb.append("\n");
            sb.append(setBase);
        }
        sb.append("\n");
        
        return sb.toString();
    }
}
