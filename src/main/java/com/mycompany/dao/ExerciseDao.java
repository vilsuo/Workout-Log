
package com.mycompany.dao;

import java.sql.SQLException;
import java.util.List;

public interface ExerciseDao {
    
    List<String> getExerciseNamesByCategory(String category) throws SQLException;
    List<String> getExerciseCategories() throws SQLException;
    
    boolean changeExerciseCategory(String exerciseName, String newCategory) throws SQLException;
    
    boolean removeExercisesByName(String name) throws SQLException; 
    boolean removeExercisesByCategory(String category) throws SQLException;
    
    boolean renameExerciseName(String currentName, String newName) throws SQLException;
    boolean renameExerciseCategory(String currentCategory, String newCategory) throws SQLException;
    
    boolean addExercise(String name, String category) throws SQLException;
}
