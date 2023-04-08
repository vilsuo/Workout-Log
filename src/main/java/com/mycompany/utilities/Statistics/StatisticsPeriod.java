
package com.mycompany.utilities.Statistics;

import java.time.LocalDate;

public abstract class StatisticsPeriod {
    
    protected LocalDate ld;
    
    public StatisticsPeriod(LocalDate ld) {
        // LocalDate is immutable
        this.ld = ld;
    }
    
    public LocalDate getLocalDate() {
        return ld;
    }
    
    public boolean isBeforeOrEqual(LocalDate ld) {
        return !this.ld.isAfter(ld);
    }
    
    public abstract void advance();
    
    public abstract String formatted();
}
