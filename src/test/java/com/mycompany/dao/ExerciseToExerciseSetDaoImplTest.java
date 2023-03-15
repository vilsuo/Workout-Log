
package com.mycompany.dao;

import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.domain.ExerciseSet;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExerciseToExerciseSetDaoImplTest {
    
    private ExerciseToExerciseSetDao linkDatabase;
    private ExerciseInfoDao exerciseInfoDatabase;
    private ExerciseSetDao exerciseSetDatabase;
    private ExerciseDao exerciseDatabase;
    private String databaseFile;
    
    private int exerciseInfoId1;
    private int exerciseInfoId2;
    
    private int exerciseId1;
    private int exerciseId2;
    
    private int exerciseSetId1;
    private int exerciseSetId2;
    
    
    
    @BeforeEach
    public void init() throws SQLException {
        this.databaseFile = "workoutLog-database-test-" + UUID.randomUUID().toString().substring(0, 6);
        this.linkDatabase = new ExerciseToExerciseSetDaoImpl("jdbc:h2:./" + databaseFile);
        this.exerciseInfoDatabase = new ExerciseInfoDaoImpl("jdbc:h2:./" + databaseFile);
        this.exerciseSetDatabase  = new ExerciseSetDaoImpl("jdbc:h2:./" + databaseFile);
        this.exerciseDatabase = new ExerciseDaoImpl("jdbc:h2:./" + databaseFile);
        
        
        String name1 = "bench";
        String name2 = "ohp";
        
        String category1 = "chest";
        String category2 = "shoulders";
        
        exerciseInfoId1 = exerciseInfoDatabase.createItem(name1, category1);
        exerciseInfoId2 = exerciseInfoDatabase.createItem(name2, category2);
        
        exerciseId1 = exerciseDatabase.createItem(exerciseInfoId1);
        exerciseId2 = exerciseDatabase.createItem(exerciseInfoId2);
        
        exerciseSetId1 = exerciseSetDatabase.createItem(3, 5, 100, 1);
        exerciseSetId2 = exerciseSetDatabase.createItem(1, 3, 110, 2);
        
    }
    
    @AfterEach
    public void tearDown() {
        try {
            new File(databaseFile + ".mv.db").delete();
            new File(databaseFile + ".trace.db").delete();
        } catch (Exception e) {

        }
    }
    
    @Test
    public void createItemTest() throws SQLException {
        linkDatabase.createItem(exerciseId1, exerciseSetId1);
        
        List<Integer> exerciseSetIdList1 = linkDatabase.getExerciseSetIdList(exerciseId1);
        assertTrue(exerciseSetIdList1.size() == 1);
        assertTrue(exerciseSetIdList1.get(0) == exerciseSetId1);
        
        linkDatabase.createItem(exerciseId1, exerciseSetId2);
        
        exerciseSetIdList1 = linkDatabase.getExerciseSetIdList(exerciseId1);
        assertTrue(exerciseSetIdList1.size() == 2);
        assertTrue(exerciseSetIdList1.contains(exerciseSetId1));
        assertTrue(exerciseSetIdList1.contains(exerciseSetId2));
        
        List<Integer> exerciseSetIdList2 = linkDatabase.getExerciseSetIdList(exerciseId2);
        assertTrue(exerciseSetIdList2.isEmpty());
        
        linkDatabase.createItem(exerciseId2, exerciseSetId2);
        exerciseSetIdList2 = linkDatabase.getExerciseSetIdList(exerciseId2);
        assertTrue(exerciseSetIdList2.size() == 1);
        assertTrue(exerciseSetIdList2.contains(exerciseSetId2));
    }
    
    @Test
    public void removeItemsByExerciseTest() throws SQLException {
        linkDatabase.createItem(exerciseId1, exerciseSetId1);
        linkDatabase.createItem(exerciseId1, exerciseSetId2);
        
        linkDatabase.createItem(exerciseId2, exerciseSetId2);
        
        List<Integer> exerciseSetIdList1 = linkDatabase.getExerciseSetIdList(exerciseId1);
        assertTrue(exerciseSetIdList1.contains(exerciseSetId1));
        assertTrue(exerciseSetIdList1.contains(exerciseSetId2));
        
        List<Integer> exerciseSetIdList2 = linkDatabase.getExerciseSetIdList(exerciseId2);
        assertTrue(exerciseSetIdList2.size() == 1);
        assertTrue(exerciseSetIdList2.get(0) == exerciseSetId2);
        
        // exercise id 1 gets removed
        linkDatabase.removeItemsByExercise(exerciseId1);
        exerciseSetIdList1 = linkDatabase.getExerciseSetIdList(exerciseId1);
        assertTrue(exerciseSetIdList1.isEmpty());
        
        // exercise id 2 remains unchanged
        exerciseSetIdList2 = linkDatabase.getExerciseSetIdList(exerciseId2);
        assertTrue(exerciseSetIdList2.size() == 1);
        assertTrue(exerciseSetIdList2.get(0) == exerciseSetId2);
    }
    
    @Test
    public void removeItemsByExerciseSetTest() throws SQLException {
        linkDatabase.createItem(exerciseId1, exerciseSetId1);
        linkDatabase.createItem(exerciseId1, exerciseSetId2);
        
        linkDatabase.createItem(exerciseId2, exerciseSetId2);
        
        List<Integer> exerciseSetIdList1 = linkDatabase.getExerciseSetIdList(exerciseId1);
        assertTrue(exerciseSetIdList1.size() == 2);
        assertTrue(exerciseSetIdList1.contains(exerciseSetId1));
        assertTrue(exerciseSetIdList1.contains(exerciseSetId2));
        
        List<Integer> exerciseSetIdList2 = linkDatabase.getExerciseSetIdList(exerciseId2);
        assertTrue(exerciseSetIdList2.size() == 1);
        assertTrue(exerciseSetIdList2.get(0) == exerciseSetId2);
        
        linkDatabase.removeItemsByExerciseSet(exerciseSetId2);
        
        // only one set gets removed
        exerciseSetIdList1 = linkDatabase.getExerciseSetIdList(exerciseId1);
        assertTrue(exerciseSetIdList1.size() == 1);
        assertTrue(exerciseSetIdList1.contains(exerciseSetId1));
        assertFalse(exerciseSetIdList1.contains(exerciseSetId2));
        
        // the only one set gets removed
        exerciseSetIdList2 = linkDatabase.getExerciseSetIdList(exerciseId2);
        assertTrue(exerciseSetIdList2.isEmpty());
    }
}
