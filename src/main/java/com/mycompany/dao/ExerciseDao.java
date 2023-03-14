
package com.mycompany.dao;

import com.mycompany.domain.Exercise;
import java.sql.SQLException;
import java.util.List;

public interface ExerciseDao {
    
    int createItem(int exerciseInfoId) throws SQLException;
    
    void removeItem(int id) throws SQLException;
    
    int getExerciseInfoId(int exerciseId) throws SQLException;
    
    // needed here? move elsewhere?
    // Exercise getItem(int id) throws SQLException;
    
    // needed here? move elsewhere?
    // List<Exercise> getTableItems() throws SQLException;
}
