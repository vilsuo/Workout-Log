
package com.mycompany.dao;

import com.mycompany.domain.ExerciseInfo;
import java.io.File;
import java.sql.SQLException;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExerciseDaoImplTest {
    
    private ExerciseDao exerciseDatabase;
    private ExerciseInfoDao exerciseInfoDatabase;
    private String databaseFile;
    
    @BeforeEach
    public void init() {
        this.databaseFile = "workoutLog-database-test-" + UUID.randomUUID().toString().substring(0, 6);
        this.exerciseDatabase = new ExerciseDaoImpl("jdbc:h2:./" + databaseFile);
        this.exerciseInfoDatabase = new ExerciseInfoDaoImpl("jdbc:h2:./" + databaseFile);
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
    public void createAndRemoveItemTest() throws SQLException {
        String name1 = "bench";
        String category1 = "chest";
        int exerciseInfoId1 = exerciseInfoDatabase.createItem(name1, category1);
        ExerciseInfo exerciseInfo1 = new ExerciseInfo(exerciseInfoId1, name1, category1);
        
        String name2 = "dip";
        String category2 = "chest";
        int exerciseInfoId2 = exerciseInfoDatabase.createItem(name2, category2);
        ExerciseInfo exerciseInfo2 = new ExerciseInfo(exerciseInfoId2, name2, category2);
        
        int id1 = exerciseDatabase.createItem(exerciseInfo1.getId());
        int id2 = exerciseDatabase.createItem(exerciseInfo2.getId());
        
        assertTrue(exerciseDatabase.getExerciseInfoId(id1) == exerciseInfo1.getId());
        assertTrue(exerciseDatabase.getExerciseInfoId(id2) == exerciseInfo2.getId());
        
        exerciseDatabase.removeItem(id2);
        
        // id2 is removed, id not
        assertTrue(exerciseDatabase.getExerciseInfoId(id1) == exerciseInfo1.getId());
        assertTrue(exerciseDatabase.getExerciseInfoId(id2) == -1);
        
        // ExerciseInfo database values are not changed
        assertTrue(exerciseInfoDatabase.getItem(id1).getId() == exerciseInfo1.getId());
        assertTrue(exerciseInfoDatabase.getItem(id2).getId() == exerciseInfo2.getId());
    }
}
