
package com.mycompany.dao;

import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.domain.ExerciseSet;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExerciseToExerciseSetDaoImplTest {
    
    private ExerciseToExerciseSetDao database;
    private String databaseFile;
    
    @BeforeEach
    public void init() {
        this.databaseFile = "workoutLog-database-test-" + UUID.randomUUID().toString().substring(0, 6);
        this.database = new ExerciseToExerciseSetDaoImpl("jdbc:h2:./" + databaseFile);
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
        ExerciseInfo exerciseInfo = new ExerciseInfo(1, "name", "category");
        Exercise exercise1 = new Exercise(1, exerciseInfo);
        Exercise exercise2 = new Exercise(2, exerciseInfo);
        
        ExerciseSet exerciseSet11 = new ExerciseSet(2, 1, 1, 1, 1);
        ExerciseSet exerciseSet12 = new ExerciseSet(3, 1, 1, 1, 1);
        
        assertTrue(database.getExerciseSetIdList(exercise1.getId()).isEmpty());
        
        // create exercise with one set
        database.createItem(exercise1.getId(), exerciseSet11.getId());
        
        List<Integer> list = database.getExerciseSetIdList(exercise1.getId());
        assertTrue(list.size() == 1);
        assertTrue(list.get(0) == exerciseSet11.getId());
        
        database.createItem(exercise1.getId(), exerciseSet12.getId());
        
        list = database.getExerciseSetIdList(exercise1.getId());
        assertTrue(list.size() == 2);
        assertTrue(list.contains(exerciseSet11.getId()));
        assertTrue(list.contains(exerciseSet12.getId()));
        
        database.createItem(exercise2.getId(), exerciseSet12.getId());
        list = database.getExerciseSetIdList(exercise2.getId());
        assertTrue(list.size() == 1);
        assertTrue(list.contains(exerciseSet12.getId()));
    }
}
