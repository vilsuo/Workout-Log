
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
    
    private int getExerciseIndexByName(String name) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM Exercise WHERE name = ?"
            );
            stmt.setString(1, name);
            ResultSet results = stmt.executeQuery();
            
            if (results.next()) {
                return results.getInt(1);
            } else {
                return -1;
            }
        }
    }
    
    private int getExerciseIndexByCategory(String category) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM Exercise WHERE category = ?"
            );
            stmt.setString(1, category);
            ResultSet results = stmt.executeQuery();
            
            if (results.next()) {
                return results.getInt(1);
            } else {
                return -1;
            }
        }
    }
    
    @Override
    public List<Exercise> getExercises() throws SQLException {
        List<Exercise> exercises = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            ResultSet results = connection.prepareStatement(
                "SELECT * FROM Exercise"
            ).executeQuery();
            
            while (results.next()) {
                exercises.add(
                    new Exercise(
                        results.getInt("id"),
                        results.getString("name"),
                        results.getString("category")
                    )
                );
            }
        }
        return exercises;
    }
    
    @Override
    public List<String> getExerciseNamesByCategory(String category) throws SQLException {
        List<String> exerciseNames = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT name FROM Exercise WHERE category = ?"
            );
            stmt.setString(1, category);
            ResultSet results = stmt.executeQuery();
            
            while (results.next()) {
                exerciseNames.add(results.getString("name"));
            }
        }
        return exerciseNames;
    }
    
    @Override
    public List<String> getExerciseCategories() throws SQLException {
        List<String> exerciseCategories = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT category FROM Exercise"
            );
            ResultSet results = stmt.executeQuery();
            
            while (results.next()) {
                exerciseCategories.add(results.getString("name"));
            }
        }
        return exerciseCategories;
    }
    
    @Override
    public boolean changeExerciseCategory(String exerciseName, String newCategory) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            // check if exercise  does not exist
            if (getExerciseIndexByName(exerciseName) == -1) {
                return false;
            }
            
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE Exercise SET category = ? WHERE name = ?"
            );
            stmt.setString(1, newCategory);
            stmt.setString(2, exerciseName);
            stmt.executeUpdate();
            
            return true;
        }
    }
    
    @Override
    public boolean removeExercisesByName(String name) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            // check if current name does not exist
            if (getExerciseIndexByName(name) == -1) {
                return false;
            }
            
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Exercise WHERE name = ?"
            );
            stmt.setString(1, name);
            stmt.executeUpdate();
            
            return true;
        }
    }
    
    @Override
    public boolean removeExercisesByCategory(String category) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            // check if category to delete does not exist
            if (getExerciseIndexByCategory(category) == -1) {
                return false;
            }
            
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM Exercise WHERE category = ?"
            );
            stmt.setString(1, category);
            stmt.executeUpdate();
            
            return true;
        }
    }
    
    @Override
    public boolean renameExerciseName(String currentName, String newName) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            // check if current name does not exist
            if (getExerciseIndexByName(currentName) == -1) {
                return false;
            }
            
            // check if new name already exists
            if (getExerciseIndexByName(newName) != -1) {
                return false;
            }
            
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE Exercise SET name = ? WHERE name = ?"
            );
            stmt.setString(1, newName);
            stmt.setString(2, currentName);
            stmt.executeQuery();
            
            return true;
        }
    }
    
    @Override
    public boolean renameExerciseCategory(String currentCategory, String newCategory) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            // check if current category does not exist
            if (getExerciseIndexByCategory(currentCategory) == -1) {
                return false;
            }
            
            // check if new category already exists
            if (getExerciseIndexByCategory(newCategory) != -1) {
                return false;
            }
            
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE Exercise SET category = ? WHERE category = ?"
            );
            stmt.setString(1, newCategory);
            stmt.setString(2, currentCategory);
            stmt.executeQuery();
            
            return true;
        }
    }

    /**
     * Adds a exercise entry to Exercise-table, if the table does not already
     * contain a entry with the given name
     * 
     * @param name      Name of the exercise to add.
     * @param category  Category of the exercise to add
     * @return          True if adding was successful, false otherwise.
     * @throws java.sql.SQLException
     */
    @Override
    public boolean addExercise(String name, String category) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            // check if exercise already exists with the given name
            if (getExerciseIndexByName(name) != -1) {
                return false;
            }
            
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Exercise (name, category) VALUES(?, ?)"
            );
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.executeQuery();
            
            return true;
        }
    }
}
