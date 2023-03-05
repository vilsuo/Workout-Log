
package com.mycompany.domain.types;

import java.util.HashMap;
import java.util.Map;

public enum Exercise {
    BENCH_PRESS("Bench press"),
    OVERHEAD_PRESS("Overhead press"),
    BARBELL_SQUAT("Barbell squat"),
    DEADLIFT("Deadlift");
    
    private static final Map<String, Exercise> BY_LABEL = new HashMap<>();
    
    static {
        for (Exercise exerciseName: Exercise.values()) {
            BY_LABEL.put(exerciseName.label, exerciseName);
        }
    }
    
    public final String label;
            
    private Exercise(String label) {
        this.label = label;
    }
    
    public static Exercise valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }
}
