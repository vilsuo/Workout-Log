
package com.mycompany.dao;

import com.mycompany.domain.ExerciseInfo;
import java.sql.SQLException;
import java.util.List;

public interface ExerciseInfoDao {
    
    List<ExerciseInfo> getExerciseInfos() throws SQLException;
    List<String> getNamesByCategory(String category) throws SQLException;
    List<String> getCategories() throws SQLException;
    
    boolean changeCategoryByName(String name, String newCategory) throws SQLException;
    
    boolean removeByName(String name) throws SQLException; 
    boolean removeByCategory(String category) throws SQLException;
    
    boolean changeName(String currentName, String newName) throws SQLException;
    boolean changeCategory(String currentCategory, String newCategory) throws SQLException;
    
    
    /**
     * Adds a exercise entry to ExerciseInfo-table, if the table does not
     * already contain a entry with the given name.
     * 
     * @param name      Name of the exercise to add.
     * @param category  Category of the exercise to add.
     * @return          Index of the added exerciseInfo, -1 if adding was not
     *                  successful.
     * @throws java.sql.SQLException
     */
    int addExerciseInfo(String name, String category) throws SQLException;
}
