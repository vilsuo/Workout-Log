
package com.mycompany.dao;

import com.mycompany.application.App;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExerciseDaoImpl implements ExerciseDao {
    
    private String databasePath;
    
    public ExerciseDaoImpl(String databasePath) {
        this.databasePath = databasePath;
    }
    
    public ExerciseDaoImpl() {
        databasePath = App.DATABASE_PATH;
    }
    
    private Connection createConnectionAndEnsureDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection(this.databasePath, "sa", "");
        String sql = "CREATE TABLE IF NOT EXISTS ExerciseInfo ("
                    + "id INTEGER AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR NOT NULL, "
                    + "category VARCHAR NOT NULL"
                    + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        
        sql = "CREATE TABLE IF NOT EXISTS Exercise ("
                    + "id INTEGER AUTO_INCREMENT PRIMARY KEY, "
                    + "exerciseInfo_id INTEGER NOT NULL, "
                    + "FOREIGN KEY (exerciseInfo_id) REFERENCES ExerciseInfo(id)"
                    + ");";
                    // "FOREIGN KEY (workout_id) REFERENCES Workout"
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        return conn;
    }
    
    @Override
    public int createItem(int exerciseInfoId) throws SQLException {
        try (Connection conn = createConnectionAndEnsureDatabase()) {
            String sql = "INSERT INTO Exercise "
                       + "(exerciseInfo_id) "
                       + "VALUES (?);";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS )) {
                pstmt.setInt(1, exerciseInfoId);
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
    public void removeItem(int id) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "DELETE FROM Exercise WHERE id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
        }
    }
    
    @Override
    public int getExerciseInfoId(int exerciseId) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "SELECT exerciseInfo_id FROM Exercise WHERE id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, exerciseId);
                try (ResultSet results = pstmt.executeQuery()) {
                    while (results.next()) {
                        return results.getInt("exerciseInfo_id");
                    }
                }
            }
        }
        return -1;
    }
    
    @Override
    public List<Integer> getExerciseIdList(int exerciseInfoId) throws SQLException {
        List<Integer> exerciseIdList = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "SELECT id FROM Exercise WHERE exerciseInfo_id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, exerciseInfoId);
                try (ResultSet results = pstmt.executeQuery()) {
                    while (results.next()) {
                        exerciseIdList.add(results.getInt("id"));
                    }
                }
            }
        }
        return exerciseIdList;
    }
    
    @Override
    public List<Integer> getAllExerciseIds() throws SQLException {
        List<Integer> exerciseIdList = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "SELECT id FROM Exercise;";
            try (ResultSet results = connection.prepareStatement(sql).executeQuery()) {
                
                while (results.next()) {
                    exerciseIdList.add(results.getInt("id"));
                }
            }
        }
        return exerciseIdList;
    }
}
