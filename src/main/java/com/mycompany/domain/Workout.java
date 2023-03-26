
package com.mycompany.domain;

import java.sql.Date;
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

    public Workout(int id, String name, List<Exercise> exerciseList, Date date, int orderNumber) {
        this.id = new ReadOnlyIntegerWrapper(id);
        this.name = new SimpleStringProperty(name);
        
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
        this(id, "temporary name", new ArrayList<>(), date, orderNumber);
    }
    
    public void addExercise(Exercise exercise) {
        exerciseList.get().add(exercise);
    }
    
    
    public ReadOnlyIntegerProperty idProperty() {
        return id;
    }

    public int getId() {
        return id.get();
    }
    
    
    public StringProperty nameProperty() {
        return name;
    }
    
    public String getName() {
        return name.get();
    }
    
    public void setName(String name) {
        this.name.set(name);
    }
    
    
    public ListProperty<Exercise> exerciseListProperty() {
        return exerciseList;
    }    
    
    public final ObservableList<Exercise> getExerciseList() {
        return exerciseList.get();
    }
    
    public final void setExerciseList(final ObservableList<Exercise> exerciseList) {
        this.exerciseList.set(exerciseList);
    }
    
    
    public ObjectProperty<Date> dateProperty() {
        return date;
    }
    
    public final Date getDate() {
        return date.get();
    }
    
    public void setDate(Date date) {
        this.date.set(date);
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
    public int compareTo(Workout other) {
        return getOrderNumber() - other.getOrderNumber();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("id=");
        sb.append(id.get());
        sb.append(", name=");
        sb.append(name.get());
        sb.append(", date=");
        sb.append(date.get().toString());
        sb.append(", #");
        sb.append(orderNumber.get());
        sb.append("\n");
        
        for (Exercise exercise : exerciseList) {
            sb.append(exercise);
            sb.append("\n");
        }
        
        return sb.toString();
    }
}
