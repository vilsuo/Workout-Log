
package com.mycompany.dao;

import com.mycompany.application.App;
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
    
    private String databasePath;
    
    public ExerciseInfoDaoImpl(String databasePath) {
        this.databasePath = databasePath;
    }
    
    public ExerciseInfoDaoImpl() {
        databasePath = App.DATABASE_PATH;
    }

    private Connection createConnectionAndEnsureDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection(databasePath, "sa", "");
        String sql = "CREATE TABLE IF NOT EXISTS ExerciseInfo ("
                    + "id INTEGER AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR NOT NULL, "
                    + "category VARCHAR NOT NULL"
                    + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        return conn;
    }
    
    @Override
    public int createItem(String name, String category) throws SQLException {
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
    public boolean updateItemName(int id, String newName) throws SQLException {
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
    public void updateItemCategory(int id, String newCategory) throws SQLException {
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
    public void removeItem(int id) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "DELETE FROM ExerciseInfo WHERE id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
        }
    }
    
    @Override
    public ExerciseInfo getItem(int id) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "SELECT * FROM ExerciseInfo WHERE id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet results = pstmt.executeQuery()) {
                    while (results.next()) {
                        return new ExerciseInfo(
                            results.getInt("id"),
                            results.getString("name"),
                            results.getString("category")
                        );
                    }
                    return null;
                }
            }
        }
    }
    
    @Override
    public List<ExerciseInfo> getItems(List<Integer> idList) throws SQLException {
        List<ExerciseInfo> exerciseInfoList = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "SELECT * FROM ExerciseInfo WHERE id = ?;";
            for (int id : idList) {
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, id);
                    try (ResultSet results = pstmt.executeQuery()) {
                        while (results.next()) {
                            exerciseInfoList.add(
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
        }
        return exerciseInfoList;
    }
    
    @Override
    public List<ExerciseInfo> getAllItems() throws SQLException {
        List<ExerciseInfo> exerciseInfoList = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "SELECT * FROM ExerciseInfo;";
            try (ResultSet results = connection.prepareStatement(sql).executeQuery()) {
                while (results.next()) {
                    exerciseInfoList.add(
                        new ExerciseInfo(
                            results.getInt("id"),
                            results.getString("name"),
                            results.getString("category")
                        )
                    );
                }
            }
        }
        return exerciseInfoList;
    }
    
    @Override
    public List<ExerciseInfo> getAllItemsByCategory(String category) throws SQLException {
        List<ExerciseInfo> exerciseInfos = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "SELECT * FROM ExerciseInfo WHERE category = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, category);
                try (ResultSet results = pstmt.executeQuery()) {
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
}
