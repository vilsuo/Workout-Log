
package com.mycompany.utilities.Statistics;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StatisticsMonth extends StatisticsPeriod {

    private static final String MONTH_PATTERN = "MM/YYYY";

    public StatisticsMonth(LocalDate ld) {
        super(ld);
    }
    
    @Override
    public void advance() {
        super.ld = super.ld.plusMonths(1);
    }
    
    @Override
    public String formatted() {
        return super.ld.format(DateTimeFormatter.ofPattern(MONTH_PATTERN));
    }
}
