
package com.mycompany.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorkoutToExerciseDaoImpl {
    
    public void createItem(Connection connection, int workoutId, int exerciseId) throws SQLException {
        String sql = "INSERT INTO WorkoutToExercise (workout_id, exercise_id) VALUES (?, ?);";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, workoutId);
        pstmt.setInt(2, exerciseId);
        pstmt.executeUpdate();
    }
    
    public void removeItemsByExercise(Connection connection, int exerciseId) throws SQLException {
        String sql = "DELETE FROM WorkoutToExercise WHERE exercise_id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, exerciseId);
        pstmt.executeUpdate();
    }
    
    public void removeItemsByWorkout(Connection connection, int workoutId) throws SQLException {
        String sql = "DELETE FROM WorkoutToExercise WHERE workout_id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, workoutId);
        pstmt.executeUpdate();
    }
    
    public List<Integer> getExerciseIdList(Connection connection, int workoutId) throws SQLException {
        List<Integer> exerciseIdList = new ArrayList<>();
        String sql = "SELECT exercise_id FROM WorkoutToExercise WHERE workout_id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, workoutId);
        
        ResultSet results = pstmt.executeQuery();
        while (results.next()) {
            exerciseIdList.add(results.getInt("exercise_id"));
        }
        return exerciseIdList;
    }
}
