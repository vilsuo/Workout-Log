
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
    
    private String databasePath;
    
    public ExerciseSetDaoImpl(String databasePath) {
        this.databasePath = databasePath;
    }
    
    public ExerciseSetDaoImpl() {
        databasePath = App.DATABASE_PATH;
    }
    
    private Connection createConnectionAndEnsureDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection(this.databasePath, "sa", "");
        String sql = "CREATE TABLE IF NOT EXISTS ExerciseSet ("
                    + "id INTEGER AUTO_INCREMENT PRIMARY KEY, "
                    + "workingSets INTEGER NOT NULL, "
                    + "repetitions INTEGER NOT NULL, "
                    + "workingWeight DOUBLE PRECISION NOT NULL, "
                    + "orderNumber INTEGER NOT NULL"
                    + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        return conn;
    }

    @Override
    public int createItem(int workingSets, int repetitions, double workingWeight, int orderNumber) throws SQLException {
       try (Connection conn = createConnectionAndEnsureDatabase()) {
            String sql = "INSERT INTO ExerciseSet "
                       + "(workingSets, repetitions, workingWeight, orderNumber) "
                       + "VALUES (?, ?, ?, ?);";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS )) {
                pstmt.setInt(1, workingSets);
                pstmt.setInt(2, repetitions);
                pstmt.setDouble(3, workingWeight);
                pstmt.setInt(4, orderNumber);
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
    public void updateItem(int id, int newWorkingSets, int newRepetitions, double newWorkingWeight, int orderNumber) throws SQLException {
        try (Connection conn = createConnectionAndEnsureDatabase()) {
            String sql = "UPDATE ExerciseSet "
                       + "SET workingSets = ?, repetitions = ?, workingWeight = ?, orderNumber = ?"
                       + "WHERE id = ?;";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, newWorkingSets);
                pstmt.setInt(2, newRepetitions);
                pstmt.setDouble(3, newWorkingWeight);
                pstmt.setInt(4, orderNumber);
                pstmt.setInt(5, id);
                pstmt.executeUpdate();
            }
        }
    }
    
    /*
    @Override
    public void updateItemOrderNumber(int id, int newOrderNumber) throws SQLException {
        try (Connection conn = createConnectionAndEnsureDatabase()) {
            String sql = "UPDATE ExerciseSet "
                       + "SET orderNumber = ? "
                       + "WHERE id = ?;";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, newOrderNumber);
                pstmt.setInt(2, id);
                pstmt.executeUpdate();
            }
        }
    }
    
    @Override
    public void updateItemsOrderNumbers(List<Integer> idList, List<Integer> newOrderNumberList) throws SQLException {
        try (Connection conn = createConnectionAndEnsureDatabase()) {
            String sql = "UPDATE ExerciseSet "
                       + "SET orderNumber = ? "
                       + "WHERE id = ?;";
            for (int i = 0; i < idList.size(); ++i) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, newOrderNumberList.get(i));
                    pstmt.setInt(2, idList.get(i));
                    pstmt.executeUpdate();
                }
            }
        }
    }
    */
    
    @Override
    public void removeItem(int id) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "DELETE FROM ExerciseSet WHERE id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
        }
    }
    
    @Override
    public void removeItems(List<Integer> idList) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "DELETE FROM ExerciseSet WHERE id = ?;";
            for (int id : idList) {
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }
            }
        }
    }
    
    @Override
    public ExerciseSet getItem(int id) throws SQLException {
        ExerciseSet exerciseSet = null;
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "SELECT * "
                       + "FROM ExerciseSet "
                       + "WHERE id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet results = pstmt.executeQuery()) {
                    while (results.next()) {
                        return new ExerciseSet(
                            results.getInt("id"),
                            results.getInt("workingSets"),
                            results.getInt("repetitions"),
                            results.getDouble("workingWeight"),
                            results.getInt("orderNumber")
                        );
                    }
                }
            }
        }
        return exerciseSet;
    }
    
    @Override
    public List<ExerciseSet> getItems(List<Integer> IdList) throws SQLException {
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "SELECT * FROM ExerciseSet WHERE id = ?;";
            for (int id : IdList) {
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, id);
                    try (ResultSet results = pstmt.executeQuery()) {
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
                }
            }
        }
        return exerciseSetList;
    }
    
    // temporary
    public List<ExerciseSet> list() throws SQLException {
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "SELECT * FROM ExerciseSet;";
            try (ResultSet results = connection.prepareStatement(sql).executeQuery()) {
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
        }
        return exerciseSetList;
    }
}
