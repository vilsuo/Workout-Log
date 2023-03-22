
package com.mycompany.dao;

import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.domain.ExerciseSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
TODO
- test this class
- make interface for this class
*/

public class ExerciseManagerImpl {
    
    private String databasePath;
    
    private ExerciseInfoDaoImpl exerciseInfoDatabase;
    private ExerciseSetDaoImpl exerciseSetDatabase;
    private ExerciseDaoImpl exerciseDatabase;
    private ExerciseToExerciseSetDaoImpl linkDatabase;
    
    public ExerciseManagerImpl(String databasePath) {
        this.databasePath = databasePath;
        exerciseInfoDatabase = new ExerciseInfoDaoImpl(this.databasePath);
        exerciseSetDatabase = new ExerciseSetDaoImpl(this.databasePath);
        exerciseDatabase = new ExerciseDaoImpl(this.databasePath);
        linkDatabase = new ExerciseToExerciseSetDaoImpl(this.databasePath);
    }

    private Connection createConnectionAndEnsureDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(this.databasePath, "sa", "");
        
        String createExerciseInfoTable = "CREATE TABLE IF NOT EXISTS ExerciseInfo ("
                    + "id INTEGER AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR NOT NULL, "
                    + "category VARCHAR NOT NULL"
                    + ");";
        
        String createExerciseTable = "CREATE TABLE IF NOT EXISTS Exercise ("
                    + "id INTEGER AUTO_INCREMENT PRIMARY KEY, "
                    + "exerciseInfo_id INTEGER NOT NULL, "
                    + "FOREIGN KEY (exerciseInfo_id) REFERENCES ExerciseInfo(id), "
                    + "orderNumber INTEGER NOT NULL"
                    + ");";
        
        String createExerciseSetTable = "CREATE TABLE IF NOT EXISTS ExerciseSet ("
                    + "id INTEGER AUTO_INCREMENT PRIMARY KEY, "
                    + "workingSets INTEGER NOT NULL, "
                    + "repetitions INTEGER NOT NULL, "
                    + "workingWeight DOUBLE PRECISION NOT NULL, "
                    + "orderNumber INTEGER NOT NULL"
                    + ");";
        
        String createExerciseToExerciseSetTable = "CREATE TABLE IF NOT EXISTS ExerciseToExerciseSet ("
                    + "exercise_id INTEGER NOT NULL, "
                    + "exerciseSet_id INTEGER NOT NULL, "
                    + "FOREIGN KEY (exercise_id) REFERENCES Exercise(id), "
                    + "FOREIGN KEY (exerciseSet_id) REFERENCES ExerciseSet(id)"
                    + ");";
        
        String[] ss = new String[]{
            createExerciseInfoTable,
            createExerciseTable,
            createExerciseSetTable,
            createExerciseToExerciseSetTable
        };

        for (String sql : ss) {
            connection.prepareStatement(sql).execute();
        }
        
