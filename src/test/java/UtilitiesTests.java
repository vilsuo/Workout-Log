
import com.mycompany.utilities.Statistics.StatisticsPeriod;
import com.mycompany.utilities.Statistics.StatisticsWeek;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


public class UtilitiesTests {
    
    //private final String CHOICE_WEEK = "Week";
    //private final String CHOICE_MONTH = "Month";
    
    @Test
    public void StatisticMonthIsBeforeOrEqualWeekTest() {
        LocalDate ld1 = LocalDate.of(2023, 1, 2);
        StatisticsPeriod sw1 = new StatisticsWeek(ld1);
        
        assertTrue(!sw1.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2023, 1, 1))));
        assertTrue(sw1.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(ld1)));
        assertTrue(sw1.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2023, 1, 3))));
        
        
        LocalDate ld2 = LocalDate.of(2023, 1, 1);
        StatisticsPeriod sw2 = new StatisticsWeek(ld2);
        
        assertTrue(!sw2.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2022, 12, 31))));
        assertTrue(sw2.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(ld2)));
        assertTrue(sw2.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2023, 1, 2))));
        
        
        LocalDate ld3 = LocalDate.of(2021, 1, 2);
        StatisticsPeriod sw3 = new StatisticsWeek(ld3);
        
        assertTrue(sw3.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2021, 1, 1))));
        assertTrue(sw3.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(ld3)));
        assertTrue(sw3.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2021, 1, 3))));
        assertTrue(sw3.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2021, 12, 31))));
        
        LocalDate ld4 = LocalDate.of(2021, 12, 30);
        StatisticsPeriod sw4 = new StatisticsWeek(ld4);
        
        assertTrue(sw4.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2021, 12, 31))));
        assertTrue(sw4.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(ld4)));
        assertTrue(sw4.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2021, 12, 29))));
        assertTrue(sw4.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2021, 12, 27))));
        
        assertTrue(!sw4.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2021, 1, 1))));
        assertTrue(!sw4.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2021, 1, 3))));
        assertTrue(!sw4.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2021, 1, 4))));
        assertTrue(!sw4.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2021, 6, 6))));
        assertTrue(!sw4.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2021, 11, 30))));
        assertTrue(!sw4.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2021, 12, 1))));
        assertTrue(!sw4.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2021, 12, 26))));
        
        
        LocalDate ld5 = LocalDate.of(2017, 6, 1);
        StatisticsPeriod sw5 = new StatisticsWeek(ld5);
        
        assertTrue(sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2017, 5, 31))));
        assertTrue(sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(ld4)));
        assertTrue(sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2017, 6, 2))));
        
        assertTrue(!sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2016, 2, 5))));
        assertTrue(!sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2016, 9, 12))));
        assertTrue(sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2018, 5, 2))));
        assertTrue(sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2018, 7, 1))));
        
        assertTrue(sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2017, 5, 29))));
        assertTrue(!sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2017, 5, 28))));
        assertTrue(!sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2017, 5, 1))));
        assertTrue(!sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2017, 4, 1))));
        assertTrue(!sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2017, 1, 1))));
        assertTrue(!sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2017, 1, 2))));
        
        assertTrue(sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2017, 6, 10))));
        assertTrue(sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2017, 7, 1))));
        assertTrue(sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2017, 12, 30))));
        assertTrue(sw5.isBeforeOrInTheSamePeriodAs(new StatisticsWeek(LocalDate.of(2017, 12, 31))));
    }
    
    /*
    @Test
    public void StatisticWeekFormattedTest() {
        assertTrue(!(new StatisticsWeek(LocalDate.of(2023, 1, 1))).formatted().equals("1/2023")); // formatting uses week of week based year (this is 52/2023
        assertTrue((new StatisticsWeek(LocalDate.of(2023, 1, 2))).formatted().equals("1/2023"));
        
        assertTrue((new StatisticsWeek(LocalDate.of(2023, 1, 8))).formatted().equals("1/2023"));
        assertTrue((new StatisticsWeek(LocalDate.of(2023, 1, 9))).formatted().equals("2/2023"));
        
        assertTrue((new StatisticsWeek(LocalDate.of(2023, 1, 31))).formatted().equals("5/2023"));
        assertTrue((new StatisticsWeek(LocalDate.of(2023, 2, 1))).formatted().equals("5/2023"));
    }
    
    @Test
    public void StatisticMonthFormattedTest() {
        assertTrue((new StatisticsMonth(LocalDate.of(2023, 1, 1))).formatted().equals("1/2023"));
        assertTrue((new StatisticsMonth(LocalDate.of(2023, 1, 31))).formatted().equals("1/2023"));
        assertTrue((new StatisticsMonth(LocalDate.of(2023, 2, 1))).formatted().equals("2/2023"));
        assertTrue((new StatisticsMonth(LocalDate.of(2023, 2, 2))).formatted().equals("2/2023"));
    }
    
    @Test
    public void StatisticWeekAdvanceTest() {
        LocalDate ld1 = LocalDate.of(2023, 1, 1);
        StatisticsPeriod sw1 = new StatisticsWeek(ld1);
        
        String beforeAdvance1 = sw1.formatted();
        sw1.advance();
        
        assertTrue(!sw1.formatted().equals(beforeAdvance1));
        assertTrue(sw1.formatted().equals("1/2023"));
        
        
        LocalDate ld2 = LocalDate.of(2023, 1, 2);
        StatisticsPeriod sw2 = new StatisticsWeek(ld2);
        
        String beforeAdvance2 = sw2.formatted();
        sw2.advance();
        
        assertTrue(!sw2.formatted().equals(beforeAdvance2));
        assertTrue(sw2.formatted().equals("2/2023"));
    }
    
    @Test
    public void getFormattedLocalDatesBetweenWeekTest() {
        LocalDate startLd1 = LocalDate.of(2023, 1, 1);
        LocalDate endLd1 = LocalDate.of(2023, 2, 5);
        
        List<String> formattedDateList1 = CustomLocalDateFormatter
            .getFormattedLocalDatesBetween(startLd1, endLd1, CHOICE_WEEK);
        
        List<String> trueFormattedDateList1 = Arrays.asList(
            new String[] {
                "52/2022",
                "1/2023",
                "2/2023",
                "3/2023",
                "4/2023",
                "5/2023"
            }
        );
        
        assertTrue(compareLists(formattedDateList1, trueFormattedDateList1));
        
        ////System.out.println("size formatted: " + formattedDateList.size() + ", size true: " + trueFormattedDateList.size());
        //assertTrue(formattedDateList1.size() == trueFormattedDateList1.size());
        //for (int i = 0; i < formattedDateList1.size(); ++i) {
        //    //System.out.println("value formatted: " + formattedDateList.get(i) + ", value true: " + trueFormattedDateList.get(i));
        //    assertTrue(
        //        formattedDateList1.get(i).equals(trueFormattedDateList1.get(i))
        //    );
        //}
        
        LocalDate startLd2 = LocalDate.of(2023, 1, 8);
        LocalDate endLd2 = LocalDate.of(2023, 1, 9);
        
        List<String> formattedDateList2 = CustomLocalDateFormatter
            .getFormattedLocalDatesBetween(startLd2, endLd2, CHOICE_WEEK);
        
        List<String> trueFormattedDateList2 = Arrays.asList(
            new String[] {
                "1/2023",
                "2/2023"
            }
        );
        
        assertTrue(compareLists(formattedDateList2, trueFormattedDateList2));
    }
    
    @Test
    public void getFormattedLocalDatesBetweenMonthTest() {
        LocalDate startLd1 = LocalDate.of(2023, 1, 1);
        LocalDate endLd1 = LocalDate.of(2023, 2, 5);
        
        List<String> formattedDateList1 = CustomLocalDateFormatter
            .getFormattedLocalDatesBetween(startLd1, endLd1, CHOICE_MONTH);
        
        List<String> trueFormattedDateList1 = Arrays.asList(
            new String[] {
                "1/2023",
                "2/2023"
            }
        );
        
        assertTrue(compareLists(formattedDateList1, trueFormattedDateList1));
        
        
        LocalDate startLd2 = LocalDate.of(2023, 2, 11);
        LocalDate endLd2 = LocalDate.of(2023, 5, 17);
        
        List<String> formattedDateList2 = CustomLocalDateFormatter
            .getFormattedLocalDatesBetween(startLd2, endLd2, CHOICE_MONTH);
        
        List<String> trueFormattedDateList2 = Arrays.asList(
            new String[] {
                "2/2023",
                "3/2023",
                "4/2023",
                "5/2023"
            }
        );
        
        assertTrue(compareLists(formattedDateList2, trueFormattedDateList2));
    }
    
    private boolean compareLists(List<String> list1, List<String> list2) {
        if (list1.size() != list2.size()) {
            System.out.println("size 1:" + list1.size() + ", size 2: " + list2.size());
            list1.stream().forEach(
                value -> System.out.println(value)
            );
            return false;
        }
        for (int i = 0; i < list1.size(); ++i) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }
        return true;
    }
    */
}
