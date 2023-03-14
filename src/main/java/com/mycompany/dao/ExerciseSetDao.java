
package com.mycompany.dao;

import com.mycompany.domain.ExerciseSet;
import java.sql.SQLException;
import java.util.List;

public interface ExerciseSetDao {
    
    int createItem(int workingSets, int repetitions, double workingWeight, int orderNumber) throws SQLException;
    
    void updateItem(int id, int newWorkingSets, int newRepetitions, double newWorkingWeight) throws SQLException;
    
    void updateItemOrderNumber(int id, int newOrderNumber) throws SQLException;
    void updateItemsOrderNumbers(List<Integer> idList, List<Integer> newOrderNumberList) throws SQLException;
    
    void removeItem(int id) throws SQLException;
    void removeItems(List<Integer> idList) throws SQLException;
    
    ExerciseSet getItem(int id) throws SQLException;
    List<ExerciseSet> getItems(List<Integer> exerciseSetIdList) throws SQLException;
}
