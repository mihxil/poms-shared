/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.RangeMatcher;
import nl.vpro.domain.media.ScheduleEvent;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author rico
 */
@XmlType(name = "scheduleEventSearchType", propOrder = {"begin", "end", "channel", "net", "rerun"})
public class ScheduleEventSearch extends RangeMatcher<Instant> implements Predicate<ScheduleEvent> {

    @XmlElement
    @Getter
    @Setter
    private String channel;

    @XmlElement
    @Getter
    @Setter
    private String net;

    @XmlElement
    @Getter
    @Setter
    private Boolean rerun;

    @XmlElement
    @Getter
    @Setter
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant begin;

    @XmlElement
    @Getter
    @Setter
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant end;

    public ScheduleEventSearch() {
    }


    public ScheduleEventSearch(String channel, Instant begin, Instant end) {
        super(begin, end, true);
        this.channel = channel;
    }

    public ScheduleEventSearch(String channel, Instant begin, Instant end, Boolean rerun) {
        super(begin, end, true);
        this.channel = channel;
        this.rerun = rerun;
    }

    public ScheduleEventSearch(String channel, String net, Instant begin, Instant end, Boolean rerun) {
        super(begin, end, true);
        this.channel = channel;
        this.net = net;
        this.rerun = rerun;
    }



    public boolean hasSearches() {
        return channel != null || net != null || rerun != null;
    }

    @Override
    public boolean test(@Nullable ScheduleEvent t) {
        return t != null && (channel == null || channel.equals(t.getChannel().name()))
            && (net == null || net.equals(t.getNet().getId()))
            && (rerun == null || rerun == t.getRepeat().isRerun())
            && super.testComparable(t.getStartInstant());
    }


    @Override
    public String toString() {
        return "ScheduleEventMatcher{channel=" + channel + ", net=" + net + ", begin=" + begin + ", end=" + end + ", rerun=" + rerun + "}";
    }
}
