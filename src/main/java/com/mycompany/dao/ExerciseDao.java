
package com.mycompany.dao;

import com.mycompany.domain.ExerciseInfo;
import java.sql.SQLException;

public interface ExerciseDao {
    
    int create(ExerciseInfo exerciseInfo) throws SQLException;
    
    void remove(int id) throws SQLException;
}
