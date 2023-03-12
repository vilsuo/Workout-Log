
package com.mycompany.dao;

import com.mycompany.domain.ExerciseInfo;
import java.sql.SQLException;
import java.util.List;

/**
 * TODO
 * 
 * - remove()
 *      - make sure all related keys are removed as well.
 */

public interface ExerciseInfoDao {
    
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
    int create(String name, String category) throws SQLException;
    
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
    boolean updateName(int id, String newName) throws SQLException;
    
    /**
     * Updates the 'category' field value with the value newCategory. Entry to
     * be updated is determined by the 'id' field.
     * 
     * @param id            Field 'id' value of the entry to be updated.
     * @param newCategory   Value to be set as a field 'category' value.
     * @throws SQLException 
     */
    void updateCategory(int id, String newCategory) throws SQLException;
    
    /**
     * Updates the 'category' field value to newCategory in all entries where
     * the field 'category' has value currentCategory.
     * 
     * @param currentCategory   Field 'category' value to be updated.
     * @param newCategory       Value to be set as a new value to field 'category'.
     * @throws SQLException 
     */
    void renameCategory(String currentCategory, String newCategory) throws SQLException;
    
    /**
     * Removes all entries where field 'id' has value id.
     * 
     * @param id
     * @throws SQLException 
     */
    void remove(int id) throws SQLException;
    
    
    ExerciseInfo getExerciseInfo(int index) throws SQLException;
    
    List<ExerciseInfo> getItems() throws SQLException;
    List<ExerciseInfo> getItemsByCategory(String category) throws SQLException;
    
    /*
    // needed?
    List<String> getNamesByCategory(String category) throws SQLException;
    List<String> getCategories() throws SQLException;
    */
}
