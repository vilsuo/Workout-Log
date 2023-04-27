
package com.mycompany.domain;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ExerciseInfo {
    
    private final ReadOnlyIntegerWrapper id;
    private final StringProperty name;
    private final StringProperty category;

    public ExerciseInfo(int id, String name, String category) {
        this.id = new ReadOnlyIntegerWrapper(id);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
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
    
    
    public StringProperty categoryProperty() {
        return category;
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        
        if (!(other instanceof ExerciseInfo)) {
            return false;
        }
        
        ExerciseInfo item = (ExerciseInfo) other;
        
        return (getId() == item.getId())
             && getName().equals(item.getName())
             && getCategory().equals(item.getCategory());
    }
    
    @Override
    public String toString() {
        return name.get() + " (" + category.get() + ")";
    }
    
    /*
    @Override
    public String toString() {
        return "id=" + id.get() + ", name=" + name.get() + ", category=" + category.get();
    }
    */
}
