
package com.mycompany.utilities.Statistics;

import java.time.LocalDate;
import java.time.temporal.WeekFields;

public abstract class StatisticsPeriod {
    
    protected LocalDate ld;
    
    public StatisticsPeriod(LocalDate ld) {
        // LocalDate is immutable
        this.ld = ld;
    }
    
    public LocalDate getLocalDate() {
        return ld;
    }
    
    public abstract boolean isBeforeOrInTheSamePeriodAs(StatisticsPeriod sp);
    
    public abstract void advance();
    
    public abstract String formatted();
    
    protected int getWeekNumber() {
        // can be the the last week of previous year:
        // (if 1.1.2023 the week number is 52
        return ld.get(WeekFields.ISO.weekOfWeekBasedYear());
    }
}
