
package com.mycompany.domain;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

public final class Exercise {
    
    private final ReadOnlyIntegerWrapper id;
    private ObjectProperty<ExerciseInfo> exerciseInfo;
    private ListProperty<ExerciseSet> exerciseSetList;

    public Exercise(int id, ExerciseInfo exerciseInfo, List<ExerciseSet> exerciseSetList) {
        this.id = new ReadOnlyIntegerWrapper(id);
        
        this.exerciseInfo = new SimpleObjectProperty(exerciseInfo);
        
        Callback<ExerciseSet, Observable[]> extractor =
            (ExerciseSet exerciseSet) -> new Observable[] {
                exerciseSet.idProperty(),
                exerciseSet.workingSetsProperty(),
                exerciseSet.repetitionsProperty(),
                exerciseSet.workingWeightProperty()
            };
        
        // list created so it fires updates if ExerciseSet changes:
        ObservableList<ExerciseSet> observableList =
            FXCollections.observableList(exerciseSetList, extractor);
        
        this.exerciseSetList = new SimpleListProperty<>(observableList);
    }
    
    public Exercise(int id, ExerciseInfo exerciseInfo) {
        this(id, exerciseInfo, new ArrayList<>());
    }
    
    public Exercise(int id, ExerciseInfo exerciseInfo, ExerciseSet exerciseSet) {
        this(id, exerciseInfo);
        
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
    
   
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("id=");
        sb.append(getId());
        sb.append("\ninfo={");
        sb.append(this.exerciseInfo.get());
        sb.append("}\nsets={");
        
        for (ExerciseSet exerciseSet : exerciseSetList.get()) {
            sb.append("\n");
            sb.append(exerciseSet);
        }
        sb.append("\n}");
        
        return sb.toString();
    }
}
