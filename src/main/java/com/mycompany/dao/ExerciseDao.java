
package com.mycompany.dao;

import com.mycompany.domain.Exercise;
import java.sql.SQLException;
import java.util.List;

public interface ExerciseDao {
    
    List<String> getExerciseCategories() throws SQLException;
    List<String> getExerciseNameByCategory(String category) throws SQLException;
    
    boolean addExercise(Exercise exercise) throws SQLException;
    boolean updateExercise(Exercise exerciseOld, Exercise exerciseNew) throws SQLException;
    boolean removeExercise(Exercise exercise) throws SQLException;
    
}
