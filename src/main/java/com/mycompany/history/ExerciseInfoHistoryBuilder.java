
package com.mycompany.history;

import com.mycompany.domain.Exercise;
import com.mycompany.domain.ExerciseInfo;
import com.mycompany.domain.ExerciseSet;
import com.mycompany.domain.Workout;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class ExerciseInfoHistoryBuilder {
    
    /**
     * 
     * @param workoutList expects a completely sorted list:
     *      - workouts sorted first by date ascending, then by order number
     *        descending
     *      - workout exercises and exercise sets similarly
     * 
     * @param exerciseInfo
     * @return 
     */
    public static ObservableList<String> createHistoryStringList(
            final List<Workout> workoutList, final ExerciseInfo exerciseInfo) {
        
        ObservableList<String> obsList = FXCollections.observableArrayList();
        
        for (Workout workout : workoutList) {
            boolean dateAdded = false;
            
            StringBuilder sb = new StringBuilder();
            for (Exercise exercise : workout.getExerciseList()) {
                if (exercise.getExerciseInfo().equals(exerciseInfo)) {
                    if (exercise.getExerciseSetList().isEmpty())  {
                        continue;
                    }
                    
                    if (!dateAdded) {
                        sb.append(workout.getDate());
                        sb.append(", ");
                        sb.append(workout.getName());
                        dateAdded = true;
                    }
                    for (ExerciseSet exerciseSet : exercise.getExerciseSetList()) {
                        sb.append("\n\t");
                        sb.append(exerciseSet.toString());
                    }
                }
            }
            if (dateAdded) {
                obsList.add(sb.toString());
            }
        }
        FXCollections.reverse(obsList);
        
        return obsList;
    }
}
