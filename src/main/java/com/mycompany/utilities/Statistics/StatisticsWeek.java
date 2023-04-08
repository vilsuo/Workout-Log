
package com.mycompany.utilities.Statistics;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StatisticsWeek extends StatisticsPeriod {
    
    private static final String WEEK_PATTERN = "ww/YYYY";

    public StatisticsWeek(LocalDate ld) {
        super(ld);
    }
    
    @Override
    public void advance() {
        super.ld = super.ld.plusWeeks(1);
    }
    
    @Override
    public String formatted() {
        return super.ld.format(DateTimeFormatter.ofPattern(WEEK_PATTERN));
    }
}
