package nl.vpro.domain.media.search;

import java.util.Date;

import org.junit.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class DateRangeTest {

    @Test
    public void testTest1() {
        DateRange range = new DateRange(new Date(100), new Date(200));
        assertThat(range.test(new Date(99))).isFalse();
        assertThat(range.test(new Date(100))).isTrue();
        assertThat(range.test(new Date(150))).isTrue();
        assertThat(range.test(new Date(200))).isFalse();
        assertThat(range.test(new Date(201))).isFalse();

    }

    @Test
    public void testTest2() {
        DateRange range = new DateRange(null, new Date(200));
        assertThat(range.test(new Date(99))).isTrue();
        assertThat(range.test(new Date(100))).isTrue();
        assertThat(range.test(new Date(150))).isTrue();
        assertThat(range.test(new Date(200))).isFalse();
        assertThat(range.test(new Date(201))).isFalse();

    }

    @Test
    public void testTest3() {
        DateRange range = new DateRange(new Date(100), null);
        assertThat(range.test(new Date(99))).isFalse();
        assertThat(range.test(new Date(100))).isTrue();
        assertThat(range.test(new Date(150))).isTrue();
        assertThat(range.test(new Date(200))).isTrue();
        assertThat(range.test(new Date(201))).isTrue();

    }


    @Test
    public void json() throws Exception {
        Jackson2TestUtil.roundTripAndSimilar(new DateRange(new Date(100), new Date(200)), "{\n" +
            "  \"start\" : 100,\n" +
            "  \"stop\" : 200\n" +
            "}");


    }


    @Test
    public void xml() throws Exception {
        JAXBTestUtil.roundTripAndSimilar(new DateRange(new Date(100), new Date(200)), "<local:dateRange xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:s=\"urn:vpro:media:search:2012\" xmlns:update=\"urn:vpro:media:update:2009\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:local=\"uri:local\">\n" +
            "    <s:start>1970-01-01T01:00:00.100+01:00</s:start>\n" +
            "    <s:stop>1970-01-01T01:00:00.200+01:00</s:stop>\n" +
            "</local:dateRange>");

    }

}
