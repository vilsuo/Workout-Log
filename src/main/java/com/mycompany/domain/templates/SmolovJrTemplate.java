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
    
    private static int calculatePercentage(int dayNumber ){
        return 70 + 5 * (dayNumber - 1);
    }
    
    // day:          1, 2, 3, 4,  5, 6, 7, 8,...
    // working sets: 6, 7, 8, 10, 6, 7, 8, 10,...
    private static int calculateNumberOfWorkingSets(int dayNumber) {
        return 6 + ((dayNumber - 1) % 4) + ((dayNumber % 4 == 0) ? 1 : 0);
    }
    
    // day:         1, 2, 3, 4, 5, 6, 7, 8,...
    // repetitions: 6, 5, 4, 3, 6, 5, 4, 3,...
    private static int calculateNumberOfWorkingRepetitions(int dayNumber) {
        return 6 - ((dayNumber - 1) % 4);
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
