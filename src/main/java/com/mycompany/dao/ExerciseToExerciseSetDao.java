
package com.mycompany.dao;

import java.sql.SQLException;
import java.util.List;

public interface ExerciseToExerciseSetDao {
    
    void createItem(int exerciseId,  int exerciseSetId) throws SQLException;
    
    void removeItemsByExerciseSet(int exerciseSetId) throws SQLException;
    void removeItemsByExercise(int exerciseId) throws SQLException;
    
    List<Integer> getExerciseSetIdList(int exerciseId) throws SQLException;
}
