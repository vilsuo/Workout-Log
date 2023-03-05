
package com.mycompany.domain;


import com.mycompany.domain.types.Exercise;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

public class ExerciseBase {
    
    private final Exercise exerciseName;
    private ObservableList<SetBase> sets;

    public ExerciseBase(Exercise exerciseName) {
        this.exerciseName = exerciseName;
        
        Callback<SetBase, Observable[]> extractor = (SetBase setBase) -> new Observable[] {
            setBase.workingSetsProperty(),
            setBase.repetitionsProperty(),
            setBase.workingWeightProperty()
        };
        
        this.sets = FXCollections.observableArrayList(extractor);
    }
    
    public ExerciseBase(Exercise exerciseName, SetBase set) {
        this(exerciseName);
        addSet(set);
    }
    
    public void addSet(SetBase set) {
        this.sets.add(set);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.exerciseName.label);
        
        for (SetBase setBase : this.sets) {
            sb.append("\n");
            sb.append(setBase);
        }
        
        sb.append("\n");
        return sb.toString();
    }
    
    
}
