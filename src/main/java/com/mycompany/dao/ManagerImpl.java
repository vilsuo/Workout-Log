
package com.mycompany.dao;

import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.domain.ExerciseSet;
import com.mycompany.domain.Workout;
import java.sql.Connection;
import java.sql.Date;
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

public class ManagerImpl {
    
    private String databasePath;
    
    private ExerciseInfoDaoImpl exerciseInfoDatabase;
    private ExerciseSetDaoImpl exerciseSetDatabase;
    private ExerciseDaoImpl exerciseDatabase;
    private WorkoutDaoImpl workoutDatabase;
    private ExerciseToExerciseSetDaoImpl exerciseToExerciseSetLinkDatabase;
    private WorkoutToExerciseDaoImpl workoutToExerciseLinkDatabase;
    
    public ManagerImpl(String databasePath) {
        this.databasePath = databasePath;
        exerciseInfoDatabase = new ExerciseInfoDaoImpl();
        exerciseSetDatabase = new ExerciseSetDaoImpl();
        exerciseDatabase = new ExerciseDaoImpl();
        workoutDatabase = new WorkoutDaoImpl();
        exerciseToExerciseSetLinkDatabase = new ExerciseToExerciseSetDaoImpl();
        workoutToExerciseLinkDatabase = new WorkoutToExerciseDaoImpl();
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
        
        String createWorkoutTable = "CREATE TABLE IF NOT EXISTS Workout ("
                    + "id INTEGER AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR, "
                    + "date DATE NOT NULL, "
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
        
        String createWorkoutToExerciseTable = "CREATE TABLE IF NOT EXISTS WorkoutToExercise ("
                    + "workout_id INTEGER NOT NULL, "
                    + "exercise_id INTEGER NOT NULL, "
                    + "FOREIGN KEY (workout_id) REFERENCES Workout(id), "
                    + "FOREIGN KEY (exercise_id) REFERENCES Exercise(id) "
                    + ");";
        
        String[] ss = new String[]{
            createExerciseInfoTable,
            createExerciseTable,
            createWorkoutTable,
            createExerciseSetTable,
            createExerciseToExerciseSetTable,
            createWorkoutToExerciseTable
        };

        for (String sql : ss) {
            connection.prepareStatement(sql).execute();
        }
        
        return connection;
    }
    
    public ExerciseInfo createExerciseInfo(String name, String category) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            int exerciseInfoId = exerciseInfoDatabase.createExerciseInfo(connection, name, category);
            
            if (exerciseInfoId == -1) {
                return null;
            }
            
