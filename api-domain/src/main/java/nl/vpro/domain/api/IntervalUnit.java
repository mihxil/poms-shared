package nl.vpro.domain.api;

import lombok.Getter;

import java.time.temporal.ChronoField;

/**
* @author Michiel Meeuwissen
* @since 5.5
*/
public enum IntervalUnit {

    YEAR(ChronoField.YEAR, "yyyy"),
    MONTH(ChronoField.MONTH_OF_YEAR, "yyyy-MM"),
    WEEK(ChronoField.ALIGNED_WEEK_OF_YEAR, "xxxx-'W'w"),
    DAY(ChronoField.DAY_OF_YEAR, "yyyy-MM-dd"),
    HOUR(ChronoField.HOUR_OF_DAY, "yyyy-MM-dd'T'HH:mm:ss'Z'"),
    MINUTE(ChronoField.MINUTE_OF_DAY, "yyyy-MM-dd'T'HH:mm:ss'Z'")
    ;

    @Getter
    private final ChronoField chronoField;


    /**
     * The format string to format an datetime value . This is used to communicate to ES.
     * TODO: Since ES uses joda-time syntax this is joda-time  (only relevant for WEEK). I'd say we don't want joda-time but javax.time, so I think xxxx should be YYYY?
     *
     */
    @Getter
    private final String format;

    IntervalUnit(ChronoField chronoField, String format) {
        this.chronoField = chronoField;
        this.format = format;
    }
}
