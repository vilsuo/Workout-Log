package com.mycompany.domain.templates;

import com.mycompany.domain.ExerciseSet;
import com.mycompany.utilities.NumberGenerator;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class SmolovJrTemplate {
    
    public static ObservableList<ExerciseSet> createExeciseSetList(
            double oneRepetitionMax, double weeklyIncrement, int weekNumber, int dayNumber) {
        
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        
        int workingSets = calculateNumberOfWorkingSets(dayNumber);
        int repetitions = calculateNumberOfWorkingRepetitions(dayNumber);
        
        int percentage = calculatePercentage(dayNumber);
        double increment = (weekNumber - 1) * weeklyIncrement;
        double workingWeight = calculateWorkingWeight(percentage, oneRepetitionMax, increment);
        
        int tempId = NumberGenerator.generateNextNegativeNumber();
        int tempOrderNumber = -1;
        
        exerciseSetList.add(
            new ExerciseSet(
                tempId,
                workingSets,
                repetitions,
                workingWeight,
                tempOrderNumber
            )
        );
        return FXCollections.observableList(exerciseSetList);
    }
    
    /**
     * 
     * @param dayNumber integer in the set [1-4]
     * @return          1 -> 70, 2 -> 75, 3 -> 80, 4 -> 85
     */
    private static int calculatePercentage(int dayNumber ){
        if (dayNumber < 1 || dayNumber > 4) {
            throw new IllegalArgumentException("Invalid dayNumber: " + dayNumber);
        }
        return 70 + 5 * (dayNumber - 1);
    }
    
    /**
     * 
     * @param dayNumber integer in the set [1-4]
     * @return          1 -> 6, 2 -> 7, 3 -> 8, 4 -> 10
     */
    private static int calculateNumberOfWorkingSets(int dayNumber) {
        switch (dayNumber) {
            case 1:
                return 6;
            case 2:
                return 7;
            case 3:
                return 8;
            case 4:
                return 10;
            default:
                throw new IllegalArgumentException("Invalid dayNumber: " + dayNumber);
        }
        
        // cyclic
        //return 6 + ((dayNumber - 1) % 4) + ((dayNumber % 4 == 0) ? 1 : 0);
    }
    
    /**
     * 
     * @param dayNumber integer in the set [1-4]
     * @return          1 -> 6, 2 -> 5, 3 -> 4, 4 -> 3
     */
    private static int calculateNumberOfWorkingRepetitions(int dayNumber) {
        if (dayNumber < 1 || dayNumber > 4) {
            throw new IllegalArgumentException("Invalid dayNumber: " + dayNumber);
        }
        
        return 7 - dayNumber;
        
        // cyclic
        //return 6 - ((dayNumber - 1) % 4);
    }
    
    private static double calculateWorkingWeight(double percentage, double oneRepMax, double extra) {
        return getRounded(percentage/100.0 * oneRepMax + extra);
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
