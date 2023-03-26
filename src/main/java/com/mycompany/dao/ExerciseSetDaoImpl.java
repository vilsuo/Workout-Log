
package com.mycompany.dao;

import com.mycompany.domain.ExerciseSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExerciseSetDaoImpl {
    
    public int createExerciseSet(Connection connection, int workingSets, int repetitions, double workingWeight, int orderNumber) throws SQLException {
            String sql = "INSERT INTO ExerciseSet (workingSets, repetitions, workingWeight, orderNumber) VALUES (?, ?, ?, ?);";
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS );
            pstmt.setInt(1, workingSets);
            pstmt.setInt(2, repetitions);
            pstmt.setDouble(3, workingWeight);
            pstmt.setInt(4, orderNumber);
            pstmt.executeUpdate();
            
            ResultSet results = pstmt.getGeneratedKeys();
            while (results.next()) {
                return results.getInt(1);
            }
            return -1;
    }

    public void updateExerciseSet(Connection connection, int id, int newWorkingSets, int newRepetitions, double newWorkingWeight, int orderNumber) throws SQLException {
            String sql = "UPDATE ExerciseSet SET workingSets = ?, repetitions = ?, workingWeight = ?, orderNumber = ? WHERE id = ?;";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, newWorkingSets);
            pstmt.setInt(2, newRepetitions);
            pstmt.setDouble(3, newWorkingWeight);
            pstmt.setInt(4, orderNumber);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
    }
    
    public void removeExerciseSet(Connection connection, int id) throws SQLException {
            String sql = "DELETE FROM ExerciseSet WHERE id = ?;";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
    }
    
    public void removeExerciseSetList(Connection connection, List<Integer> idList) throws SQLException {
            String sql = "DELETE FROM ExerciseSet WHERE id = ?;";
            for (int id : idList) {
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
    }
    
    public ExerciseSet getExerciseSet(Connection connection, int id) throws SQLException {
        ExerciseSet exerciseSet = null;
            String sql = "SELECT * FROM ExerciseSet WHERE id = ?;";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            ResultSet results = pstmt.executeQuery();
            while (results.next()) {
                return new ExerciseSet(
                    results.getInt("id"),
                    results.getInt("workingSets"),
                    results.getInt("repetitions"),
                    results.getDouble("workingWeight"),
                    results.getInt("orderNumber")
                );
            }
        return exerciseSet;
    }
    
    public List<ExerciseSet> getExerciseSetList(Connection connection, List<Integer> IdList) throws SQLException {
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
            String sql = "SELECT * FROM ExerciseSet WHERE id = ?;";
            for (int id : IdList) {
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, id);
                
                ResultSet results = pstmt.executeQuery();
                while (results.next()) {
                    exerciseSetList.add(
                        new ExerciseSet(
                            results.getInt("id"),
                            results.getInt("workingSets"),
                            results.getInt("repetitions"),
                            results.getDouble("workingWeight"),
                            results.getInt("orderNumber")
                        )
                    );
                }
            }
        return exerciseSetList;
    }
    
    /*
    // temporary
    public List<ExerciseSet> list(Connection connection) throws SQLException {
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
            String sql = "SELECT * FROM ExerciseSet;";
            
            ResultSet results = connection.prepareStatement(sql).executeQuery();
            while (results.next()) {
                exerciseSetList.add(
                    new ExerciseSet(
                        results.getInt("id"),
                        results.getInt("workingSets"),
                        results.getInt("repetitions"),
                        results.getDouble("workingWeight"),
                        results.getInt("orderNumber")
                    )
                );
            }
        return exerciseSetList;
    }
    */
}
