/**
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;

import javax.xml.bind.JAXB;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduleTest {

    @Test
    public void testUnmarshalForMISDuplicatesOnClearScheduleEvents() throws Exception {
        MediaTable table = new MediaTable();

        Program program = MediaBuilder.program().crids("crid://domain.com/12345").build();
        table.addProgram(program);

        // Need three to trigger ConcurrentModificationException
        ScheduleEvent event1 = new ScheduleEvent(Channel.CONS, new Date(0), new Date(10));
        event1.setUrnRef(program.getCrids().get(0));
        event1.setPoProgID("id1");
        ScheduleEvent event2 = new ScheduleEvent(Channel.CONS, new Date(10), new Date(10));
        event2.setUrnRef(program.getCrids().get(0));
        event2.setPoProgID("id2");
        ScheduleEvent event3 = new ScheduleEvent(Channel.CONS, new Date(20), new Date(10));
        event2.setUrnRef(program.getCrids().get(0));
        event2.setPoProgID("id2");

        Schedule schedule = new Schedule(Channel.CONS, new Date(), Arrays.asList(event1, event2, event3));
        table.setSchedule(schedule);

        final StringWriter writer = new StringWriter();
        JAXB.marshal(table, writer);
        final MediaTable result = JAXB.unmarshal(new StringReader(writer.toString()), MediaTable.class);

        assertThat(result.getProgramTable()).hasSize(2);
        assertThat(result.getProgramTable().get(0).getMid()).isEqualTo("id1");
        assertThat(result.getProgramTable().get(0).getScheduleEvents()).hasSize(1);
        assertThat(result.getProgramTable().get(0).getCrids()).hasSize(1);
        assertThat(result.getProgramTable().get(1).getMid()).isEqualTo("id2");
        assertThat(result.getProgramTable().get(1).getScheduleEvents()).hasSize(1);
        assertThat(result.getProgramTable().get(1).getCrids()).isEmpty();
    }
}
