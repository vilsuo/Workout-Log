
package com.mycompany.dao;

import com.mycompany.domain.ExerciseInfo;
import java.sql.SQLException;
import java.util.List;

public interface ExerciseInfoDao {
    
    /**
     * Adds a entry to ExerciseInfo-table, if the table does not
     * already contain a entry with the given name.
     * 
     * @param name      Name of the exercise to add.
     * @param category  Category of the exercise to add.
     * @return          Index of the added exerciseInfo, -1 if adding was not
     *                  successful.
     * @throws java.sql.SQLException
     */
    int createItem(String name, String category) throws SQLException;
    
    /**
     * Updates the 'name' field value with the value newName. Entry to be
     * updated is determined by the 'id' field. Update takes place only if there
     * does not already exist any entries where 'name' field has value newName.
     * 
     * @param id            Field 'id' value of the entry to be updated.
     * @param newName       Value to be set as a field 'name' value.
     * @return              True if update was successful, false otherwise.
     * @throws SQLException 
     */
    boolean updateItemName(int id, String newName) throws SQLException;
    
    void updateItemCategory(int id, String newCategory) throws SQLException;
    
    void removeItem(int id) throws SQLException;
    void removeItems(List<Integer> idList) throws SQLException;
    
    ExerciseInfo getItem(int id) throws SQLException;
    List<ExerciseInfo> getItems(List<Integer> idList) throws SQLException;
    
    List<ExerciseInfo> getAllItems() throws SQLException;
    List<ExerciseInfo> getAllItemsByCategory(String category) throws SQLException;
}
