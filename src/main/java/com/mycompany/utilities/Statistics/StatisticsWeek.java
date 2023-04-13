
package com.mycompany.utilities.Statistics;

import java.time.LocalDate;

public final class StatisticsWeek extends StatisticsPeriod {
    
    public StatisticsWeek(LocalDate ld) {
        super(ld);
    }
    
    @Override
    public boolean isBeforeOrInTheSamePeriodAs(StatisticsPeriod sp) {
        if (ld.getYear() < sp.ld.getYear()) {
            return true;
            
        } else if (ld.getYear() == sp.ld.getYear()) {
            
            if (ld.getDayOfYear() <= sp.ld.getDayOfYear()) {
                return true;
                
            } else {
                // now ld is after sp.ld...
                
                if (getWeekNumber() < sp.getWeekNumber()) {
                    // this means that sp must have a week number of 52>= but sp
                    // has a local date in the start of the year
                    return false;
                    
                } else if (getWeekNumber() == sp.getWeekNumber()) {
                    // it is possible to have same week numbers but the other
                    
                    return (ld.getMonthValue() - sp.ld.getMonthValue()) <= 1 ;
                    
                } else {
                    return false;
                }
            }
        }
        return false;
    }
    
    @Override
    public void advance() {
        ld = ld.plusWeeks(1);
    }
    
    @Override
    public String formatted() {
        int weekNumber = getWeekNumber();
        int yearNumber = ld.getYear();
        
        // can be the the last week of previous year
        if (ld.getMonthValue() == 1 && weekNumber >= 52) {
            --yearNumber;
        }
        
        return weekNumber + "/" + yearNumber;
    }
}
