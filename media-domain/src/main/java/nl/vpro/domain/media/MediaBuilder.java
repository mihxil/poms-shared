/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.ToString;

import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.xml.bind.JAXB;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.EmbargoBuilder;
import nl.vpro.domain.classification.Term;
import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.exceptions.ModificationException;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.Editor;
import nl.vpro.domain.user.Portal;
import nl.vpro.domain.user.ThirdParty;
import nl.vpro.i18n.LocalizedString;
import nl.vpro.util.DateUtils;
import nl.vpro.util.TimeUtils;

import static nl.vpro.domain.EmbargoBuilder.fromLocalDate;
import static nl.vpro.util.DateUtils.toInstant;

@SuppressWarnings("unchecked")
public interface MediaBuilder<B extends MediaBuilder<B, M>, M extends MediaObject> extends EmbargoBuilder<B> {

    static ProgramBuilder program() {
        return new ProgramBuilder();
    }

    static ProgramBuilder program(ProgramType type) {
        return program().type(type);
    }

      static ProgramBuilder broadcast() {
          return program()
            .type(ProgramType.BROADCAST)
              .audioOrVideo();
    }

    static ProgramBuilder clip() {
        return program()
            .type(ProgramType.CLIP)
            .audioOrVideo();
    }

    static ProgramBuilder program(Program program) {
        return new ProgramBuilder(program);
    }

    static GroupBuilder group() {
        return new GroupBuilder();
    }

    static GroupBuilder group(GroupType type) {
        return group().type(type);
    }

    static GroupBuilder group(Group group) {
        return new GroupBuilder(group);
    }

    static SegmentBuilder segment() {
        return new SegmentBuilder();
    }

    static SegmentBuilder segment(Segment segment) {
        return new SegmentBuilder(segment);
    }

    M build();

    /**
     * Accesss to the underlying media object. Though this is public, this should normally not be used by user code.
     * Use {@link #build()} in stead.
     * @return
     */
    M mediaObject();

    String getMid();


    @SuppressWarnings("unchecked")
    default B id(Long id) {
        mediaObject().setId(id);
        return (B)this;
    }

    B mid(String mid);

