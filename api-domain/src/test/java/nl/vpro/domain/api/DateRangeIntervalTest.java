package nl.vpro.domain.api;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import nl.vpro.domain.media.Schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateRangeIntervalTest {
    
    private String[][] exampleIntervals = {
        {"2 YEAR", "2016","P46Y"},
        {"3 YEAR", "2016", "P46Y"},
        {"3 MONTH", "2016-05", "P556M" }, 
        {"3 WEEK", "2016-W18", "P2418W"}, 
        {"6 DAY", "2016-05-06", "P16927D"}, 
        {"5 HOUR", "2016-05-06T14:21:00+02:00", "PT406260H"}, 
        {"6 MINUTE", "2016-05-06T14:21:00+02:00", "PT24375621M"}
    };
    
    @Test(expected = IllegalArgumentException.class)
    public void testIllegal() {
        new DateRangeInterval("foo").parsed();
    }
    
    @Test
    public void testParse() {
        Date date = Date.from(LocalDate.of(2016, 5, 6).atTime(14, 21).atZone(Schedule.ZONE_ID).toInstant());
        for (String[] i : exampleIntervals) {
            DateRangeInterval interval = new DateRangeInterval(i[0]);
            String printAsTime = interval.parsed().print(date.toInstant(), false);
            String printAsDuration = interval.parsed().print(date.toInstant(), true);
            boolean begin = interval.parsed().isBucketBegin(date.toInstant());
            System.out.println(i[0] + ":" + printAsTime + ":" + printAsDuration + ":" + begin);
            assertThat(printAsTime).isEqualTo(i[1]);
            assertThat(printAsDuration).isEqualTo(i[2]);

        }
    }

    @Test
    public void testMatchesWeek() {
        DateRangeInterval interval = new DateRangeInterval("2WEEK");

        ZonedDateTime begin = ZonedDateTime.parse("2015-01-02T00:00:00Z")
                .truncatedTo(ChronoUnit.DAYS)
                .with(TemporalAdjusters.next(DayOfWeek.THURSDAY))
                .with(ChronoField.ALIGNED_WEEK_OF_YEAR, 10 % 2 + 2);


        ZonedDateTime end = begin.plusWeeks(2);
        System.out.println(begin + "-" + end);
        assertTrue(interval.matches(Date.from(begin.toInstant()), Date.from(end.toInstant())));
    }
    

    @Test
    @Ignore
    public void testNoMatchesWeek() {
        DateRangeInterval interval = new DateRangeInterval("2WEEK");


        ZonedDateTime begin = ZonedDateTime.parse("2015-01-02T00:00:00Z")
            .truncatedTo(ChronoUnit.DAYS)
            .with(TemporalAdjusters.next(DayOfWeek.THURSDAY))
            .with(ChronoField.ALIGNED_WEEK_OF_YEAR, 10 % 2 + 1);

        ZonedDateTime end = begin.plusWeeks(2);
        
        System.out.println(begin + "-" + end);
        assertFalse(interval.matches(Date.from(begin.toInstant()), Date.from(end.toInstant())));
        
    }


    @Test
    public void testMatchesYear() {
        DateRangeInterval interval = new DateRangeInterval("YEAR");

        ZonedDateTime begin = ZonedDateTime.parse("2015-01-02T00:00:00Z")
            .truncatedTo(ChronoUnit.DAYS)
            .with(ChronoField.DAY_OF_YEAR, 1);
        ZonedDateTime end = begin.plusYears(1);
        assertTrue(interval.matches(Date.from(begin.toInstant()), Date.from(end.toInstant())));

    }

    @Test
    public void testNoMatchesYear() {
        DateRangeInterval interval = new DateRangeInterval("YEAR");

        ZonedDateTime begin = ZonedDateTime.parse("2015-01-02T00:00:00Z")
            .truncatedTo(ChronoUnit.DAYS)
            .with(ChronoField.DAY_OF_YEAR, 1);
        ZonedDateTime end = begin.plusYears(2);
        assertFalse(interval.matches(Date.from(begin.toInstant()), Date.from(end.toInstant())));

    }
}
