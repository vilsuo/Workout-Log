
package com.mycompany.domain.templates;

import com.mycompany.domain.ExerciseSet;
import com.mycompany.utilities.NumberGenerator;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Wendler531Template {
    
    //private static final ManagerImpl manager = new ManagerImpl(App.DATABASE_PATH);
    
    private final static int TRAINING_ONE_REPETITION_MAX = 90;
    
    /*
    public static Exercise createExercise(Workout workout, ExerciseInfo exerciseInfo, 
            double oneRepetitionMax, double weeklyIncrement, int weekNumber) throws SQLException {
        
        int exerciseOrderNumber = workout.getExerciseList().size() + 1;
        int exerciseId = manager.createExercise(workout.getId(), exerciseInfo.getId(), exerciseOrderNumber);
        Exercise exercise = new Exercise(exerciseId, exerciseInfo, exerciseOrderNumber);
        
        double trainingOneRepetitionMax =
            (oneRepetitionMax * TRAINING_ONE_REPETITION_MAX / 100.0)
            + (weekNumber - 1) * weeklyIncrement;
        
        for (int setNumber = 1; setNumber <= 3; ++setNumber) {
            int repetitions = calculateNumberOfWorkingRepetitions(weekNumber, setNumber);
            int percentage = calculatePercentage(weekNumber, setNumber);
            double workingWeight = calculateWorkingWeight(percentage, trainingOneRepetitionMax);

            int exerciseSetId = manager.createExerciseSet(1, repetitions, workingWeight, setNumber);
            ExerciseSet exerciseSet = new ExerciseSet(exerciseSetId, 1, repetitions, workingWeight, setNumber);

            manager.addExerciseSetToExercise(exerciseId, exerciseSetId);
            exercise.addExerciseSet(exerciseSet);
        }
        return exercise;
    }
    */
    
    public static ObservableList<ExerciseSet> createExerciseSetList(
            double oneRepetitionMax, int weekNumber) {
        
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        double trainingOneRepetitionMax =
            (oneRepetitionMax * TRAINING_ONE_REPETITION_MAX / 100.0);
        
        for (int setNumber = 1; setNumber <= 3; ++setNumber) {
            int repetitions = calculateNumberOfWorkingRepetitions(weekNumber, setNumber);
            int percentage = calculatePercentage(weekNumber, setNumber);
            double workingWeight = calculateWorkingWeight(percentage, trainingOneRepetitionMax);
            
            int tempId = NumberGenerator.generateNextNegativeNumber();
            int tempOrderNumber = -1;
            
            exerciseSetList.add(
                new ExerciseSet(
                    tempId,
                    1,
                    repetitions,
                    workingWeight,
                    tempOrderNumber
                )
            );
        }
        return FXCollections.observableList(exerciseSetList);
    }
    
    
    /**
     * @param week      integer in the set [1, 4]
     * @param setNumber integer in the set [1, 3]
     * @return 
     */
    private static int calculatePercentage(int week, int setNumber) {
        if (week < 4) {
            return 50 + 5 * week + 10 * setNumber;
        }
        
        // week == 4
        return 30 + 10 * setNumber;
    }
    
    /**
     * @param week      integer in the set [1, 4]
     * @param setNumber integer in the set [1, 3]
     * @return 
     */
    private static int calculateNumberOfWorkingRepetitions(int week, int setNumber) {
        switch (week) {
            case 1:
                return 5;
            case 2:
                return 3;
            case 3:
                // new setNumber: 1, 2, 3
                // return value:  5, 3, 1
                return 7 - 2 * setNumber;
            default:
                // week == 4
                return 5;
        }
    }
    
    private static double calculateWorkingWeight(int percentage, double trainingOneRepetitionMax) {
        return getRounded(percentage/100.0 * trainingOneRepetitionMax);
    }
    
    /**
     * @param value Value to round. Must be non-negative
     * 
     * @return      this.oneRepMax rounded to closest 2.5 increment
     */
    private static double getRounded(double value) {
        double toNearest = 2.5;
        double rounded  = value - (value % toNearest);
        if (((value % toNearest) - toNearest / 2) >= 0) {
            rounded += toNearest;
        }
        return rounded;
    }
}
