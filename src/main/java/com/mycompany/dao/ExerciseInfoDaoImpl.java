
package com.mycompany.dao;

import com.mycompany.domain.ExerciseInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExerciseInfoDaoImpl {
    
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
    public int createExerciseInfo(Connection connection, String name, String category) throws SQLException {
        if (getExerciseInfoIdByName(connection, name) != -1) {
            return -1;
        }
        String sql = "INSERT INTO ExerciseInfo (name, category) VALUES (?, ?);";
        PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS );
        pstmt.setString(1, name);
        pstmt.setString(2, category);
        pstmt.executeUpdate();
        
        ResultSet results = pstmt.getGeneratedKeys();
        while (results.next()) {
            return results.getInt(1);
        }
        return -1;
    }
    
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
    public boolean updateExerciseInfoName(Connection connection, int id, String newName) throws SQLException {
        if (getExerciseInfoIdByName(connection, newName) != -1) {
            return false;
        }
        
        String sql = "UPDATE ExerciseInfo SET name = ? WHERE id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, newName);
        pstmt.setInt(2, id);
        pstmt.executeUpdate();
        
        return true;
    }
    
    public void updateExerciseInfoCategory(Connection connection, int id, String newCategory) throws SQLException {
        String sql = "UPDATE ExerciseInfo SET category = ? WHERE id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, newCategory);
        pstmt.setInt(2, id);
        pstmt.executeUpdate();
    }
    
    public void removeExerciseInfo(Connection connection, int id) throws SQLException {
        String sql = "DELETE FROM ExerciseInfo WHERE id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }
    
    public ExerciseInfo getExerciseInfo(Connection connection, int id) throws SQLException {
        String sql = "SELECT * FROM ExerciseInfo WHERE id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        
        ResultSet results = pstmt.executeQuery();
        while (results.next()) {
            return new ExerciseInfo(
                results.getInt("id"),
                results.getString("name"),
                results.getString("category")
            );
        }
        return null;
    }
    
    /*
    public List<ExerciseInfo> getExerciseInfoList(Connection connection, List<Integer> idList) throws SQLException {
        List<ExerciseInfo> exerciseInfoList = new ArrayList<>();
        String sql = "SELECT * FROM ExerciseInfo WHERE id = ?;";
        for (int id : idList) {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            ResultSet results = pstmt.executeQuery();
            while (results.next()) {
                exerciseInfoList.add(
                    new ExerciseInfo(
                        results.getInt("id"),
                        results.getString("name"),
                        results.getString("category")
                    )
                );
            }
        }
        return exerciseInfoList;
    }
    */
    
    public List<ExerciseInfo> getAllExerciseInfos(Connection connection) throws SQLException {
        List<ExerciseInfo> exerciseInfoList = new ArrayList<>();
        String sql = "SELECT * FROM ExerciseInfo;";
        
        ResultSet results = connection.prepareStatement(sql).executeQuery();
        while (results.next()) {
            exerciseInfoList.add(
                new ExerciseInfo(
                    results.getInt("id"),
                    results.getString("name"),
                    results.getString("category")
                )
            );
        }
        return exerciseInfoList;
    }
    
    public List<ExerciseInfo> getExerciseInfoListByCategory(Connection connection ,String category) throws SQLException {
        List<ExerciseInfo> exerciseInfos = new ArrayList<>();
        String sql = "SELECT * FROM ExerciseInfo WHERE category = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, category);
        
        ResultSet results = pstmt.executeQuery();
        while (results.next()) {
            exerciseInfos.add(
                new ExerciseInfo(
                    results.getInt("id"),
                    results.getString("name"),
                    results.getString("category")
                )
            );
        }
        return exerciseInfos;
    }
    
    private int getExerciseInfoIdByName(Connection connection, String name) throws SQLException {
        String sql = "SELECT id FROM ExerciseInfo WHERE name = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, name);
        
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            return results.getInt(1);
        } else {
            return -1;
        }
    }
}