        return connection;
    }
    
    public int createExerciseInfo(String name, String category) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            return exerciseInfoDatabase.createItem(connection, name, category);
        }
    }
    
    public boolean updateExerciseInfoName(int id, String newName) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            return exerciseInfoDatabase.updateItemName(connection, id, newName);
        }
    }
    
    public void updateExerciseInfoCategory(int id, String newCategory) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            exerciseInfoDatabase.updateItemCategory(connection, id, newCategory);
        }
    }
    
    public List<ExerciseInfo> getAllExerciseInfos() throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            return exerciseInfoDatabase.getAllItems(connection);
        }
    }
    
    public int createExerciseSet(int workingSets, int repetitions, double workingWeight, int orderNumber) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            return exerciseSetDatabase.createItem(connection, workingSets, repetitions, workingWeight, orderNumber);
        }
    }
    
    public void updateExerciseSet(int id, int newWorkingSets, int newRepetitions, double newWorkingWeight, int orderNumber) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            exerciseSetDatabase.updateItem(connection, id, newWorkingSets, newRepetitions, newWorkingWeight, orderNumber);
        }
    }
    
    
    // make param exerciseInfoId instead?
    /**
     * 
     * @param exerciseInfo  Assumes item is already in the ExerciseInfo-database.
     * @return
     * @throws SQLException 
     */
    public Exercise createNewExercise(ExerciseInfo exerciseInfo, int orderNumber) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            int exerciseId = exerciseDatabase.createItem(connection, exerciseInfo.getId(), orderNumber);
            if (exerciseId == -1) {
                return null;
            }
            return new Exercise(exerciseId, exerciseInfo, orderNumber);
        }
    }

    public void addExerciseSetToExercise(int exerciseId, int exerciseSetId) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            linkDatabase.createItem(connection, exerciseId, exerciseSetId);
        }
    }
    
    /**
     * Assumes the exercise set belongs only to a single exercise, if not it is 
     * removed from all exercises.
     *
     * @param exerciseSetId Removed from link database, and exercise set database.
     * @throws SQLException 
     */
    public void removeExerciseSet(int exerciseSetId) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            linkDatabase.removeItemsByExerciseSet(connection, exerciseSetId);
            exerciseSetDatabase.removeItem(connection, exerciseSetId);
        }
    }
    
    /**
     * @param exerciseId
     * @return              ExerciseSets are sorted by orderNumber
     * @throws SQLException 
     */
    public Exercise getExercise(int exerciseId) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            int exerciseInfoId = exerciseDatabase.getExerciseInfoId(connection, exerciseId);
            int exerciseOrderNumber = exerciseDatabase.getItemOrderNumber(connection, exerciseId);
            
            ExerciseInfo exerciseInfo = exerciseInfoDatabase.getItem(connection, exerciseInfoId);

            List<Integer> exerciseSetIdList = linkDatabase.getExerciseSetIdList(connection, exerciseId);

            List<ExerciseSet> exerciseSetList = exerciseSetDatabase.getItems(connection, exerciseSetIdList);
            Collections.sort(exerciseSetList);

            return new Exercise(exerciseId, exerciseInfo, exerciseSetList, exerciseOrderNumber);
        }
    }
    
    public List<Exercise> getAllExercises() throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            List<Exercise> exerciseList = new ArrayList<>();
            List<Integer> exerciseIdList = exerciseDatabase.getAllExerciseIds(connection);
            for (int id : exerciseIdList) {
                exerciseList.add(getExercise(id));
            }
            Collections.sort(exerciseList);
            return exerciseList;
        }
    }
    
    /**
     * Removes also all ExerciseSets associated with the Exercise.
     * 
     * @param exerciseId
     * @throws SQLException 
     */
    public void removeExercise(int exerciseId) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            List<Integer> exerciseSetIdList = linkDatabase.getExerciseSetIdList(connection, exerciseId);

            // 1. delete link table
            linkDatabase.removeItemsByExercise(connection, exerciseId);

            // delete sets
            exerciseSetDatabase.removeItems(connection, exerciseSetIdList);

            // delete exercise
            exerciseDatabase.removeItem(connection, exerciseId);
        }
    }
    
    /**
     * Removes also all Exercises and ExerciseSets associated with the ExerciseInfo.
     * 
     * @param exerciseInfoId
     * @throws SQLException 
     */
    public void removeExerciseInfo(int exerciseInfoId) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
        // exercises associated with deleted exerciseInfo
            List<Integer> exerciseIdList = exerciseDatabase.getExerciseIdList(connection, exerciseInfoId);

            for (int exerciseId : exerciseIdList) {
                // get exercise set associated with the exercise
                List<Integer> exerciseSetIdList = linkDatabase.getExerciseSetIdList(connection, exerciseId);

                // 1. delete link table
                linkDatabase.removeItemsByExercise(connection, exerciseId);

                // 2.1 delete exercise sets
                exerciseSetDatabase.removeItems(connection, exerciseSetIdList);

                // 2. delete exercises
                exerciseDatabase.removeItem(connection, exerciseId);
            }

            // 3. delete exerciseInfo
            exerciseInfoDatabase.removeItem(connection, exerciseInfoId);
        }
    }
}
