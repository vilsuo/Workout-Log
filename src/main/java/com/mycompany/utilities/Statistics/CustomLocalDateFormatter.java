
package com.mycompany.utilities.Statistics;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class CustomLocalDateFormatter {
    
    public static String formatLocalDate(LocalDate localDate, String choice) {
        if (choice.equals("Week")) {
            
            return new StatisticsWeek(localDate).formatted();
            
        } else if (choice.equals("Month")) {
            return new StatisticsMonth(localDate).formatted();
        }
        return null;
    }
    
    public static List<String> getFormattedLocalDatesBetween(LocalDate startLocalDate, LocalDate endLocalDate, String choice) {
        List<String> formattedLocalDateList = new ArrayList<>();
        
        StatisticsPeriod sp;
        switch (choice) {
            case "Week":
                sp = new StatisticsWeek(startLocalDate);
                break;
            case "Month":
                sp = new StatisticsMonth(startLocalDate);
                break;
            default:
                return null;
        }
        
        while (sp.isBeforeOrEqual(endLocalDate)) {
            formattedLocalDateList.add(sp.formatted());
            sp.advance();
        }
        return formattedLocalDateList;
    }
}
