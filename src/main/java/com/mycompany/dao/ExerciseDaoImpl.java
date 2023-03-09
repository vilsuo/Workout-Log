
package com.mycompany.dao;

import com.mycompany.domain.Exercise;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExerciseDaoImpl implements ExerciseDao {
    
    private String databasePath;

    public ExerciseDaoImpl(String databasePath) {
        this.databasePath = databasePath;
    }
    
    private Connection createConnectionAndEnsureDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection(this.databasePath, "sa", "");
        try {
            conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS Exercise("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "name VARCHAR(50) "
                        + "category VARCHAR(50)"
                        + ")"
            ).execute();
        } catch (SQLException t) {
            
        }
        
        return conn;
    }

    @Override
    public List<String> getExerciseCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT category FROM Exercise"
            );
            ResultSet results = stmt.executeQuery();
            
            while (results.next()) {
                categories.add(results.getString("name"));
            }
        }
        
        return categories;
    }

    @Override
    public List<String> getExerciseNameByCategory(String category) throws SQLException {
        List<String> exercises = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT name FROM Exercise WHERE category = ?"
            );
            stmt.setString(1, category);
            ResultSet results = stmt.executeQuery();
            
            while (results.next()) {
                exercises.add(results.getString("name"));
            }
        }
        return exercises;
    }

    @Override
    public boolean addExercise(Exercise exercise) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean updateExercise(Exercise exerciseOld, Exercise exerciseNew) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeExercise(Exercise exercise) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
