
package com.mycompany.domain.templates;

import com.mycompany.domain.ExerciseSet;
import com.mycompany.utilities.NumberGenerator;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Wendler531Template {
    
    private final static int TRAINING_ONE_REPETITION_MAX_PERCENT = 90;
    
    public static ObservableList<ExerciseSet> createExerciseSetList(
            double oneRepetitionMax, int weekNumber) {
        
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        double trainingOneRepetitionMax =
            (oneRepetitionMax * TRAINING_ONE_REPETITION_MAX_PERCENT / 100.0);
        
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
     * @param weekNumber    integer in the set [1, 4]
     * @param setNumber     integer in the set [1, 3]
     * @return 
     */
    private static int calculatePercentage(int weekNumber, int setNumber) {
        if (setNumber < 1 || setNumber > 3) {
            throw new IllegalArgumentException("Invalid setNumber: " + setNumber);
        }
        
        if (0 < weekNumber && weekNumber < 4) {
            return 50 + 5 * weekNumber + 10 * setNumber;
            
        } else if (weekNumber == 4) {
            return 30 + 10 * setNumber;
            
        } else {
            throw new IllegalArgumentException("Invalid weekNumber: " + weekNumber);
        }
    }
    
    /**
     * @param weekNumber    integer in the set [1, 4]
     * @param setNumber     integer in the set [1, 3]
     * @return 
     */
    private static int calculateNumberOfWorkingRepetitions(int weekNumber, int setNumber) {
        switch (weekNumber) {
            case 1:
                return 5;
            case 2:
                return 3;
            case 3:
                if (setNumber < 1 || setNumber > 3) {
                    throw new IllegalArgumentException("Invalid setNumber: " + setNumber);
                }
                // setNumber:    1, 2, 3
                // return value: 5, 3, 1
                return 7 - 2 * setNumber;
            case 4:
                return 5;
            default:
                throw new IllegalArgumentException("Invalid weekNumber: " + weekNumber);
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
