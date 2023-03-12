
package com.mycompany.dao;

import java.sql.SQLException;
import java.util.List;

public interface ExerciseSetDao {
    
    int create(int workingSets, int repetitions, double workingWeight) throws SQLException;
    
    void update(int id, int workingSets, int repetitions, double workingWeight) throws SQLException;
    
    void remove(int id) throws SQLException;
    
    void remove(List<Integer> idList) throws SQLException;
}
