
package com.mycompany.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExerciseDaoImpl {
    
    public int createExercise(Connection connection, int exerciseInfoId, int orderNumber) throws SQLException {
        String sql = "INSERT INTO Exercise (exerciseInfo_id, orderNumber) VALUES (?, ?);";
        PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS );
        pstmt.setInt(1, exerciseInfoId);
        pstmt.setInt(2, orderNumber);
        pstmt.executeUpdate();
        
        ResultSet results = pstmt.getGeneratedKeys();
        while (results.next()) {
            return results.getInt(1);
        }
        return -1;
    }
    
    public void removeExercise(Connection connection, int id) throws SQLException {
        String sql = "DELETE FROM Exercise WHERE id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }
    
    // remove items
    
    public void updateExercise(Connection connection, int id, int newExerciseInfoId, int newOrderNumber) throws SQLException {
        String sql = "UPDATE Exercise SET exerciseInfo_id = ?, orderNumber = ? WHERE id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, newExerciseInfoId);
        pstmt.setInt(2, newOrderNumber);
        pstmt.setInt(3, id);
        pstmt.executeUpdate();
    }
    
    public int getExerciseOrderNumber(Connection connection, int exerciseId) throws SQLException {
        String sql = "SELECT orderNumber FROM Exercise WHERE id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, exerciseId);
        
        ResultSet results = pstmt.executeQuery();
        while (results.next()) {
            return results.getInt("orderNumber");
        }
        return -1;
    }
    
    public int getExerciseInfoId(Connection connection, int exerciseId) throws SQLException {
        String sql = "SELECT exerciseInfo_id FROM Exercise WHERE id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, exerciseId);
        
        ResultSet results = pstmt.executeQuery();
        while (results.next()) {
            return results.getInt("exerciseInfo_id");
        }
        return -1;
    }
    
    public List<Integer> getExerciseIdList(Connection connection, int exerciseInfoId) throws SQLException {
        List<Integer> exerciseIdList = new ArrayList<>();
        String sql = "SELECT id FROM Exercise WHERE exerciseInfo_id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, exerciseInfoId);
        
        ResultSet results = pstmt.executeQuery();
        while (results.next()) {
            exerciseIdList.add(results.getInt("id"));
        }
        return exerciseIdList;
    }
    
    public List<Integer> getAllExerciseIds(Connection connection) throws SQLException {
        List<Integer> exerciseIdList = new ArrayList<>();
        String sql = "SELECT id FROM Exercise;";
        
        ResultSet results = connection.prepareStatement(sql).executeQuery();
        while (results.next()) {
            exerciseIdList.add(results.getInt("id"));
        }
        return exerciseIdList;
    }
}
