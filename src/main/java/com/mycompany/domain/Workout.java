
package com.mycompany.domain;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

public class Workout implements Comparable<Workout> {
    
    private final ReadOnlyIntegerWrapper id;
    private StringProperty name;
    private ListProperty<Exercise> exerciseList;
    private ObjectProperty<Date> date;
    private IntegerProperty orderNumber;

    public Workout(int id, List<Exercise> exerciseList, Date date, int orderNumber) {
        this.id = new ReadOnlyIntegerWrapper(id);
        
        this.name = new SimpleStringProperty("temporary workout name");
        
        Callback<Exercise, Observable[]> extractor =
            (Exercise exercise) -> new Observable[] {
                exercise.idProperty(),
                exercise.exerciseInfoProperty(),
                exercise.exerciseSetListProperty(),
                exercise.orderNumberProperty()
            };
        
        ObservableList<Exercise> observableList =
            FXCollections.observableList(exerciseList, extractor);
        
        this.exerciseList = new SimpleListProperty<>(observableList);
        
        this.date = new SimpleObjectProperty(date);
        this.orderNumber = new SimpleIntegerProperty(orderNumber);
    }
    
    public Workout(int id, Date date, int orderNumber) {
        this(id, new ArrayList<>(), date, orderNumber);
    }
    
    public void addExercise(Exercise exercise) {
        exerciseList.get().add(exercise);
    }

    public int getOrderNumber() {
        return orderNumber.get();
    }
    

    @Override
    public int compareTo(Workout other) {
        return getOrderNumber() - other.getOrderNumber();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("id=");
        sb.append(id.get());
        sb.append(", date=");
        sb.append(date.get().toString());
        sb.append("\n");
        
        for (Exercise exercise : exerciseList) {
            sb.append(exercise);
        }
        
        return sb.toString();
    }
}
