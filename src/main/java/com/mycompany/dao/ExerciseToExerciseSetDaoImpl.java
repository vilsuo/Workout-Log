
package com.mycompany.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExerciseToExerciseSetDaoImpl {
    
    private String databasePath;
    
    public ExerciseToExerciseSetDaoImpl(String databasePath) {
        this.databasePath = databasePath;
    }
    
    public void createItem(Connection connection, int exerciseId,  int exerciseSetId) throws SQLException {
            String sql = "INSERT INTO ExerciseToExerciseSet (exercise_id, exerciseSet_id) VALUES (?, ?);";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, exerciseId);
            pstmt.setInt(2, exerciseSetId);
            pstmt.executeUpdate();
    }
    
    public void removeItemsByExerciseSet(Connection connection, int exerciseSetId) throws SQLException {
            String sql = "DELETE FROM ExerciseToExerciseSet WHERE exerciseSet_id = ?;";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, exerciseSetId);
            pstmt.executeUpdate();
    }
    
    public void removeItemsByExercise(Connection connection, int exerciseId) throws SQLException {
            String sql = "DELETE FROM ExerciseToExerciseSet WHERE exercise_id = ?;";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, exerciseId);
            pstmt.executeUpdate();
    }
    
    public List<Integer> getExerciseSetIdList(Connection connection, int exerciseId) throws SQLException {
        List<Integer> exerciseSetIdList = new ArrayList<>();
            String sql = "SELECT exerciseSet_id FROM ExerciseToExerciseSet WHERE exercise_id = ?;";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, exerciseId);
            
            ResultSet results = pstmt.executeQuery();
            while (results.next()) {
                exerciseSetIdList.add(results.getInt("exerciseSet_id"));
            }
        return exerciseSetIdList;
    }
}
