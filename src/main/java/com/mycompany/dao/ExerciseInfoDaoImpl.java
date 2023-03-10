
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
    public int create(String name, String category) throws SQLException {
        if (getIndexByName(name) != -1) {
            return -1;
        }
        
        try (Connection conn = createConnectionAndEnsureDatabase()) {
            String sql = "INSERT INTO ExerciseInfo (name, category) VALUES (?, ?);";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS )) {
                pstmt.setString(1, name);
                pstmt.setString(2, category);
                pstmt.executeUpdate();
                
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    while (rs.next()) {
                        return rs.getInt(1);
                    }
                    
                    return -1; //?
                }
            }
        }
    }
    
    @Override
    public boolean updateName(int id, String newName) throws SQLException {
        if (getIndexByName(newName) != -1) {
            return false;
        }
        
        try (Connection conn = createConnectionAndEnsureDatabase()) {
            String sql = "UPDATE ExerciseInfo SET name = ? WHERE id = ?;";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newName);
                pstmt.setInt(2, id);
                pstmt.executeUpdate();
                return true;
            }
        }
    }
    
    @Override
    public void updateCategory(int id, String newCategory) throws SQLException {
        try (Connection conn = createConnectionAndEnsureDatabase()) {
            String sql = "UPDATE ExerciseInfo SET category = ? WHERE id = ?;";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newCategory);
                pstmt.setInt(2, id);
                pstmt.executeUpdate();
            }
        }
    }
    
    @Override
    public void renameCategory(String currentCategory, String newCategory) throws SQLException {
        try (Connection conn = createConnectionAndEnsureDatabase()) {
            String sql =  "UPDATE ExerciseInfo SET category = ? WHERE category = ?;";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newCategory);
                pstmt.setString(2, currentCategory);
                pstmt.executeUpdate();
            }
        }
    }
    
    @Override
    public void remove(int id) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "DELETE FROM ExerciseInfo WHERE id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
        }
    }
    
    @Override
    public List<ExerciseInfo> getItems() throws SQLException {
        List<ExerciseInfo> exerciseInfos = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "SELECT * FROM ExerciseInfo;";
            try (ResultSet results = connection.prepareStatement(sql).executeQuery()) {
                while (results.next()) {
                    exerciseInfos.add(
                        new ExerciseInfo(
                            results.getInt("id"),
                            results.getString("name"),
                            results.getString("category")
                        )
                    );
                }
            }
        }
        return exerciseInfos;
    }
    
    @Override
    public List<ExerciseInfo> getItemsByCategory(String category) throws SQLException {
        List<ExerciseInfo> exerciseInfos = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "SELECT * FROM ExerciseInfo WHERE category = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, category);
                try (ResultSet results = connection.prepareStatement(sql).executeQuery()) {
                    while (results.next()) {
                        exerciseInfos.add(
                            new ExerciseInfo(
                                results.getInt("id"),
                                results.getString("name"),
                                results.getString("category")
                            )
                        );
                    }
                }
            }
        }
        return exerciseInfos;
    }
    
    @Override
    public int getIndexByName(String name) throws SQLException {
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
    
    
    /*
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
    */
}
