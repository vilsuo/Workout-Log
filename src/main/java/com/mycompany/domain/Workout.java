
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
    
    public Workout(int id, String name, Date date, int orderNumber) {
        this(id, name, new ArrayList<>(), date, orderNumber);
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
        if (date.get().equals(other.getDate())) {
            return orderNumber.get() - other.orderNumber.get();
        }
        return date.get().before(other.getDate()) ? -1 : 1;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name.get());
        for (Exercise exercise : exerciseList.get()) {
            sb.append("\n");
            sb.append(exercise);
        }
        return sb.toString();
    }
}
