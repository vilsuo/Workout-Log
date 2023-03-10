
package com.mycompany.dao;

import com.mycompany.domain.ExerciseInfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExerciseInfoDaoImpl implements ExerciseInfoDao {
    
    private final String path = "jdbc:h2:./workoutLog-database";
    private String databasePath;

    public ExerciseInfoDaoImpl(String databasePath) {
        this.databasePath = path;
    }
    
    private Connection createConnectionAndEnsureDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection(this.databasePath, "sa", "");
        String sql = "CREATE TABLE IF NOT EXISTS ExerciseInfo ("
                    + "id IDENTITY NOT NULL PRIMARY KEY, "
                    + "name VARCHAR NOT NULL, "
                    + "category VARCHAR NOT NULL"
                    + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        return conn;
    }
    
    @Override
    public int addExerciseInfo(String name, String category) throws SQLException {
        if (getIndexByName(name) != -1) {
            return -1;
        }
        String sql = "INSERT INTO ExerciseInfo (name, category) VALUES (?, ?);";
        try (Connection conn = createConnectionAndEnsureDatabase()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS )) {
                pstmt.setString(1, name);
                pstmt.setString(2, category);
            
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    while (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1; //?
    }
    
    private int getIndexByName(String name) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM ExerciseInfo WHERE name = ?;"
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
    
    private int getIndexByCategory(String category) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM ExerciseInfo WHERE category = ?;"
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
    public List<ExerciseInfo> getExerciseInfos() throws SQLException {
        List<ExerciseInfo> exerciseInfos = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            ResultSet results = connection.prepareStatement(
                "SELECT * FROM ExerciseInfo;"
            ).executeQuery();
            
            while (results.next()) {
                exerciseInfos.add(new ExerciseInfo(
                        results.getInt("id"),
                        results.getString("name"),
                        results.getString("category")
                    )
                );
            }
        }
        return exerciseInfos;
    }
    
    @Override
    public List<String> getNamesByCategory(String category) throws SQLException {
        List<String> names = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT name FROM ExerciseInfo WHERE category = ?;"
            );
            stmt.setString(1, category);
            ResultSet results = stmt.executeQuery();
            
            while (results.next()) {
                names.add(results.getString("name"));
            }
        }
        return names;
    }
    
    @Override
    public List<String> getCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT category FROM ExerciseInfo;"
            );
            ResultSet results = stmt.executeQuery();
            
            while (results.next()) {
                categories.add(results.getString("category"));
            }
        }
        return categories;
    }
    
    @Override
    public boolean changeCategoryByName(String name, String newCategory) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            // check if exercise  does not exist
            if (getIndexByName(name) == -1) {
                return false;
            }
            
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE ExerciseInfo SET category = ? WHERE name = ?;"
            );
            stmt.setString(1, newCategory);
            stmt.setString(2, name);
            stmt.executeUpdate();
            
            return true;
        }
    }
    
    @Override
    public boolean removeByName(String name) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            // check if current name does not exist
            if (getIndexByName(name) == -1) {
                return false;
            }
            
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM ExerciseInfo WHERE name = ?;"
            );
            stmt.setString(1, name);
            stmt.executeUpdate();
            
            return true;
        }
    }
    
    @Override
    public boolean removeByCategory(String category) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            // check if category to delete does not exist
            if (getIndexByCategory(category) == -1) {
                return false;
            }
            
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM ExerciseInfo WHERE category = ?;"
            );
            stmt.setString(1, category);
            stmt.executeUpdate();
            
            return true;
        }
    }
    
    @Override
    public boolean changeName(String currentName, String newName) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            // check if current name does not exist
            if (getIndexByName(currentName) == -1) {
                return false;
            }
            
            // check if new name already exists
            if (getIndexByName(newName) != -1) {
                return false;
            }
            
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE ExerciseInfo SET name = ? WHERE name = ?;"
            );
            stmt.setString(1, newName);
            stmt.setString(2, currentName);
            stmt.executeQuery();
            
            return true;
        }
    }
    
    @Override
    public boolean changeCategory(String currentCategory, String newCategory) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            // check if current category does not exist
            if (getIndexByCategory(currentCategory) == -1) {
                return false;
            }
            
            // check if new category already exists
            if (getIndexByCategory(newCategory) != -1) {
                return false;
            }
            
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE ExerciseInfo SET category = ? WHERE category = ?;"
            );
            stmt.setString(1, newCategory);
            stmt.setString(2, currentCategory);
            stmt.executeQuery();
            
            return true;
        }
    }
}
