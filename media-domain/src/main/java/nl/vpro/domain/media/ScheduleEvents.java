/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.Optional;

/**
 * @author Roelof Jan Koekoek
 * @since 1.7
 */
public class ScheduleEvents {

    /**
     * Equals to events on their real start time. Returns true when:
     * <p/>
     * channel = channel AND (start + offset) = (start + offset)
     */
    public static boolean equalHonoringOffset(ScheduleEvent event1, ScheduleEvent event2) {
        return
            event1.getChannel() != null && event1.getChannel().equals(event2.getChannel()) &&
                event1.getRealStartInstant() != null && event2.getRealStartInstant() != null && event1.getRealStartInstant().toEpochMilli() == event2.getRealStartInstant().toEpochMilli();
    }

    /**
     * Compares two events allowing for a certain margin in their real start time. This is sometimes usefull when
     * comparing events from a source that uses guide times rounded to the minute for example
     *
     * @return true wen equal
     */
    public static boolean differWithinMargin(ScheduleEvent event1, ScheduleEvent event2, long marginInMillis) {
        return
            event1.getChannel() != null && event1.getChannel().equals(event2.getChannel()) &&
                event1.getRealStartInstant() != null && event2.getRealStartInstant() != null && Math.abs(event1.getRealStartInstant().toEpochMilli() - event2.getRealStartInstant().toEpochMilli()) <= marginInMillis;
    }

    public static String userFriendlyToString(Iterable<ScheduleEvent> scheduleEvents) {
        StringBuilder builder = new StringBuilder();
        for (ScheduleEvent event : scheduleEvents) {
            if (builder.length() > 0) {
                builder.append("; ");
            }
            builder.append(event.getChannel()).append(", ").append(event.getStartInstant().toString());

        }
        return builder.toString();
    }

    public static String userFriendlyToString(MediaObject object) {
        return userFriendlyToString(object.getScheduleEvents());
    }


    public static boolean isRerun(ScheduleEvent scheduleEevent) {
        return Repeat.isRerun(scheduleEevent.getRepeat());
    }
    public static boolean isOriginal(ScheduleEvent scheduleEevent) {
        return Repeat.isOriginal(scheduleEevent.getRepeat());
    }

    public static Optional<ScheduleEvent> sortDateEventForProgram(Iterable<ScheduleEvent> scheduleEvents) {
        ScheduleEvent result = null;
        if (scheduleEvents != null) {
            for (ScheduleEvent s : scheduleEvents) {
                if (ScheduleEvents.isOriginal(s) && (result == null || s.getStartInstant().isAfter(result.getStartInstant()))) {
                    result = s;
                }
            }
        }
        return Optional.ofNullable(result);
    }

    public static Optional<ScheduleEvent> getFirstScheduleEvent(Iterable<ScheduleEvent> scheduleEvents, boolean ignoreReruns) {
        if (scheduleEvents != null) {
            for (ScheduleEvent s : scheduleEvents) {
                if (! ignoreReruns || ScheduleEvents.isOriginal(s)) {
                    return Optional.of(s);
                }
            }
        }
        return Optional.empty();

    }

    public static Optional<ScheduleEvent> getLastScheduleEvent(Iterable<ScheduleEvent> scheduleEvents, boolean ignoreReruns) {
        ScheduleEvent result = null;
        if (scheduleEvents != null) {
            for (ScheduleEvent s : scheduleEvents) {
                if (!ignoreReruns || ScheduleEvents.isOriginal(s)) {
                    result = s;
                }
            }
        }
        return Optional.ofNullable(result);
    }

}
