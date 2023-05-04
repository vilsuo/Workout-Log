
package com.mycompany.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WorkoutDaoImpl {
    
    public int createWorkout(Connection connection, String name, Date date, int orderNumber) throws SQLException {
        String sql = "INSERT INTO Workout (name, date, orderNumber) VALUES (?, ?, ?);";
        PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS );
        pstmt.setString(1, name);
        pstmt.setDate(2, date);
        pstmt.setInt(3, orderNumber);
        pstmt.executeUpdate();
        
        ResultSet results = pstmt.getGeneratedKeys();
        while (results.next()) {
            return results.getInt(1);
        }
        return -1;
    }
    
    public String getWorkoutName(Connection connection, int id) throws SQLException {
        String sql = "SELECT name FROM Workout WHERE id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        
        ResultSet results = pstmt.executeQuery();
        while (results.next()) {
            return results.getString("name");
        }
        return "";
    }
    
    public int getWorkoutOrderNumber(Connection connection, int id) throws SQLException {
        String sql = "SELECT orderNumber FROM Workout WHERE id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        
        ResultSet results = pstmt.executeQuery();
        while (results.next()) {
            return results.getInt("orderNumber");
        }
        return -1;
    }
    
    public void updateWorkoutName(Connection connection, int id, String newName) throws SQLException {
        String sql = "UPDATE Workout SET name = ? WHERE id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, newName);
        pstmt.setInt(2, id);
        pstmt.executeUpdate();
    }
    
    public void updateWorkoutOrderNumber(Connection connection, int id, int newOrderNumber) throws SQLException {
        String sql = "UPDATE Workout SET orderNumber = ? WHERE id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, newOrderNumber);
        pstmt.setInt(2, id);
        pstmt.executeUpdate();
    }
    
    public Date getWorkoutDate(Connection connection, int workoutId) throws SQLException {
        String sql = "SELECT date FROM Workout WHERE id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, workoutId);
        
        ResultSet results = pstmt.executeQuery();
        while (results.next()) {
            return results.getDate("date");
        }
        return null;
    }
    
    public List<Integer> getWorkoutIdListByDate(Connection connection, Date date) throws SQLException {
        List<Integer> idList = new ArrayList<>();
        String sql = "SELECT id FROM Workout WHERE date = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setDate(1, date);
        
        ResultSet results = pstmt.executeQuery();
        while (results.next()) {
            idList.add(results.getInt("id"));
        }
        return idList;
    }
    
    public List<Integer> getWorkoutIdListByDate(Connection connection, Date startDate, Date endDate) throws SQLException {
        List<Integer> idList = new ArrayList<>();
        
        String sql = "SELECT id FROM Workout WHERE date >= ? AND date <= ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setDate(1, startDate);
        pstmt.setDate(2, endDate);
        
        ResultSet results = pstmt.executeQuery();
        while (results.next()) {
            idList.add(results.getInt("id"));
        }
        return idList;
    }
    
    public List<Integer> getAllWorkoutIds(Connection connection) throws SQLException {
        List<Integer> idList = new ArrayList<>();
        
        String sql = "SELECT id FROM Workout;";
        ResultSet results = connection.prepareStatement(sql).executeQuery();
        while (results.next()) {
            idList.add(results.getInt("id"));
        }
        return idList;
    }
    
    public List<Date> getDateList(Connection connection) throws SQLException {
        List<Date> dateList = new ArrayList<>();
        
        String sql = "SELECT date FROM Workout;";
        ResultSet results = connection.prepareStatement(sql).executeQuery();
        while (results.next()) {
            dateList.add(results.getDate("date"));
        }
        return dateList;
    }
    
    public List<Date> getWorkoutDatesBetween(Connection connection, Date startDate, Date endDate) throws SQLException {
        List<Date> dateList = new ArrayList<>();
        
        String sql = "SELECT date FROM Workout WHERE date >= ? AND date <= ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setDate(1, startDate);
        pstmt.setDate(2, endDate);
        
        ResultSet results = pstmt.executeQuery();
        while (results.next()) {
            dateList.add(results.getDate("date"));
        }
        return dateList;
    }
    
    public void removeWorkout(Connection connection, int id) throws SQLException {
        String sql = "DELETE FROM Workout WHERE id = ?;";
        
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }
}
