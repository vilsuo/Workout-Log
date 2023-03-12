
package com.mycompany.dao;

import com.mycompany.application.App;
import com.mycompany.domain.ExerciseSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExerciseSetDaoImpl implements ExerciseSetDao {
    
    private final String databasePath = App.DATABASE_PATH;
    
    private Connection createConnectionAndEnsureDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection(this.databasePath, "sa", "");
        String sql = "CREATE TABLE IF NOT EXISTS ExerciseSet ("
                    + "id IDENTITY NOT NULL PRIMARY KEY, "
                    + "workingSets INTEGER NOT NULL, "
                    + "repetitions INTEGER NOT NULL, "
                    + "workingWeight DOUBLE PRECISION NOT NULL"
                    + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        return conn;
    }

    @Override
    public int create(int workingSets, int repetitions, double workingWeight) throws SQLException {
       try (Connection conn = createConnectionAndEnsureDatabase()) {
            String sql = "INSERT INTO ExerciseSet "
                       + "(workingSets, repetitions, workingWeight) "
                       + "VALUES (?, ?, ?);";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS )) {
                pstmt.setInt(1, workingSets);
                pstmt.setInt(2, repetitions);
                pstmt.setDouble(3, workingWeight);
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
    public void update(int id, int workingSets, int repetitions, double workingWeight) throws SQLException {
        try (Connection conn = createConnectionAndEnsureDatabase()) {
            String sql = "UPDATE ExerciseSet "
                       + "SET workingSets = ?, repetitions = ?, workingWeight = ? "
                       + "WHERE id = ?;";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, workingSets);
                pstmt.setInt(2, repetitions);
                pstmt.setDouble(3, workingWeight);
                pstmt.setInt(4, id);
                pstmt.executeUpdate();
            }
        }
    }

    @Override
    public void remove(int id) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "DELETE FROM ExerciseSet WHERE id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
        }
    }
    
    @Override
    public void remove(List<Integer> idList) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            for (int id : idList) {
                String sql = "DELETE FROM ExerciseSet WHERE id = ?;";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }
            }
        }
    }
    
    public List<ExerciseSet> list() throws SQLException {
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM ExerciseSet;"
            );
            ResultSet results = stmt.executeQuery();
            
            while (results.next()) {
                exerciseSetList.add(
                    new ExerciseSet(
                        results.getInt("id"),
                        results.getInt("workingSets"),
                        results.getInt("repetitions"),
                        results.getDouble("workingWeight")
                    )
                );
            }
        }
        return exerciseSetList;
    }
    
}
