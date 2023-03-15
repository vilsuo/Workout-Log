
package com.mycompany.dao;

import com.mycompany.application.App;
import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.domain.ExerciseSet;
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
    
    private ExerciseInfoDao exerciseInfoDatabase = new ExerciseInfoDaoImpl();
    private ExerciseSetDao exerciseSetDatabase = new ExerciseSetDaoImpl();
    private ExerciseDao exerciseDatabase = new ExerciseDaoImpl();
    private ExerciseToExerciseSetDao linkDatabase = new ExerciseToExerciseSetDaoImpl();
    
    public ExerciseManagerImpl(String databasePath) {
        this.databasePath = databasePath;
        exerciseInfoDatabase = new ExerciseInfoDaoImpl(this.databasePath);
        exerciseSetDatabase = new ExerciseSetDaoImpl(this.databasePath);
        exerciseDatabase = new ExerciseDaoImpl(this.databasePath);
        linkDatabase = new ExerciseToExerciseSetDaoImpl(this.databasePath);
    }
    
    public ExerciseManagerImpl() {
        databasePath = App.DATABASE_PATH;
        exerciseInfoDatabase = new ExerciseInfoDaoImpl();
        exerciseSetDatabase = new ExerciseSetDaoImpl();
        exerciseDatabase = new ExerciseDaoImpl();
        linkDatabase = new ExerciseToExerciseSetDaoImpl();
    }

    
    /**
     * 
     * @param exerciseInfo  Assumes item is already in the ExerciseInfo-database.
     * @return
     * @throws SQLException 
     */
    public Exercise createNewExercise(ExerciseInfo exerciseInfo) throws SQLException {
        int exerciseId = exerciseDatabase.createItem(exerciseInfo.getId());
        if (exerciseId == -1) {
            return null;
        }
        return new Exercise(exerciseId, exerciseInfo);
    }

    /**
     * 
     * @param exercise
     * 
     * @param exerciseSet   Assumes item is already in the ExerciseSet-database.
     *                      Does not update order number.
     * 
     * @throws SQLException 
     */
    public void addExerciseSetToExercise(int exerciseId, int exerciseSetId) throws SQLException {
        linkDatabase.createItem(exerciseId, exerciseSetId);
    }
    
    /**
     * Assumes the exercise set belongs only to a single exercise, if not it is 
     * removed from all exercises.
     *
     * @param exerciseSetId Removed from link database, and exercise set database.
     * @throws SQLException 
     */
    public void removeExerciseSet(int exerciseSetId) throws SQLException {
        linkDatabase.removeItemsByExerciseSet(exerciseSetId);
        exerciseSetDatabase.removeItem(exerciseSetId);
    }
    
    /**
     * @param exerciseId
     * @return              ExerciseSets are sorted by orderNumber
     * @throws SQLException 
     */
    public Exercise getExercise(int exerciseId) throws SQLException {
        int exerciseInfoId = exerciseDatabase.getExerciseInfoId(exerciseId);
        ExerciseInfo exerciseInfo = exerciseInfoDatabase.getItem(exerciseInfoId);
        
        List<Integer> exerciseSetIdList = linkDatabase.getExerciseSetIdList(exerciseId);
        
        List<ExerciseSet> exerciseSetList = exerciseSetDatabase.getItems(exerciseSetIdList);
        Collections.sort(exerciseSetList);
        
        return new Exercise(exerciseId, exerciseInfo, exerciseSetList);
    }
    
    public List<Exercise> getAllExercises() throws SQLException {
        List<Exercise> exerciseList = new ArrayList<>();
        List<Integer> exerciseIdList = exerciseDatabase.getAllExerciseIds();
        for (int id : exerciseIdList) {
            exerciseList.add(getExercise(id));
        }
        return exerciseList;
    }
    
    /**
     * Removes also all ExerciseSets associated with the Exercise.
     * 
     * @param exerciseId
     * @throws SQLException 
     */
    public void removeExercise(int exerciseId) throws SQLException {
        
        List<Integer> exerciseSetIdList = linkDatabase.getExerciseSetIdList(exerciseId);
        
        // 1. delete link table
        linkDatabase.removeItemsByExercise(exerciseId);
        
        // delete sets
        exerciseSetDatabase.removeItems(exerciseSetIdList);
        
        // delete exercise
        exerciseDatabase.removeItem(exerciseId);
    }
    
    /**
     * Removes also all Exercises and ExerciseSets associated with the ExerciseInfo.
     * 
     * @param exerciseInfoId
     * @throws SQLException 
     */
    public void removeExerciseInfo(int exerciseInfoId) throws SQLException {
        // exercises associated with deleted exerciseInfo
        List<Integer> exerciseIdList = exerciseDatabase.getExerciseIdList(exerciseInfoId);
        
        for (int exerciseId : exerciseIdList) {
            // get exercise set associated with the exercise
            List<Integer> exerciseSetIdList = linkDatabase.getExerciseSetIdList(exerciseId);
            
            // 1. delete link table
            linkDatabase.removeItemsByExercise(exerciseId);
            
            // 2.1 delete exercise sets
            exerciseSetDatabase.removeItems(exerciseSetIdList);
            
            // 2. delete exercises
            exerciseDatabase.removeItem(exerciseId);
        }
        
        // 3. delete exerciseInfo
        exerciseInfoDatabase.removeItem(exerciseInfoId);
    }
}
