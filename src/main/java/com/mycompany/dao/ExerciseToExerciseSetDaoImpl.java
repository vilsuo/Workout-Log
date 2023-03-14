
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

public class ExerciseToExerciseSetDaoImpl implements ExerciseToExerciseSetDao {
    
    private String databasePath;
    
    public ExerciseToExerciseSetDaoImpl(String databasePath) {
        this.databasePath = databasePath;
    }
    
    public ExerciseToExerciseSetDaoImpl() {
        databasePath = App.DATABASE_PATH;
    }
    
    private Connection createConnectionAndEnsureDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection(this.databasePath, "sa", "");
        /*
        String sql = "CREATE TABLE IF NOT EXISTS ExerciseInfo ("
                    + "id IDENTITY NOT NULL PRIMARY KEY, "
                    + "name VARCHAR NOT NULL, "
                    + "category VARCHAR NOT NULL"
                    + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        
        sql = "CREATE TABLE IF NOT EXISTS Exercise ("
                    + "id IDENTITY NOT NULL PRIMARY KEY, "
                    + "exerciseInfo_id BIGINT NOT NULL, "
                    + "FOREIGN KEY (exerciseInfo_id) REFERENCES ExerciseInfo(id)"
                    + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        
        String sql = "CREATE TABLE IF NOT EXISTS ExerciseSet ("
                    + "id IDENTITY NOT NULL PRIMARY KEY, "
                    + "workingSets INTEGER NOT NULL, "
                    + "repetitions INTEGER NOT NULL, "
                    + "workingWeight DOUBLE PRECISION NOT NULL, "
                    + "orderNumber INTEGER NOT NULL"
                    + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        */
        String sql = "CREATE TABLE IF NOT EXISTS ExerciseToExerciseSet ("
                    + "exercise_id INTEGER NOT NULL, "
                    + "exerciseSet_id INTEGER NOT NULL, "
                    + "FOREIGN KEY (exercise_id) REFERENCES Exercise(id), "
                    + "FOREIGN KEY (exerciseSet_id) REFERENCES ExerciseSet(id)"
                    + ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
        return conn;
    }
    
    @Override
    public void createItem(int exerciseId,  int exerciseSetId) throws SQLException {
        try (Connection conn = createConnectionAndEnsureDatabase()) {
            String sql = "INSERT INTO ExerciseToExerciseSet "
                       + "(exercise_id, exerciseSet_id) "
                       + "VALUES (?, ?);";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, exerciseId);
                pstmt.setInt(2, exerciseSetId);
                pstmt.executeUpdate();
            }
        }
    }
    
    @Override
    public void removeItemsByExerciseSet(int exerciseSetId) throws SQLException {
        // delete from this link table the exercise set id values
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "DELETE FROM ExerciseToExerciseSet WHERE exerciseSet_id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, exerciseSetId);
                pstmt.executeUpdate();
            }
        }
        
        /*
        // delete all exercise sets associated with the exercise id
        ExerciseSetDao exerciseSetDatabase = new ExerciseSetDaoImpl();
        exerciseSetDatabase.removeItem(exerciseSetId);
        */
    }
    
    @Override
    public void removeItemsByExercise(int exerciseId) throws SQLException {
        /*
        List<Integer> exerciseIdsExerciseSetIdList = getExerciseSetIdList(exerciseId);
        */
        
        // delete from this link table the exercise id values
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "DELETE FROM ExerciseToExerciseSet WHERE exercise_id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, exerciseId);
                pstmt.executeUpdate();
            }
        }
        /*
        // delete all exercise sets associated with the exercise id
        ExerciseSetDao exerciseSetDatabase = new ExerciseSetDaoImpl();
        exerciseSetDatabase.removeItems(exerciseIdsExerciseSetIdList);
        
        // finally removeItems exercise
        ExerciseDao exerciseDatabase = new ExerciseDaoImpl();
        exerciseDatabase.removeItem(exerciseId);
        */
    }
    
    @Override
    public List<Integer> getExerciseSetIdList(int exerciseId) throws SQLException {
        List<Integer> exerciseSetList = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            String sql = "SELECT exerciseSet_id "
                       + "FROM ExerciseToExerciseSet "
                       + "WHERE exercise_id = ?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, exerciseId);
                try (ResultSet result = pstmt.executeQuery()) {
                    while (result.next()) {
                        exerciseSetList.add(result.getInt("exerciseSet_id"));
                    }
                }
            }
        }
        return exerciseSetList;
    }
}