    @SuppressWarnings("unchecked")
    default B urn(String urn) {
        mediaObject().setUrn(urn);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B createdBy(Editor user) {
        mediaObject().setCreatedBy(user);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B createdBy(String user) {
        mediaObject().setCreatedBy(user(user));
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B lastModifiedBy(Editor user) {
        mediaObject().setLastModifiedBy(user);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B lastModifiedBy(String user) {
        mediaObject().setLastModifiedBy(user(user));
        return (B) this;
    }

    @Deprecated
    default B creationDate(Date date) {
        return creationDate(toInstant(date));
    }
    @SuppressWarnings("unchecked")
    default B creationDate(Instant date) {
        mediaObject().setCreationInstant(date);
        return (B)this;
    }

    default B creationDate(ZonedDateTime date) {
        return creationDate(toInstant(date));
    }
    default B creationDate(LocalDateTime date) {
        return creationDate(fromLocalDate(date));
    }

    default B creationInstant(Instant date) {
        return creationDate(date);
    }

    default B clearCreationDate() {
        mediaObject().setCreationInstant(null);
        return (B) this;
    }

    @Deprecated
    default B lastModified(Date date) {
        return lastModified(DateUtils.toInstant(date));
    }

    default B lastModified(Instant date) {
        mediaObject().setLastModifiedInstant(date);
        return (B) this;
    }

    default B lastModified(ZonedDateTime date) {
        return lastModified(toInstant(date));
    }

    default B lastModified(LocalDateTime date) {
        return lastModified(fromLocalDate(date));
    }


    @Override
    default B publishStart(Instant date) {
        mediaObject().setPublishStartInstant(date);
        return (B) this;
    }

    @Override
    default B publishStop(Instant date) {
        mediaObject().setPublishStopInstant(date);
        return (B) this;
    }

    @Deprecated
    default B lastPublished(Date date) {
        return lastPublished(DateUtils.toInstant(date));
    }

    @SuppressWarnings("unchecked")
    default B lastPublished(Instant date) {
        mediaObject().setLastPublishedInstant(date);
        return (B) this;
    }

    default B lastPublished(LocalDateTime date) {
        return lastPublished(fromLocalDate(date));
    }

    @SuppressWarnings("unchecked")
    default B workflow(Workflow workflow) {
        mediaObject().setWorkflow(workflow);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B crids(Collection<String> crids) {
        for(String crid : crids) {
            if (crid != null) {
                mediaObject().addCrid(crid);
            }
        }
        return (B)this;
    }

    default B crids(String... crids) {
        return crids(Arrays.asList(crids));
    }


    default B broadcasters(Broadcaster... broadcasters) {
        return broadcasters(Arrays.asList(broadcasters));
    }

    @SuppressWarnings("unchecked")
    default B broadcasters(Collection<Broadcaster> broadcasters) {
        for(Broadcaster broadcaster : broadcasters) {
            mediaObject().addBroadcaster(broadcaster);
        }
        return (B)this;
    }

    default B broadcasters(String... broadcasters) {
        for (String broadcaster : broadcasters) {
            mediaObject().addBroadcaster(new Broadcaster(broadcaster));
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B clearBroadcasters() {
        mediaObject().getBroadcasters().clear();
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B portals(Portal... portals) {
        for (Portal portal : portals) {
            mediaObject().addPortal(portal);
        }
        return (B) this;
    }

    default B portals(String... portals) {
        for (String portal : portals) {
            mediaObject().addPortal(new Portal(portal));
        }
        return (B) this;
    }


    @SuppressWarnings("unchecked")
    default B clearPortals() {
        mediaObject().clearPortals();
        return (B)this;
    }


    @SuppressWarnings("unchecked")
    default B thirdParties(ThirdParty... thirdParties) {
        for(ThirdParty thirdParty : thirdParties) {
            mediaObject().addThirdParty(thirdParty);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B portalRestrictions(PortalRestriction... restrictions) {
        for(PortalRestriction publicationRule : restrictions) {
            mediaObject().addPortalRestriction(publicationRule);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B portalRestrictions(Portal... restrictions) {
        for (Portal publicationRule : restrictions) {
            mediaObject().addPortalRestriction(new PortalRestriction(publicationRule));
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B portalRestrictions(String... portals) {
        for (String portal : portals) {
            mediaObject().addPortalRestriction(new PortalRestriction(new Portal(portal)));
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B geoRestrictions(GeoRestriction... restrictions) {
        for(GeoRestriction publicationRule : restrictions) {
            mediaObject().addGeoRestriction(publicationRule);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B geoRestrictions(Region... restrictions) {
        for (Region region : restrictions) {
            mediaObject().addGeoRestriction(new GeoRestriction(region));
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B titles(Title... titles) {
        for(Title title : titles) {
            mediaObject().addTitle(title);
        }
        return (B)this;
    }

    default B mainTitle(@Nonnull String title, @Nonnull OwnerType owner) {
        return titles(new Title(title, owner, TextualType.MAIN));
    }

    default B mainTitle(@Nonnull String title) {
        return mainTitle(title, OwnerType.BROADCASTER);
    }


    default B subTitle(String title, @Nonnull OwnerType owner) {
        if (StringUtils.isNotEmpty(title)) {
            return titles(new Title(title, owner, TextualType.SUB));
        } else {
            return (B) this;
        }
    }

    default B subTitle(String title) {
        return subTitle(title, OwnerType.BROADCASTER);
    }


    default B lexicoTitle(String title, @Nonnull OwnerType owner) {
        if (StringUtils.isNotEmpty(title)) {
            return titles(new Title(title, owner, TextualType.LEXICO));
        } else {
            return (B) this;
        }
    }

    default B lexicoTitle(String title) {
        if (StringUtils.isNotEmpty(title)) {

            return lexicoTitle(title, OwnerType.BROADCASTER);
        } else {
            return (B) this;
        }
    }

    default B descriptions(Description... descriptions) {
        for(Description description : descriptions) {
            mediaObject().addDescription(description);
        }
        return (B)this;
    }

    default B mainDescription(String description, @Nonnull OwnerType owner) {
        if (StringUtils.isNotEmpty(description)) {
            return descriptions(new Description(description, owner, TextualType.MAIN));
        } else {
            return (B) this;
        }
    }

    default B mainDescription(String description) {
        return mainDescription(description, OwnerType.BROADCASTER);
    }

    @SuppressWarnings("unchecked")
    default B genres(Genre... genres) {
        for(Genre genre : genres) {
            mediaObject().addGenre(genre);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B genres(Collection<Genre> genres) {
        for(Genre genre : genres) {
            mediaObject().addGenre(genre);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B genres(String... termIds) {
        for (String genre : termIds) {
            mediaObject().addGenre(new Genre(new Term(genre)));
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B tags(Tag... tags) {
        for(Tag tag : tags) {
            mediaObject().addTag(tag);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B tags(String... tags) {
        for (String tag : tags) {
            mediaObject().addTag(new Tag(tag));
        }
        return (B) this;
    }


    @SuppressWarnings("unchecked")
    default B source(String source) {
        mediaObject().setSource(source);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B countries(String... countries) {
        for(String country : countries) {
            mediaObject().addCountry(country);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B languages(String... languages) {
        for(String language : languages) {
            mediaObject().addLanguage(LocalizedString.adapt(language));
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B avType(AVType type) {
        mediaObject().setAVType(type);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B avAttributes(AVAttributes avAttribute) {
        mediaObject().setAvAttributes(avAttribute);
        return (B)this;

    }

    @SuppressWarnings("unchecked")
    default B aspectRatio(AspectRatio as) {
        AVAttributes av = mediaObject().getAvAttributes();
        if(av == null) {
            av = new AVAttributes();
            mediaObject().setAvAttributes(av);
        }
        VideoAttributes va = av.getVideoAttributes();
        if(va == null) {
            va = new VideoAttributes();
            av.setVideoAttributes(va);
        }
        va.setAspectRatio(as);
        return (B)this;

    }

    /**
     * @deprecated Use {@link #duration(Duration)}
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    default B duration(Date duration) {
        try {
            mediaObject().setDurationWithDate(duration);
        } catch (ModificationException e) {
            throw new IllegalStateException(e);
        }
        return (B)this;
    }


    @SuppressWarnings("unchecked")
    default B duration(java.time.Duration duration) {
        try {
            mediaObject().setDuration(duration);
        } catch (ModificationException e) {
            throw new IllegalStateException(e);
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B releaseYear(Short y) {
        mediaObject().setReleaseYear(y);
        return (B)this;
    }

    default B persons(Person... persons) {
        return persons(Arrays.asList(persons));
    }

    default B person(RoleType role, String givenName, String familyName) {
        mediaObject().addPerson(Person.builder()
            .givenName(givenName)
            .familyName(familyName)
            .role(role)
            .build());
        return (B) this;
    }


    @SuppressWarnings("unchecked")
    default B persons(Collection<Person> persons) {
        persons.forEach(person -> mediaObject().addPerson(person));
        return (B)this;
    }

    default B intentions(Intentions... intentions) {
        return intentions(Arrays.asList(intentions));
    }

    default B intentions(Collection<Intentions> intentions) {
        intentions.forEach(intention -> mediaObject().addIntention(intention));
        return (B)this;
    }

    default B targetGroups(TargetGroups ...targetGroups) {
        return targetGroups(Arrays.asList(targetGroups));
    }

    default B targetGroups(Collection<TargetGroups> targetGroups) {
        targetGroups.forEach(targetGroup -> mediaObject().addTargetGroups(targetGroup));
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B awards(String... awards) {
        for(String award : awards) {
            mediaObject().addAward(award);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B memberOf(MemberRef... memberRef) throws CircularReferenceException {
        for (MemberRef ref : memberRef) {
            if (ref.getMember() == null) {
                ref.setMember(mediaObject());
            }
        }
        mediaObject().getMemberOf().addAll(Arrays.asList(memberRef));
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B memberOf(@Nonnull MediaObject media, Integer number) throws CircularReferenceException {
        mediaObject().createMemberOf(media, number, null);
        return (B) this;
    }

    default B memberOf(MediaObject media) throws CircularReferenceException {
        return memberOf(media, 1);
    }


    default B memberOf(String mid, Integer number) throws CircularReferenceException {
        return memberOf(new MemberRef(mid, number));
    }


    default B memberOf(String mid) throws CircularReferenceException {
        return memberOf(new MemberRef(mid));
    }
    default B clearMemberOf() {
        mediaObject().getMemberOf().clear();
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B ageRating(AgeRating ageRating) {
        mediaObject().setAgeRating(ageRating);
        return (B)this;
    }
    default B ageRatingAllIfUnset() {
        if (mediaObject().getAgeRating() == null) {
            mediaObject().setAgeRating(AgeRating.ALL);
        }
        return (B) this;
    }


    @SuppressWarnings("unchecked")
    default B contentRatings(ContentRating... contentRatings) {
        mediaObject().getContentRatings().addAll(Arrays.asList(contentRatings));
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B emails(String... emails) {
        mediaObject().getEmail().addAll(Arrays.asList(emails));
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B websites(Website... websites) {
        mediaObject().getWebsites().addAll(Arrays.asList(websites));
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B websites(String... websites) {
        mediaObject().getWebsites().addAll(Arrays.stream(websites).map(Website::new).collect(Collectors.toList()));
        return (B) this;
    }

    default B clearWebsites() {
        mediaObject().getWebsites().clear();
        return (B) this;
    }

    default B twitterRefs(String... twitter) {
        List<TwitterRef> reference = new ArrayList<>();
        for(String t : twitter) {
            reference.add(new TwitterRef(t));
        }
        mediaObject().getTwitterRefs().addAll(reference);
        return (B)this;
    }

    default B twitterRefs(TwitterRef... twitter) {
        List<TwitterRef> reference = new ArrayList<>(Arrays.asList(twitter));
        mediaObject().getTwitterRefs().addAll(reference);
        return (B) this;
    }
    default B clearTwitterRefs() {
        mediaObject().getTwitterRefs().clear();
        return (B) this;
    }
    @SuppressWarnings("unchecked")
    default B teletext(Short teletext) {
        mediaObject().setTeletext(teletext);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B locations(Location... locations) {
        for(Location location : locations) {
            mediaObject().addLocation(location);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B locations(Iterable<Location> locations) {
        for(Location location : locations) {
            mediaObject().addLocation(location);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B locations(String... locations) {
        for (String location : locations) {
            mediaObject().addLocation(new Location(location, OwnerType.BROADCASTER));
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B clearLocations() {
        mediaObject().getLocations().clear();
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B scheduleEvents(ScheduleEvent... scheduleEvents) {
        for(ScheduleEvent event : scheduleEvents) {
            event.setParent(mediaObject());
        }
        return (B)this;
    }

    default B scheduleEvent(Channel c, LocalDateTime time, java.time.Duration duration, Function<ScheduleEvent, ScheduleEvent> merger, ScheduleEventTitle... titles) {
        return scheduleEvent(c, time.atZone(Schedule.ZONE_ID).toInstant(), duration, merger, titles);
    }

    default B scheduleEventRerun(Channel c, LocalDateTime time, java.time.Duration duration, ScheduleEventTitle... titles) {
        return scheduleEvent(c, time.atZone(Schedule.ZONE_ID).toInstant(), duration,
            e -> {e.setRepeat(Repeat.rerun());return e;},
            titles);
    }


    default B scheduleEvent(Channel c, LocalDateTime time, java.time.Duration duration, ScheduleEventTitle... titles) {
        return scheduleEvent(c, time, duration, e -> e, titles);
    }

    default B scheduleEvent(Channel c, java.time.Instant time, java.time.Duration duration, Function<ScheduleEvent, ScheduleEvent> merger, ScheduleEventTitle... titles) {
        ScheduleEvent event = ScheduleEvent.builder()
            .channel(c)
            .start(time)
            .duration(duration)
            .build();
        event.setParent(mediaObject());
        for (ScheduleEventTitle title : titles) {
            event.addTitle(title);
        }
        merger.apply(event);
        return (B) this;
    }

     default B scheduleEvent(ScheduleEvent event) {
        event.setParent(mediaObject());
        return (B) this;
    }

    default B scheduleEvent(Channel c, java.time.Instant time, java.time.Duration duration, ScheduleEventTitle... titles) {
        return scheduleEvent(c, time, duration, e->e, titles);
    }


    @SuppressWarnings("unchecked")
    default B firstScheduleEventTitles(ScheduleEventTitle... titles) {
        for (ScheduleEventTitle title : titles) {
            mediaObject().getScheduleEvents().first().addTitle(title);
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B scheduleEventTitles(Channel channel, LocalDateTime time, ScheduleEventTitle... titles) {
        ScheduleEvent scheduleEvent = MediaObjects.findScheduleEvent(channel, time, mediaObject().getScheduleEvents());
        if (scheduleEvent != null) {
            for (ScheduleEventTitle title : titles) {
                scheduleEvent.addTitle(title);
            }
        }
        return (B) this;
    }


    @SuppressWarnings("unchecked")
    default B firstScheduleEventDescriptions(ScheduleEventDescription... descriptions) {
        for (ScheduleEventDescription description : descriptions) {
            mediaObject().getScheduleEvents().first().addDescription(description);
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B scheduleEventDescriptions(Channel channel, LocalDateTime time, ScheduleEventDescription... descriptions) {
        ScheduleEvent scheduleEvent = MediaObjects.findScheduleEvent(channel, time, mediaObject().getScheduleEvents());
        if (scheduleEvent != null) {
            for (ScheduleEventDescription description : descriptions) {
                scheduleEvent.addDescription(description);
            }
        }
        return (B) this;
    }

    /**
     * This adds descendantOf's explicitely. The use cases for this are limited, a mediaobject basicly has {@link #memberOf} or {@link ProgramBuilder#episodeOf(String)}
     * If your mediaobject is not going to be serialized to the database (e.g. in test cases) you might want to fill descendantof explicitely.
     *
     */
    @SuppressWarnings("unchecked")
    default B descendantOf(DescendantRef... refs) throws CircularReferenceException {
        mediaObject().setDescendantOf(new TreeSet<>(Arrays.asList(refs)));
        return (B)this;
    }


    /**
     * See {@link #descendantOf(DescendantRef...)}
     *
     */
    @Deprecated
    default B descendantOf(String... mids) {
        return descendantOf(Arrays.stream(mids).map(m -> DescendantRef.builder().midRef(m).build())
            .toArray(DescendantRef[]::new));

    }

    @SuppressWarnings("unchecked")
    default B relations(Relation... relations) {
        for(Relation relation : relations) {
            mediaObject().addRelation(relation);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B images(Image... images) {
        for(Image image : images) {
            if (image != null) {
                mediaObject().addImage(image);
            }
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B images(Image.Builder... images) {
        for (Image.Builder image : images) {
            mediaObject().addImage(image.build());
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B embeddable(boolean isEmbeddable) {
        mediaObject().setEmbeddable(isEmbeddable);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B mergedTo(MediaObject media) {
        workflow(Workflow.MERGED);
        mediaObject().setMergedTo(media);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B mergedTo(String media) {
        workflow(Workflow.MERGED);
        mediaObject().setMergedToRef(media);
        return (B) this;
    }

    default B clearMergedTo() {
        if (mediaObject().getWorkflow() == Workflow.MERGED) {
            workflow(Workflow.PUBLISHED);
        }
        mediaObject().setMergedTo(null);
        return (B) this;
    }

    default B video() {
        return
            ageRatingAllIfUnset()
                .avType(AVType.VIDEO);
    }

    default B audio() {
        return
            ageRatingAllIfUnset()
                .avType(AVType.AUDIO);
    }


     default B audioOrVideo() {
        return
            ageRatingAllIfUnset()
                .avType(AVType.MIXED);
    }
    /**
     * Makes a (deep) copy of this builder. This returns a new instance on which you can make changes without affecting the original one.
     */
    B copy();


    static Editor user(String principalId) {
        return new Editor(principalId, null, null, null, null);
    }


    @ToString
    abstract class AbstractBuilder<T extends AbstractBuilder<T, M>, M extends MediaObject>  implements MediaBuilder<T, M>, Cloneable {

        protected String mid;
        protected boolean midSet = false;

        protected AbstractBuilder(M m) {
            this.mediaObject = m;
            this.mid = m.getMid();
        }

        @Override
        public T mid(String m) {
            this.mid = m;
            this.midSet = true;
            return (T) this;
        }

        @Valid
        transient M mediaObject;

        @Override
        public M build() {
            if (midSet) {
                mediaObject.setMid(mid);
            }
            return mediaObject;
        }
        @Override
        public M mediaObject() {
            return mediaObject;
        }
        @Override
        public String getMid() {
            if (midSet) {
                return mid;
            }
            return mediaObject.getMid();
        }



        @Override
        public T copy() {
            try {
                T o = (T) super.clone();
                StringWriter writer = new StringWriter();
                JAXB.marshal(this.mediaObject, writer);
                o.mediaObject = MediaObjects.deepCopy(this.mediaObject);
                o.mediaObject.setMid(null);
                o.mid(this.mid);
                return o;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @ToString(callSuper = true)
    abstract class AbstractProgramBuilder<T extends AbstractProgramBuilder<T> & MediaBuilder<T,Program>> extends AbstractBuilder<T, Program> implements MediaBuilder<T, Program> {

        protected AbstractProgramBuilder() {
            this(new Program());
        }

        protected AbstractProgramBuilder(Program program) {
            super(program);
        }

        public T type(ProgramType type) {
            mediaObject().setType(type);
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T episodeOf(MemberRef... memberRef) throws CircularReferenceException {
            for(MemberRef ref : memberRef) {
                if (ref.getMember() == null) {
                    ref.setMember(mediaObject());
                }
            }
            mediaObject().getEpisodeOf().addAll(Arrays.asList(memberRef));
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T episodeOf(Group group, Integer number) throws CircularReferenceException {
            mediaObject().createEpisodeOf(group, number, null);
            return (T) this;
        }

        public T episodeOf(String mid, Integer number) {
            return episodeOf(new MemberRef(mid, number));
        }

        public T episodeOf(String mid) {
            return episodeOf(new MemberRef(mid));
        }

        public T segments(Segment... segments) {
            return segments(Arrays.asList(segments));
        }

        @SuppressWarnings("unchecked")
        public T segments(Iterable<Segment> segments) {
            for (Segment segment : segments) {
                mediaObject().addSegment(segment);
            }
            return (T) this;
        }



        @SuppressWarnings("unchecked")
        public T predictions(Prediction... predictions) {
            if(predictions == null || predictions.length == 0) {
                mediaObject().getPredictions().clear();
            } else {
                for(Prediction pred : predictions) {
                    pred.setMediaObject(mediaObject);
                    mediaObject().getPredictions().add(pred);
                }
            }
            return (T) this;
        }

        @Override
        public Program build() {
            super.build();
            return mediaObject;
        }

    }

    class ProgramBuilder extends AbstractProgramBuilder<ProgramBuilder> {
        protected ProgramBuilder() {
        }

        protected ProgramBuilder(Program program) {
            super(program);
        }
    }

    @ToString(callSuper = true)
    abstract  class AbstractGroupBuilder<T extends AbstractGroupBuilder<T>> extends AbstractBuilder<T, Group> implements MediaBuilder<T, Group> {

        protected AbstractGroupBuilder() {
            this(new Group());
        }

        protected AbstractGroupBuilder(Group group) {
            super(group);
        }

        public T type(GroupType type) {
            mediaObject().setType(type);
            return (T) this;
        }

        public T poSeriesID(String poSeriesID) {
            return mid(poSeriesID);
        }
        public T ordered(Boolean ordered) {
            mediaObject().setOrdered(ordered);
            return (T) this;
        }
    }
    class GroupBuilder extends AbstractGroupBuilder<GroupBuilder> {
        protected GroupBuilder() {
        }

        protected GroupBuilder(Group group) {
            super(group);
        }
    }

    @ToString(callSuper = true)
    abstract class AbstractSegmentBuilder<T extends AbstractSegmentBuilder<T>> extends AbstractBuilder<T, Segment> implements MediaBuilder<T, Segment> {

        protected AbstractSegmentBuilder() {
            this(new Segment());
        }

        protected AbstractSegmentBuilder(Segment segment) {
            super(segment);
        }

        /**
         * @deprecated  Use #start(java.time.Duration)
         */
        @Deprecated
        public T start(Date start) {
            mediaObject().setStart(TimeUtils.durationOf(start).orElse(null));
            return (T) this;
        }

        public T start(java.time.Duration start) {
            mediaObject().setStart(start);
            return (T) this;
        }
        public T parent(Program parent) {
            mediaObject().setParent(parent);
            return (T) this;
        }

        public T midRef(String midRef) {
            mediaObject().setMidRef(midRef);
            return (T) this;
        }

        public T type(SegmentType segmentType) {
            mediaObject().setType(segmentType);
            return (T) this;
        }
    }

    class SegmentBuilder extends AbstractSegmentBuilder<SegmentBuilder> {
        protected SegmentBuilder() {
        }

        protected SegmentBuilder(Segment segment) {
            super(segment);
        }
    }
}
