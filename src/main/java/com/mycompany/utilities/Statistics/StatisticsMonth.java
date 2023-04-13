
package com.mycompany.utilities.Statistics;

import java.time.LocalDate;

public final class StatisticsMonth extends StatisticsPeriod {

    public StatisticsMonth(LocalDate ld) {
        super(ld);
    }
    
    @Override
    public boolean isBeforeOrInTheSamePeriodAs(StatisticsPeriod sp) {
        if (ld.getYear() < sp.ld.getYear()) {
            return true;
            
        } else if (ld.getYear() == sp.ld.getYear()) {
            return ld.getMonthValue() <= sp.ld.getMonthValue();
            
        }
        return false;
    }
    
    @Override
    public void advance() {
        ld = ld.plusMonths(1);
    }
    
    @Override
    public String formatted() {
        return ld.getMonthValue() + "/" + ld.getYear();
    }
}