            return new ExerciseInfo(exerciseInfoId, name, category);
        }
    }
    
    public boolean updateExerciseInfoName(int id, String newName) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            return exerciseInfoDatabase.updateExerciseInfoName(connection, id, newName);
        }
    }
    
    public void updateExerciseInfoCategory(int id, String newCategory) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            exerciseInfoDatabase.updateExerciseInfoCategory(connection, id, newCategory);
        }
    }
    
    public List<ExerciseInfo> getAllExerciseInfos() throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            return exerciseInfoDatabase.getAllExerciseInfos(connection);
        }
    }
    
    
    public int createExerciseSet(int workingSets, int repetitions, double workingWeight, int orderNumber) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            return exerciseSetDatabase.createExerciseSet(connection, workingSets, repetitions, workingWeight, orderNumber);
        }
    }
    
    public void updateExerciseSet(int id, int newWorkingSets, int newRepetitions, double newWorkingWeight, int orderNumber) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            exerciseSetDatabase.updateExerciseSet(connection, id, newWorkingSets, newRepetitions, newWorkingWeight, orderNumber);
        }
    }
    
    
    // make param exerciseInfoId instead?
    public Exercise createExercise(int workoutId, ExerciseInfo exerciseInfo, int orderNumber) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            int exerciseId = exerciseDatabase.createExercise(connection, exerciseInfo.getId(), orderNumber);
            if (exerciseId == -1) {
                return null;
            }
            
            workoutToExerciseLinkDatabase.createItem(connection, workoutId, exerciseId);
            
            return new Exercise(exerciseId, exerciseInfo, orderNumber);
        }
    }

    public void addExerciseSetToExercise(int exerciseId, int exerciseSetId) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            exerciseToExerciseSetLinkDatabase.createItem(connection, exerciseId, exerciseSetId);
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
            exerciseToExerciseSetLinkDatabase.removeItemsByExerciseSet(connection, exerciseSetId);
            exerciseSetDatabase.removeExerciseSet(connection, exerciseSetId);
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
            int exerciseOrderNumber = exerciseDatabase.getExerciseOrderNumber(connection, exerciseId);
            
            ExerciseInfo exerciseInfo = exerciseInfoDatabase.getExerciseInfo(connection, exerciseInfoId);

            List<Integer> exerciseSetIdList = exerciseToExerciseSetLinkDatabase.getExerciseSetIdList(connection, exerciseId);

            List<ExerciseSet> exerciseSetList = exerciseSetDatabase.getExerciseSetList(connection, exerciseSetIdList);
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
    
    public Workout createWorkout(String name, Date date, int orderNumber) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            int workoutId = workoutDatabase.createWorkout(connection, name, date, orderNumber);
            
            if (workoutId == -1) {
                return null;
            }
            
            return new Workout(workoutId, date, orderNumber);
        }
    }
    
    public List<Date> getWorkoutDates() throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            return workoutDatabase.getDateList(connection);
        }
    }
    
    public List<Workout> getWorkoutsByDate(Date date) throws SQLException {
        List<Workout> workoutList = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            
            List<Integer> workoutIdList = workoutDatabase.getWorkoutIdListByDate(connection, date);
            for (int workoutId : workoutIdList) {
                List<Exercise> exerciseList = new ArrayList<>();
                
                List<Integer> exerciseIdList = workoutToExerciseLinkDatabase.getExerciseIdList(connection, workoutId);
                for (int exerciseId : exerciseIdList) {
                    
                    int exerciseInfoId = exerciseDatabase.getExerciseInfoId(connection, exerciseId);
                    ExerciseInfo exerciseInfo = exerciseInfoDatabase.getExerciseInfo(connection, exerciseInfoId);
                    
                    List<Integer> exerciseSetIdList = exerciseToExerciseSetLinkDatabase.getExerciseSetIdList(connection, exerciseId);
                    List<ExerciseSet> exerciseSetList = exerciseSetDatabase.getExerciseSetList(connection, exerciseSetIdList);
                    
                    int exerciseOrderNumber = exerciseDatabase.getExerciseOrderNumber(connection, exerciseId);
                    
                    exerciseList.add(
                        new Exercise(
                            exerciseId,
                            exerciseInfo,
                            exerciseSetList,
                            exerciseOrderNumber
                        )
                    );
                }
                
                int workoutOrderNumber = workoutDatabase.getWorkoutOrderNumber(connection, workoutId);
                String workoutName = workoutDatabase.getWorkoutName(connection, workoutId);
                workoutList.add(
                    new Workout(
                        workoutId,
                        workoutName,
                        exerciseList,
                        date,
                        workoutOrderNumber
                    )
                );
            }
        }
        
        // sort by order number
        Collections.sort(workoutList);
        return workoutList;
    }
    
    /**
     * Removes also all ExerciseSets associated with the Exercise.
     * 
     * @param exerciseId
     * @throws SQLException 
     */
    public void removeExercise(int exerciseId) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            List<Integer> exerciseSetIdList = exerciseToExerciseSetLinkDatabase.getExerciseSetIdList(connection, exerciseId);

            // 1. delete link tables values
            exerciseToExerciseSetLinkDatabase.removeItemsByExercise(connection, exerciseId);
            workoutToExerciseLinkDatabase.removeItemsByExercise(connection, exerciseId);

            // delete sets
            exerciseSetDatabase.removeExerciseSetList(connection, exerciseSetIdList);

            // delete exercise
            exerciseDatabase.removeExercise(connection, exerciseId);
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
                List<Integer> exerciseSetIdList = exerciseToExerciseSetLinkDatabase.getExerciseSetIdList(connection, exerciseId);

                // 1. delete exercise to exercise set link table values
                exerciseToExerciseSetLinkDatabase.removeItemsByExercise(connection, exerciseId);

                // 2 delete exercise sets
                exerciseSetDatabase.removeExerciseSetList(connection, exerciseSetIdList);
                
                // 3. delete workout to exercise link table values
                workoutToExerciseLinkDatabase.removeItemsByExercise(connection, exerciseId);

                // 4. delete exercises
                exerciseDatabase.removeExercise(connection, exerciseId);
            }

            // 5. delete exerciseInfo
            exerciseInfoDatabase.removeExerciseInfo(connection, exerciseInfoId);
        }
    }
    
    public void removeWorkout(int workoutId) throws SQLException {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            // 1. get exercises associated with workout
            List<Integer> exerciseIdList = workoutToExerciseLinkDatabase.getExerciseIdList(connection, workoutId);
            System.out.println("1");
            // 2. remove workout to exercise link table values
            workoutToExerciseLinkDatabase.removeItemsByWorkout(connection, workoutId);
            System.out.println("2");
            for (int exerciseId : exerciseIdList) {
                // 3. get exercise sets associated with the exercise
                List<Integer> exerciseSetIdList = exerciseToExerciseSetLinkDatabase.getExerciseSetIdList(connection, exerciseId);
                System.out.println("3");
                // 4. remove exercise to exercise set link table values
                exerciseToExerciseSetLinkDatabase.removeItemsByExercise(connection, exerciseId);
                System.out.println("4");
                // 5. remove exercise sets associated with the exercise
                exerciseSetDatabase.removeExerciseSetList(connection, exerciseSetIdList);
                System.out.println("5");
                // 6. remove exercise associated with the workout
                exerciseDatabase.removeExercise(connection, exerciseId);
            }
            System.out.println("6");
            // 7. remove workout
            workoutDatabase.removeWorkout(connection, workoutId);
            System.out.println("7");
        }
    }
}
