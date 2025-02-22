package nl.vpro.domain.media;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Range;

import nl.vpro.domain.Child;
import nl.vpro.domain.Identifiable;
import nl.vpro.domain.TextualObject;
import nl.vpro.domain.media.bind.NetToString;
import nl.vpro.domain.media.support.*;
import nl.vpro.jackson2.DurationToJsonTimestamp;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.jackson2.StringZonedLocalDateToJsonTimestamp;
import nl.vpro.jackson2.Views;
import nl.vpro.persistence.LocalDateToDateConverter;
import nl.vpro.util.DateUtils;
import nl.vpro.util.TriFunction;
import nl.vpro.xml.bind.DurationXmlAdapter;
import nl.vpro.xml.bind.InstantXmlAdapter;
import nl.vpro.xml.bind.ZonedLocalDateXmlAdapter;

import static javax.persistence.CascadeType.ALL;
import static nl.vpro.domain.TextualObjects.sorted;

@Entity
@IdClass(ScheduleEventIdentifier.class)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@FilterDefs({
    @FilterDef(name = MediaObject.DELETED_FILTER),
})
@Filters({
    @Filter(name = MediaObject.DELETED_FILTER, condition = "(select m.workflow from mediaobject m where m.id = mediaobject_id and m.mergedTo_id is null) NOT IN ('MERGED', 'DELETED')")
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "scheduleEventType", propOrder = {
    "titles",
    "descriptions",
    "repeat",
    "memberOf",
    "avAttributes",
    "textSubtitles",
    "textPage",
    "guideDate",
    "startInstant",
    "offset",
    "duration",
    "poProgID",
    "poSeriesIDLegacy",
    "primaryLifestyle",
    "secondaryLifestyle"
})
@JsonPropertyOrder({
    "titles",
    "descriptions",
    "channel",
    "startInstant",
    "guideDate",
    "duration",
    "midRef",
    "poProgID",
    "repeat",
    "memberOf",
    "avAttributes",
    "textSubtitles",
    "textPage",
    "offset",
    "poSeriesIDLegacy",
    "primaryLifestyle",
    "secondaryLifestyle"
})
@SuppressWarnings({"serial", "NullableProblems", "ConstantConditions"})
public class ScheduleEvent implements Serializable, Identifiable<ScheduleEventIdentifier>,
    Comparable<ScheduleEvent>,
    TextualObject<ScheduleEventTitle, ScheduleEventDescription, ScheduleEvent>,
    Child<MediaObject> {

    @Id
    @Enumerated(EnumType.STRING)
    @NotNull
    protected Channel channel;

    @Id
    @NotNull
    protected Instant start;

    @ManyToOne
    @Valid
    protected Net net;

    @Column(nullable = false, name = "guideDay", columnDefinition="Date")
    @Convert(converter = LocalDateToDateConverter.class)
    protected LocalDate guideDay;

    @Embedded
    protected Repeat repeat;

    protected String memberOf;

    @OneToOne(orphanRemoval = true, cascade = ALL)
    protected AVAttributes avAttributes;

    protected String textSubtitles;

    protected String textPage;

    @Column(name = "start_offset")
    @JsonSerialize(using = DurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = DurationToJsonTimestamp.Deserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Duration offset;

    @JsonSerialize(using = DurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = DurationToJsonTimestamp.Deserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Duration duration;

    protected String imi;

    @Transient
    protected String urnRef;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JsonBackReference
    protected MediaObject mediaObject;

    @Enumerated(EnumType.STRING)
    protected ScheduleEventType type;

    @Embedded
    @Column(name = "primary")
    @XmlElement
    protected Lifestyle primaryLifestyle;

    @Embedded
    @Column(name = "secondary")
    @XmlElement
    protected SecondaryLifestyle secondaryLifestyle;

    @Transient
    protected String midRef;

    protected String poSeriesID;

    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = ALL)
    @Valid
    @XmlElement(name = "title")
    @JsonProperty("titles")
    protected Set<ScheduleEventTitle> titles = new TreeSet<>();


    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = ALL)
    @Valid
    @XmlElement(name = "description")
    @JsonProperty("descriptions")
    protected Set<ScheduleEventDescription> descriptions = new TreeSet<>();

    public ScheduleEvent() {
    }

    public ScheduleEvent(Channel channel, Instant start, Duration duration) {
        this(channel, null, guideLocalDate(start), start, duration, null);
    }

    public ScheduleEvent(Channel channel, Net net, Instant start, Duration duration) {
        this(channel, net, guideLocalDate(start), start, duration, null);
    }

    public ScheduleEvent(Channel channel, LocalDate guideDay, Instant start, Duration duration) {
        this(channel, null, guideDay, start, duration, null);
    }

    public ScheduleEvent(Channel channel, Net net, LocalDate guideDay, Instant start, Duration duration) {
        this(channel, net, guideDay, start, duration, null);
    }

    public ScheduleEvent(Channel channel, Instant start, Duration duration, MediaObject media) {
        this(channel, null, guideLocalDate(start), start, duration, media);
    }

    public ScheduleEvent(Channel channel, Net net, Instant start, Duration duration, MediaObject media) {
        this(channel, net, guideLocalDate(start), start, duration, media);
    }

    public ScheduleEvent(
        @Nonnull  Channel channel,
        @Nullable Net net,
        @Nullable LocalDate guideDay,
        @Nonnull  Instant start,
        @Nonnull  Duration duration,
        @Nullable MediaObject media) {
        this(channel, net, guideDay, start, duration, media, null);
    }

    @lombok.Builder(builderClassName = "Builder")
    private ScheduleEvent(
        @Nonnull Channel channel,
        @Null  Net net,
        @Nullable  LocalDate guideDay,
        @Nonnull  Instant start,
        @Nonnull  Duration duration,
        @Nullable MediaObject media,
        @Nullable  Repeat repeat) {
        this.channel = channel;
        this.net = net;
        this.guideDay = guideDay == null ? guideLocalDate(start) : guideDay;
        this.start = start;
        this.duration = duration;
        this.repeat = Repeat.nullIfDefault(repeat);
        setParent(media);
    }


    /**
     * @deprecated use constructor with types from java.time
     */
    @Deprecated
    public ScheduleEvent(Channel channel, Date start, Date duration) {
        this(channel, null, guideDay(start), start, duration, null);
    }

    /**
     * @deprecated use constructor with types from java.time
     */
    @Deprecated
    public ScheduleEvent(Channel channel, Net net, Date start, Date duration) {
        this(channel, net, guideDay(start), start, duration, null);
    }

    /**
     * @deprecated use constructor with types from java.time
     */
    @Deprecated
    public ScheduleEvent(Channel channel, Date guideDay, Date start, Date duration) {
        this(channel, null, guideDay, start, duration, null);
    }

    /**
     * @deprecated use constructor with types from java.time
     */
    @Deprecated
    public ScheduleEvent(Channel channel, Net net, Date guideDay, Date start, Date duration) {
        this(channel, net, guideDay, start, duration, null);
    }

    /**
     * @deprecated use constructor with types from java.time
     */
    @Deprecated
    public ScheduleEvent(Channel channel, Date start, Date duration, MediaObject media) {
        this(channel, null, guideDay(start), start, duration, media);
    }

    /**
     * @deprecated use constructor with types from java.time
     */
    @Deprecated
    public ScheduleEvent(Channel channel, Net net, Date start, Date duration, MediaObject media) {
        this(channel, net, guideDay(start), start, duration, media);
    }

    /**
     * @deprecated use constructor with types from java.time
     */
    @Deprecated
    public ScheduleEvent(Channel channel, Net net, Date guideDay, Date start, Date duration, MediaObject media) {
        this(channel, net, guideLocalDate(guideDay), instant(start), duration(duration), media);
    }


    public ScheduleEvent(ScheduleEvent source) {
        this(source, source.mediaObject);
    }

    public ScheduleEvent(ScheduleEvent source, MediaObject parent) {
        this.channel = source.channel;
        this.net = source.net;
        this.start = source.start;
        this.guideDay = source.guideDay;
        this.repeat = Repeat.copy(source.repeat);
        this.memberOf = source.memberOf;
        this.avAttributes = AVAttributes.copy(source.avAttributes);
        this.textSubtitles = source.textSubtitles;
        this.textPage = source.textPage;
        this.offset = source.offset;
        this.duration = source.duration;
        this.imi = source.imi;
        this.urnRef = source.urnRef;
        this.type = source.type;
        this.primaryLifestyle = Lifestyle.copy(source.primaryLifestyle);
        this.secondaryLifestyle = SecondaryLifestyle.copy(source.secondaryLifestyle);
        this.midRef = source.midRef;
        this.poSeriesID = source.poSeriesID;

        this.mediaObject = parent;
    }

    public static ScheduleEvent copy(ScheduleEvent source) {
        if (source == null) {
            return null;
        }
        return copy(source, source.mediaObject);
    }

    public static ScheduleEvent copy(ScheduleEvent source, MediaObject parent) {
        if (source == null) {
            return null;
        }

        return new ScheduleEvent(source, parent);
    }

    private static Date guideDay(Date start) {
        if (start == null) {
            return null;
        }
        return Date.from(guideLocalDate(start).atTime(Schedule.START_OF_SCHEDULE).atZone(Schedule.ZONE_ID).toInstant());
    }

    private static LocalDate guideLocalDate(Date start) {
        if (start == null) {
            return null;
        }
        return guideLocalDate(start.toInstant());
    }

    private static LocalDate guideLocalDate(Instant start) {
        return Schedule.guideDay(start);
    }

    private static Duration duration(Date duration) {
        if (duration == null) {
            return null;
        }
        return Duration.ofMillis(duration.getTime());
    }

    private static Instant instant(Date instant) {
        if (instant == null) {
            return null;
        }
        return instant.toInstant();
    }

    @XmlElement
    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(Repeat value) {
        this.repeat = value;
    }

    @XmlElement
    public String getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(String value) {
        this.memberOf = value;
    }

    /**
     * I think in principle some av-attributes (like the aspect ratio) may vary for different schedule events.
     */
    @XmlElement
    public AVAttributes getAvAttributes() {
        return avAttributes;
    }

    public void setAvAttributes(AVAttributes value) {
        this.avAttributes = value;
    }

    @XmlElement
    public String getTextSubtitles() {
        return textSubtitles;
    }

    public void setTextSubtitles(String value) {
        this.textSubtitles = value;
    }

    @XmlElement
    public String getTextPage() {
        return textPage;
    }

    public void setTextPage(String textPage) {
        this.textPage = textPage;
    }

    /**
     * @deprecated use {@link #getGuideDate}
     */
    @Deprecated
    @XmlTransient
    public Date getGuideDay() {
        LocalDate dateToUse = guideDay == null ? guideLocalDate(start) : guideDay;
        return dateToUse == null ? null : Date.from(dateToUse.atStartOfDay(Schedule.ZONE_ID).toInstant());
    }


    /**
     * @deprecated A full date object is not stored in the database nor in xml. Use {@link #setGuideDate}
     */
    @Deprecated
    public void setGuideDay(Date guideDay) {
        this.guideDay = guideDay == null ? null : guideDay.toInstant().atZone(Schedule.ZONE_ID).toLocalDate();
    }



    @XmlElement(name = "guideDay")
    @XmlJavaTypeAdapter(ZonedLocalDateXmlAdapter.class)
    @XmlSchemaType(name = "date")
    @JsonDeserialize(using = StringZonedLocalDateToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringZonedLocalDateToJsonTimestamp.Serializer.class)
    public LocalDate getGuideDate() {
        return guideDay;
    }

    public void setGuideDate(LocalDate guideDate) {
        this.guideDay = guideDate;
    }

    @XmlTransient
    @Deprecated
    public Date getStart() {
        return DateUtils.toDate(start);
    }


    @Deprecated
    public void setStart(Date start) {
        // if (this.start != null) throw new IllegalStateException(); Used in test cases.
        this.start = DateUtils.toInstant(start);
    }
    @XmlElement(name = "start")
    @XmlSchemaType(name = "dateTime")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getStartInstant() {
        return start;
    }
    public void setStartInstant(Instant start) {
        this.start = start;
    }


    @JsonView({Views.Publisher.class})
    // Because of other 'start' fields (e.g. in segment, it is mapped to _long_). This field is mapped to date in ES. In ES fields with same name must have same mapping.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "eventStart")
    protected Instant getEventStart() {
        return getStartInstant();
    }

    public Instant getStopInstant() {
        return start.plus(getDuration());
    }
     public void setStopInstant(Instant stop) {
        this.duration = Duration.between(start, stop);
     }

    @XmlTransient
    @Deprecated
    public Date getRealStart() {
        if (start == null) {
            return null;
        }

        if (offset == null) {
            return Date.from(start);
        }

        return Date.from(start.plus(offset));
    }


    @XmlTransient
    public Instant getRealStartInstant() {
        if (start == null) {
            return null;
        }

        if (offset == null) {
            return start;
        }

        return start.plus(offset);
    }

    @XmlElement
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonIgnore
    public Duration getOffset() {
        return offset;
    }

    public void setOffset(Duration offset) {
        this.offset = offset;
    }


    /**
     * @since 4.3
     */
    @XmlElement(name = "duration")
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonIgnore
    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration value) {
        this.duration = value;
    }

    @XmlAttribute
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        if (this.channel != null && channel != null && channel != this.channel) {
            throw new IllegalStateException();
        }
        this.channel = channel;
    }

    @XmlAttribute
    @JsonSerialize(using = NetToString.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        this.net = net;
    }

    @XmlAttribute
    public String getImi() {
        return imi;
    }

    public void setImi(String value) {
        this.imi = value;
    }

    @XmlAttribute(required = true)
    public String getUrnRef() {
        if (urnRef == null && mediaObject != null) {
            return mediaObject.getUrn();
        }
        return urnRef;
    }

    public void setUrnRef(String value) {
        this.urnRef = value;
    }

    @XmlAttribute(required = true)
    public String getMidRef() {
        if (this.midRef == null && mediaObject != null) {
            return mediaObject.getMid();
        }
        return midRef;
    }

    public void setMidRef(String midRef) {
        this.midRef = midRef;
    }

    @Override
    @XmlTransient
    public MediaObject getParent() {
        return mediaObject;
    }

    @Override
    public void setParent(MediaObject mediaObject) {
        if (this.mediaObject != null) {
            this.mediaObject.removeScheduleEvent(this);
        }
        this.mediaObject = mediaObject;
        if (mediaObject != null) {
            mediaObject.addScheduleEvent(this);
        }
    }

    @XmlTransient
    @Override
    public ScheduleEventIdentifier getId() {
        return new ScheduleEventIdentifier(channel, start);
    }

    @XmlAttribute
    public ScheduleEventType getType() {
        return type;
    }

    public void setType(ScheduleEventType type) {
        this.type = type;
    }

    @XmlElement
    public String getPoProgID() {
        return getMidRef();
    }

    public void setPoProgID(String poProgID) {
        setMidRef(poProgID);
    }

    @XmlTransient
    public String getPoSeriesID() {
        return poSeriesID;
    }

    public void setPoSeriesID(String poSeriesID) {
        this.poSeriesID = poSeriesID;
    }

    @XmlElement(name = "poSeriesID")
    public String getPoSeriesIDLegacy() {
        return null;
    }

    public void setPoSeriesIDLegacy(String poSeriesID) {
        this.poSeriesID = poSeriesID;
    }

    public void clearMediaObject() {
        if (this.mediaObject != null) {
            this.mediaObject.removeScheduleEvent(this);
            this.mediaObject = null;
        }
    }

    public Lifestyle getPrimaryLifestyle() {
        return primaryLifestyle;
    }

    public void setPrimaryLifestyle(Lifestyle primaryLifestyle) {
        this.primaryLifestyle = primaryLifestyle;
    }

    public SecondaryLifestyle getSecondaryLifestyle() {
        return secondaryLifestyle;
    }

    public void setSecondaryLifestyle(SecondaryLifestyle secondaryLifestyle) {
        this.secondaryLifestyle = secondaryLifestyle;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ScheduleEvent");
        sb.append("{channel=").append(channel);
        if (start != null) {
            sb.append(", start=").append(start.atZone(Schedule.ZONE_ID).toLocalDateTime());
        }
        if (mediaObject != null) {
            sb.append(", mediaObject=").append(mediaObject.getMid() == null ? "(no mid)" : mediaObject.getMid()); // it seems that the title may be lazy, so just show mid of media object.
        }
        if (repeat != null && repeat.isRerun) {
            sb.append(", RERUN");
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(ScheduleEvent o) {
        Instant otherStart = o.start;
        if (start != null
            && otherStart != null
            && (!start.equals(otherStart))) {

            return start.compareTo(o.start);
        }

        Channel otherChannel = o.getChannel();
        if (getChannel() != null && otherChannel != null) {
            return getChannel().ordinal() - otherChannel.ordinal();
        } else {
            return hashCode() - o.hashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScheduleEvent that = (ScheduleEvent) o;

        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        } else {
            return System.identityHashCode(this);
        }
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof MediaObject) {
            this.mediaObject = (MediaObject) parent;
        }

        if (guideDay == null && start != null) {
            guideDay = guideLocalDate(start);
        }
    }

    /**
     * The titles associated with the schedule event.
     */
    @Override
    public SortedSet<ScheduleEventTitle> getTitles() {
        return sorted(titles);

    }

    @Override
    public void setTitles(SortedSet<ScheduleEventTitle> titles) {
        this.titles = titles;
    }

    @Override
    public TriFunction<String, OwnerType, TextualType, ScheduleEventTitle> getOwnedTitleCreator() {
        return (value, ownerType, textualType) -> new ScheduleEventTitle(ScheduleEvent.this, value, ownerType, textualType);
    }

    @Override
    public TriFunction<String, OwnerType, TextualType, ScheduleEventDescription> getOwnedDescriptionCreator() {
        return (value, ownerType, textualType) -> new ScheduleEventDescription(ScheduleEvent.this, value, ownerType, textualType);
    }


    @Override
    public ScheduleEvent addTitle(ScheduleEventTitle title) {
        title.setParent(this);
        return TextualObject.super.addTitle(title);
    }

    @Override
    public SortedSet<ScheduleEventDescription> getDescriptions() {
        return sorted(descriptions);
    }


    @Override
    public void setDescriptions(SortedSet<ScheduleEventDescription> descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public ScheduleEvent addDescription(ScheduleEventDescription description) {
        description.setParent(this);
        return TextualObject.super.addDescription(description);
    }

    /**
     * Overriden to help hibernate search (see MediaSearchMappingFactory)
     */
    @Override
    public String getMainTitle() {
        return TextualObject.super.getMainTitle();
    }

    /**
     * Overriden to help hibernate search (see MediaSearchMappingFactory)
     */
    @Override
    public String getSubTitle() {
        return TextualObject.super.getSubTitle();
    }

    /**
     * Overriden to help hibernate search (see MediaSearchMappingFactory)
     */
    @Override
    public String getMainDescription() {
        return TextualObject.super.getMainDescription();
    }


    public Range<Instant> asRange() {
        return Range.closedOpen(start, start.plus(duration));
    }
    public void setRange(Range<Instant> range) {
        this.start = range.lowerEndpoint();
        this.duration = Duration.between(range.lowerEndpoint(), range.upperEndpoint());
    }

    public static class Builder {

        public Builder localStart(int year, int month, int day, int hour, int minute) {
            return localStart(LocalDateTime.of(year, month, day, hour, minute));
        }

        public Builder localStart(LocalDateTime localDateTime) {
            return start(localDateTime.atZone(Schedule.ZONE_ID).toInstant());
        }

        public Builder rerun(boolean b) {
            return repeat(b ? Repeat.rerun() : Repeat.original());
        }

    }
}
