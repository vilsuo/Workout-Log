
package com.mycompany.dao;

import java.sql.SQLException;
import java.util.List;

public interface ExerciseDao {
    
    int createItem(int exerciseInfoId) throws SQLException;
    
    void removeItem(int id) throws SQLException;
    
    int getExerciseInfoId(int exerciseId) throws SQLException;
    
    // write tests!
    List<Integer> getExerciseIdList(int exerciseInfoId) throws SQLException;
    
    // temporary?
    List<Integer> getAllExerciseIds() throws SQLException;
}
