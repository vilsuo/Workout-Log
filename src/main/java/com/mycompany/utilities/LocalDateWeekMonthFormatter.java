
package com.mycompany.utilities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LocalDateWeekMonthFormatter {
    
    private String choice;
    
    public static final String patternWeek;
    public static final String patternMonth;

    public LocalDateWeekMonthFormatter(String choice) {
        this.choice = choice;
    }
    
    private String formatLocalDateBasedOnChoice(LocalDate localDate) {
        if (choice.equals("Week")) {
            
        } else if (choice.equals("Month")) {
            
        }
        return localDate.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    private List<String> getFormattedLocalDatesBetween(LocalDate startLocalDate, LocalDate endLocalDate) {
        List<String> formattedLocalDateList = new ArrayList<>();
        
        if (choice.equals("Week")) {
            
            int i = 0;
            while (startLocalDate.plusWeeks(i).isBefore(endLocalDate)) {
                formattedLocalDateList.add(
                    startLocalDate.plusWeeks(i).format(
                        DateTimeFormatter.ofPattern(pattern)
                    )
                );
                ++i;
            }
        } else if (choice.equals("Month")) {
            int i = 0;
            while (startLocalDate.plusWeeks(i).isBefore(endLocalDate)) {
                formattedLocalDateList.add(
                    startLocalDate.plusMonths(i).format(
                        DateTimeFormatter.ofPattern(pattern)
                    )
                );
                ++i;
            }
        }
    }
}
