/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dao;

import com.mycompany.application.App;
import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.domain.ExerciseSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExerciseDaoImpl implements ExerciseDao {
    
    private final String databasePath = App.DATABASE_PATH;
    
    private Connection createConnectionAndEnsureDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection(this.databasePath, "sa", "");
        String sql = "CREATE TABLE IF NOT EXISTS Exercise ("
                    + "id IDENTITY NOT NULL PRIMARY KEY, "
                    + "FOREIGN KEY (exerciseInfo_id) REFERENCES ExerciseInfo"
                    + ");";
                    // "FOREIGN KEY (workout_id) REFERENCES Workout"
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        return conn;
    }
    
    @Override
    public int create(ExerciseInfo exerciseInfo) throws SQLException {
        try (Connection conn = createConnectionAndEnsureDatabase()) {
            String sql = "INSERT INTO Exercise "
                       + "(exerciseInfo_id) "
                       + "VALUES (?);";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS )) {
                pstmt.setInt(1, exerciseInfo.getId());
                pstmt.executeUpdate();
                
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    while (rs.next()) {
                        return rs.getInt(1);
                    }
                    
                    return -1;
                }
            }
        }
    }
    
    @Override
    public void remove(int id) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "DELETE FROM Exercise WHERE id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
        }
    }
    
    public List<Exercise> list() throws SQLException {
        List<Exercise> exerciseList = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM Exercise;"
            );
            ResultSet results = stmt.executeQuery();
            
            ExerciseInfoDaoImpl exerciseInfoDatabase = new ExerciseInfoDaoImpl();
            while (results.next()) {
                ExerciseInfo exerciseInfo = exerciseInfoDatabase
                        .getExerciseInfo(results.getInt("exerciseInfo_id"));
                
                exerciseList.add(
                    new Exercise(
                        results.getInt("id"),
                        exerciseInfo
                    )
                );
            }
        }
        return exerciseList;
    }
}
