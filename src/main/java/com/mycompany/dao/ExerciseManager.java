
package com.mycompany.dao;

import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.domain.ExerciseSet;
import java.sql.SQLException;

public interface ExerciseManager {
    
    Exercise createNewExercise(ExerciseInfo exerciseInfo) throws SQLException;
    
    void addExerciseSetToExercise(Exercise exercise, ExerciseSet exerciseSet);
}
