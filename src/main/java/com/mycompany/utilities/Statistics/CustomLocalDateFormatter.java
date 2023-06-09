
package com.mycompany.utilities.Statistics;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class CustomLocalDateFormatter {
    
    public static String formatLocalDate(LocalDate localDate, String choice) {
        switch (choice) {
            case "Week":
                return new StatisticsWeek(localDate).formatted();
            case "Month":
                return new StatisticsMonth(localDate).formatted();
            default:
                throw new IllegalArgumentException("Invalid choice: " + choice);
        }
    }
    
    public static List<String> getFormattedLocalDatesBetween(LocalDate startLocalDate, LocalDate endLocalDate, String choice) {
        List<String> formattedLocalDateList = new ArrayList<>();
        
        StatisticsPeriod startSp;
        StatisticsPeriod endSp;
        switch (choice) {
            case "Week":
                startSp = new StatisticsWeek(startLocalDate);
                endSp = new StatisticsWeek(endLocalDate);
                break;
            case "Month":
                startSp = new StatisticsMonth(startLocalDate);
                endSp = new StatisticsMonth(endLocalDate);
                break;
            default:
                throw new IllegalArgumentException("Invalid choice: " + choice);
        }
        
        while (startSp.isBeforeOrInTheSamePeriodAs(endSp)) {
            formattedLocalDateList.add(startSp.formatted());
            startSp.advance();
        }
        return formattedLocalDateList;
    }
}
