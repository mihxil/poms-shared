/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.domain.support.License;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.Portal;
import nl.vpro.domain.user.TestEditors;
import nl.vpro.i18n.Locales;

@SuppressWarnings({"unchecked", "deprecation"})
public interface MediaTestDataBuilder<
        T extends MediaTestDataBuilder<T, M> &  MediaBuilder<T, M>,
        M extends MediaObject
        >
    extends MediaBuilder<T, M>, Cloneable {

    AtomicLong idBase = new AtomicLong(0L);

    AtomicLong midBase = new AtomicLong(12345L);

    static ProgramTestDataBuilder program() {
        return new ProgramTestDataBuilder();
    }

    static ProgramTestDataBuilder program(Program program) {
        return new ProgramTestDataBuilder(program);
    }

    static ProgramTestDataBuilder program(ProgramBuilder program) {
        return new ProgramTestDataBuilder(program.mediaObject());
    }

    static ProgramTestDataBuilder broadcast() {
        return new ProgramTestDataBuilder().type(ProgramType.BROADCAST);
    }

    static ProgramTestDataBuilder clip() {
        return new ProgramTestDataBuilder().type(ProgramType.CLIP);
    }

    static ProgramTestDataBuilder promo() {
        return new ProgramTestDataBuilder().type(ProgramType.PROMO);
    }

    static GroupTestDataBuilder group() {
        return new GroupTestDataBuilder();
    }

    static GroupTestDataBuilder group(Group group) {
        return new GroupTestDataBuilder(group);
    }

    static GroupTestDataBuilder group(GroupBuilder group) {
        return new GroupTestDataBuilder(group.mediaObject());
    }

    static GroupTestDataBuilder playlist() {
        return new GroupTestDataBuilder().type(GroupType.PLAYLIST);
    }

    static GroupTestDataBuilder season() {
        return new GroupTestDataBuilder().type(GroupType.SEASON);
    }

    static GroupTestDataBuilder series() {
        return new GroupTestDataBuilder().type(GroupType.SERIES);
    }

    static SegmentTestDataBuilder segment() {
        return new SegmentTestDataBuilder();
    }


    static SegmentTestDataBuilder segment(Program parent) {
        return new SegmentTestDataBuilder().parent(parent);
    }

    static SegmentTestDataBuilder segment(Segment segment) {
        return new SegmentTestDataBuilder(segment);
    }

    static SegmentTestDataBuilder segment(SegmentBuilder segment) {
        return new SegmentTestDataBuilder(segment.mediaObject());
    }


    /**
     * @deprecated This is itself a mediabuilder nowadays.
     */
    @Deprecated
    <TT extends MediaBuilder<TT, M>> MediaBuilder<TT, M> getMediaBuilder();


    default T lean() {
        return creationDate((Instant) null).workflow(null);
    }

    default T valid() {
        return constrained();
    }

    default T validNew() throws ModificationException {
        return constrainedNew();
    }

    default T constrained() {
        return constrainedNew()
            .withId()
            .withMid();
    }

    default T constrainedNew() {
        return
            withAVType()
                .withBroadcasters()
                .withTitles()
                .withCreationDate()
                .withDuration();
    }

    default T withCreatedBy() {
        return createdBy(TestEditors.vproEditor());
    }

    default T withLastModifiedBy() {
        return lastModifiedBy(TestEditors.vproEditor());
    }

    default T withCreationDate() {
        return creationDate(Instant.now());
    }

    default T withFixedCreationDate() {
        return creationDate(LocalDate.of(2015, 3, 6).atStartOfDay(Schedule.ZONE_ID).toInstant());
    }

    default T withFixedLastPublished() {
        return lastPublished(LocalDate.of(2015, 3, 6).atStartOfDay(Schedule.ZONE_ID).plusHours(2).toInstant());
    }


    default T withLastModified() {
        return lastModified(Instant.now());
    }
    default T withFixedLastModified() {
        return lastModified(LocalDate.of(2015, 3, 6).atStartOfDay(Schedule.ZONE_ID).plusHours(1).toInstant());
    }

    default T withPublishStart() {
        return publishStart(Instant.now());
    }

    default T withFixedPublishStart() {
        return publishStart(Instant.EPOCH);
    }

    default T withPublishStop() {
        return publishStop(Instant.now().plus(2, ChronoUnit.HOURS));
    }
    default T withFixedPublishStop() {
        return publishStop(LocalDate.of(2500, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant());
    }

    default T withFixedDates() {
        return
            withFixedCreationDate()
                .withFixedLastModified()
                .withFixedLastPublished();
    }


    default T withId() {
        return id(idBase.incrementAndGet());
    }

    default T withWorkflow() {
        return published();
    }

    default T published() {
        if (mediaObject().isMerged()) {
            return workflow(Workflow.MERGED);
        } else {
            return workflow(Workflow.PUBLISHED);
        }
    }

    default T published(Instant lastPublished) {
        return published().lastPublished(lastPublished);
    }

    default T withUrn() {
        return id(idBase.incrementAndGet());
    }


    default T withMid() {
        if (StringUtils.isEmpty(getMid())) {
            withMid(midBase);
        }
        return (T) this;
    }

    default T withMid(AtomicLong base) {
        return mid("VPROWON_" + base.incrementAndGet());
    }


    default T withMids() {
        return withMids(midBase);
    }
    default T withFixedMids() {
        return withMids(new AtomicLong(20000L));
    }

    default T withMids(AtomicLong id) {
        if (mediaObject().getMid() == null) {
            withMid(id);
        }
        for (DescendantRef ref : mediaObject().getDescendantOf()) {
            ref.setMidRef("VPROWON_DG_" + id.incrementAndGet());

        }
        return (T) this;
    }

    default T title(String mainTitle) {
        return mainTitle(mainTitle);
    }

    AvailableSubtitles DUTCH_CAPTION = new AvailableSubtitles(
        Locales.DUTCH,
        SubtitlesType.CAPTION);

    default T withDutchCaptions() {
        mediaObject().getAvailableSubtitles().add(DUTCH_CAPTION);
        return (T) this;
    }
    default T withSubtitles() {
        return withDutchCaptions();
    }
    default T clearSubtitles() {
        mediaObject().getAvailableSubtitles().clear();
        return (T) this;
    }


    default T withCrids() {
        return crids("crid://bds.tv/9876", "crid://tmp.fragment.mmbase.vpro.nl/1234");
    }

    Broadcaster BNN = Broadcaster.of("BNN");
    Broadcaster AVRO = Broadcaster.of("AVRO");

    default T withBroadcasters() {
        return broadcasters(BNN, AVRO);
    }

    default T withoutBroadcasters() {
        return clearBroadcasters();
    }
    default T withoutPortals() {
        return clearPortals();
    }

    default T withPortals() {
        return portals(new Portal("3VOOR12_GRONINGEN", "3voor12 Groningen"), new Portal("STERREN24", "Sterren24"));
    }

    default T withoutOrganizations() {
        return withoutBroadcasters().withoutPortals();
    }

    default T withPortalRestrictions() {
        return portalRestrictions(
            new PortalRestriction(new Portal("STERREN24", "Sterren24")),
            new PortalRestriction(new Portal("3VOOR12_GRONINGEN", "3voor12 Groningen"), Instant.ofEpochMilli(0), Instant.ofEpochMilli(100000)));
    }

    default T withGeoRestrictions() {
        return geoRestrictions(
            GeoRestriction.builder().region(Region.NL).build(),
            GeoRestriction.builder().region(Region.BENELUX).start(Instant.ofEpochMilli(0)).stop(Instant.ofEpochMilli(100000)).build(),
            GeoRestriction.builder().region(Region.NL).start(Instant.ofEpochMilli(0)).stop(Instant.ofEpochMilli(100000)).platform(Platform.TVVOD).build()
        );
    }



    default T withTitles() {
        return titles(
            new Title("Main title", OwnerType.BROADCASTER, TextualType.MAIN),
            new Title("Short title", OwnerType.BROADCASTER, TextualType.SHORT),
            new Title("Main title MIS", OwnerType.MIS, TextualType.MAIN),
            new Title("Episode title MIS", OwnerType.MIS, TextualType.SUB));
    }


    default T withDescriptions() {
        return descriptions(
            new Description("Main description", OwnerType.BROADCASTER, TextualType.MAIN),
            new Description("Short description", OwnerType.BROADCASTER, TextualType.SHORT),
            new Description("Main description MIS", OwnerType.MIS, TextualType.MAIN),
            new Description("Episode description MIS", OwnerType.MIS, TextualType.EPISODE));
    }
    default T withTags() {
        return tags(new Tag("tag1"), new Tag("tag2"), new Tag("tag3"));
    }

    Genre NIEUWS_ACTUALITEITEN = new Genre("3.0.1.7.21");
    Genre DOCUMENTAIRE_NATUUR = new Genre("3.0.1.8.25");


    default T withGenres() {
        return genres(NIEUWS_ACTUALITEITEN, DOCUMENTAIRE_NATUUR);
    }

    @SuppressWarnings("unchecked")
    default T withSource() {
        if (mediaObject().getSource() == null) {
            return source("Naar het gelijknamige boek van W.F. Hermans");
        }
        return (T) this;
    }

    default T withCountries() {
        return countries("GB", "US");
    }

    default T withLanguages() {
        return languages("nl", "fr");
    }


    default T withAvAttributes() {
        return avAttributes(
            AVAttributes.builder()
                .bitrate(1000000)
                .byteSize(2000000L)
                .avFileFormat(AVFileFormat.M4V)
                .videoAttributes(
                    VideoAttributes.builder()
                        .videoCoding("VCODEC")
                        .horizontalSize(640)
                        .verticalSize(320)
                        .build()
                )
                .audioAttributes(
                    AudioAttributes.builder()
                        .audioCoding("ACODEC")
                        .language(Locales.NETHERLANDISH)
                        .numberOfChannels(2)
                        .build()
                )
            .build()
        );

    }

    default T withAVType() {
        return avType(AVType.VIDEO);
    }

    default T withAspectRatio() {
        return aspectRatio(AspectRatio._16x9);
    }

    @SuppressWarnings("unchecked")
    default T withDuration()  {
        return duration(Duration.of(2, ChronoUnit.HOURS));
    }

    @SuppressWarnings("unchecked")
    default T withReleaseYear() {
        return releaseYear(Short.valueOf("2004"));
            }
    default T withPersons() {
        return persons(
            Person.builder()
                .givenName("Bregtje")
                .familyName("van der Haak")
                .role(RoleType.DIRECTOR)
                .gtaaUri("http://gtaa/1234")
                .build(),
            new Person("Hans", "Goedkoop", RoleType.PRESENTER),
            new Person("Meta", "de Vries", RoleType.PRESENTER),
            new Person("Claire", "Holt", RoleType.ACTOR));
    }

    default T withAwards() {
        return awards(
            "In 2003 bekroond met een Gouden Kalf",
            "Winnaar IDFA scenarioprijs 2008.",
            "De NCRV-documentaire Onverklaarbaar? van Patrick Bisschops is genomineerd voor de prestigieuze Prix Europa 2010.",
            "De jeugdfilm BlueBird won diverse internationale prijzen, waaronder de Grote Prijs op het Montreal International Children's Film Festival en de Glazen Beer van de jongerenjury op het Filmfestival van Berlijn.");
    }

    default T withMemberOf()  {
        return withMemberOf(midBase);
    }

    default T withMemberOf(AtomicLong mids) {
        Group series = group().constrained().withMid(mids).id(100L).type(GroupType.SERIES).build();

        Group season = group().constrained().withMid(mids).id(200L).type(GroupType.SEASON).build();
        try {
            season.createMemberOf(series, 1, OwnerType.BROADCASTER);
        } catch (CircularReferenceException e) {
            throw new RuntimeException(e);
        }

        return memberOf(season, 1);
    }


    default T withAgeRating() {
        return ageRating(AgeRating._12);
    }

    default T withContentRating() {
        return contentRatings(ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL);
    }
    default T withDescendantOf() throws CircularReferenceException {
        return descendantOf(
            new DescendantRef(null, "urn:vpro:media:program:1", MediaType.BROADCAST),
            new DescendantRef(null, "urn:vpro:media:group:2", MediaType.SERIES),
            new DescendantRef("MID_123456", null, MediaType.SEASON)
        );
    }

    default T withEmail() {
        return emails("info@npo.nl", "programma@avro.nl");
    }

    Website HTTP_JOURNAAL = new Website("http://www.omroep.nl/programma/journaal");
    Website HTTP_TEGENLICHT = new Website("http://tegenlicht.vpro.nl/afleveringen/222555");
    default T withWebsites() {
        return websites(HTTP_JOURNAAL, HTTP_TEGENLICHT);
    }

    TwitterRef HASH_VPRO = new TwitterRef("#vpro");
    TwitterRef AT_TWITTER = new TwitterRef("@twitter");
    default T withTwitterRefs() {
        return twitterRefs(HASH_VPRO, AT_TWITTER);
    }

    default T withTeletext() {
        return teletext(Short.valueOf("100"));
    }



    default T withLocations() {
        Location l1 = new Location("http://player.omroep.nl/?aflID=4393288", OwnerType.NEBO);
        l1.setCreationInstant(LocalDateTime.of(2016, 3, 4, 12, 45).atZone(Schedule.ZONE_ID).toInstant());
        Location l2 = new Location("http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf", OwnerType.BROADCASTER);
        l2.setCreationInstant(LocalDateTime.of(2016, 3, 4, 13, 45).atZone(Schedule.ZONE_ID).toInstant());
        l2.setDuration(Duration.of(30L, ChronoUnit.MINUTES).plus(Duration.of(33, ChronoUnit.SECONDS)));
        Location l3 = new Location("http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf", OwnerType.BROADCASTER);
        l3.setCreationInstant(LocalDateTime.of(2016, 3, 4, 14, 45).atZone(Schedule.ZONE_ID).toInstant());
        Location l4 = new Location("http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v", OwnerType.BROADCASTER);
        l4.setDuration(Duration.of(10L, ChronoUnit.MINUTES));
        l4.setOffset(Duration.of(13L, ChronoUnit.MINUTES));
        l4.setCreationInstant(LocalDateTime.of(2016, 3, 4, 15, 45).atZone(Schedule.ZONE_ID).toInstant());

        return locations(l1, l2, l3, l4);

    }

    default T withPublishedLocations() {
        Location l1 = new Location("http://www.vpro.nl/location/1", OwnerType.BROADCASTER);
        l1.setCreationInstant(LocalDateTime.of(2017, 2, 5, 11, 42).atZone(Schedule.ZONE_ID).toInstant());
        l1.setWorkflow(Workflow.PUBLISHED);
        Location l2 = new Location("http://www.npo.nl/location/1", OwnerType.NPO);
        l2.setDuration(Duration.of(10L, ChronoUnit.MINUTES));
        l2.setOffset(Duration.of(13L, ChronoUnit.MINUTES));
        l2.setCreationInstant(LocalDateTime.of(2017, 3, 4, 15, 45).atZone(Schedule.ZONE_ID).toInstant());

        return locations(l1, l2);

    }

    default T withScheduleEvents() {
        return scheduleEvents(
            ScheduleEvent.builder()
                .channel(Channel.NED3)
                .start(Instant.ofEpochMilli(100))
                .duration(Duration.ofMillis(200))
                .guideDay(LocalDate.of(1969, 12, 31))
                .repeat(Repeat.original())
                .build(),
            ScheduleEvent.builder()
                .channel(Channel.NED3)
                .net(new Net("ZAPP"))
                .start(Instant.ofEpochMilli(300 + 3 * 24 * 3600 * 1000))
                .duration(Duration.ofMillis(50))
                .repeat(Repeat.rerun())
                .build(),
            ScheduleEvent.builder()
                .channel(Channel.HOLL)
                .start(Instant.ofEpochMilli(350 + 8 * 24 * 3600 * 1000))
                .duration(Duration.ofMillis(250))
                .rerun(true)
                .build(),
            ScheduleEvent.builder().channel(Channel.CONS).start(Instant.ofEpochMilli(600 + 10 * 24 * 3600 * 1000)).duration(Duration.ofMillis(200)).rerun(true).build()
        );
    }

    default T withScheduleEvent(LocalDateTime localDateTime, Function<ScheduleEvent, ScheduleEvent> merger) {
        return scheduleEvent(Channel.NED1, localDateTime, Duration.ofMinutes(30L), merger);
    }

    default T withScheduleEvent(LocalDateTime localDateTime) {
        return scheduleEvent(Channel.NED1, localDateTime, Duration.ofMinutes(30L));
    }

    default T withScheduleEvent(int year, int month, int day, int hour, int minutes, Function<ScheduleEvent, ScheduleEvent> merger) {
        return withScheduleEvent(LocalDateTime.of(year, month, day, hour, minutes), merger);
    }

    default T withScheduleEvent(int year, int month, int day, int hour, int minutes) {
        return withScheduleEvent(LocalDateTime.of(year, month, day, hour, minutes));
    }

    RelationDefinition VPRO_LABEL     = RelationDefinition.of("LABEL", "VPRO");
    RelationDefinition AVRO_THESAURUS = RelationDefinition.of("THESAURUS", "AVRO");
    RelationDefinition VPRO_ARTIST    = RelationDefinition.of("ARTIST", "VPRO");
    RelationDefinition EO_KOOR        = RelationDefinition.of("KOOR", "EO");

    RelationDefinition[] RELATION_DEFINITIONS = new RelationDefinition[] {VPRO_LABEL, AVRO_THESAURUS, VPRO_ARTIST, EO_KOOR};

    default T withRelations() {
        return relations(
            new Relation(VPRO_LABEL, "http://www.bluenote.com/", "Blue Note"),
            new Relation(AVRO_THESAURUS, null, "synoniem"),
            new Relation(VPRO_ARTIST, null, "Marco Borsato"),
            new Relation(EO_KOOR, null, "Ulfts Mannenkoor"));
    }

    default T withImages() {
        return images(
            Image.builder().imageUri("urn:vpro:image:1234").title("Eerste plaatje"),
            Image.builder().imageUri("urn:vpro:image:5678").title("Tweede plaatje"),
            Image.builder().owner(OwnerType.NEBO).imageUri("urn:vpro:image:2468").title("Een plaatje met andere owner"),
            Image.builder().owner(OwnerType.NEBO).imageUri("urn:vpro:image:8888").title("Nog een plaatje met andere owner")
        );
    }

    default T withImagesWithCredits() {
        Instant fixedDate = LocalDateTime.of(2017, 5, 11, 10, 0).atZone(Schedule.ZONE_ID).toInstant();
        return images(
            Image.builder()
                .imageUri("urn:vpro:image:11234")
                .title("Eerste plaatje met credits")
                .credits("CREDITS")
                .license(License.PUBLIC_DOMAIN)
                .source("SOURCE")
                .creationDate(fixedDate)
            ,
            Image.builder()
                .imageUri("urn:vpro:image:15678")
                .title("Tweede plaatje met credits")
                .credits("CREDITS")
                .license(License.PUBLIC_DOMAIN)
                .source("SOURCE")
                .creationDate(fixedDate)
            ,
            // ALso some without credits
            Image.builder()
                .owner(OwnerType.NEBO)
                .imageUri("urn:vpro:image:12468")
                .title("Een plaatje met andere owner")
                .creationDate(fixedDate),
            Image.builder()
                .owner(OwnerType.NEBO)
                .imageUri("urn:vpro:image:18888")
                .title("Nog een plaatje met andere owner")
                .creationDate(fixedDate)
        );
    }

    default T withPublishedImages() {
        return images(
            image(OwnerType.BROADCASTER, "urn:vpro:image:1234", Workflow.PUBLISHED),
            image(OwnerType.BROADCASTER, "urn:vpro:image:5678", Workflow.PUBLISHED)
        );
    }



    default T withAuthorityRecord() {
        return authoritativeRecord(Platform.INTERNETVOD);
    }

    @SuppressWarnings("unchecked")
    default T authoritativeRecord(Platform... platforms) {
        for (Platform platform : platforms) {
            Prediction prediction = mediaObject().findOrCreatePrediction(platform);
            prediction.setAuthority(Authority.SYSTEM);
            prediction.setPlannedAvailability(true);
        }
        return (T)this;
    }

    default T withMergedTo() {
        return mergedTo(MediaBuilder.group().type(GroupType.SEASON).build());
    }

    default T withIds() {
        return withIds(idBase);
    }
    default T withFixedIds() {
        return withIds(new AtomicLong(1));
    }

    default T withIds(AtomicLong id) {

        for (Image image : mediaObject().getImages()) {
            if (image.getId() == null) {
                image.setId(id.incrementAndGet());
            }
        }
        for (Location location : mediaObject().getLocations()) {
            if (location.getId() == null) {
                location.setId(id.incrementAndGet());
            }
        }
      /*  for (MemberRef ref : mediaObject().getMemberOf()) {

        }
        for (DescendantRef ref : mediaObject().getDescendantOf()) {

        }*/
        if (mediaObject().getId() == null) {
            id(id.incrementAndGet());
        }
        return (T) this;
    }

    default T withEverything() {
        return withEverything(new AtomicLong(1), new AtomicLong(20000L));
    }
    default T withEverything(Float version) {
        T result = withEverything();
        if (version != null && version < 5.9) {
            for (ScheduleEvent se :result.getMediaBuilder().build().getScheduleEvents()) {
                se.setGuideDate(null);
            }
        }
        return result;
    }

    default T withEverything(AtomicLong ids, AtomicLong mids) {
        return
            withMids(mids)
                .withAgeRating()
                .withAspectRatio()
                .withAuthorityRecord()
                .withAvAttributes()
                .withAVType()
                .withAwards()
                .withBroadcasters()
                .withContentRating()
                .withCountries()
                .withCreatedBy()
                .withDescendantOf()
                .withDescriptions()
                .withDuration()
                .withEmail()
                .withFixedDates()
                .withGenres()
                .withGeoRestrictions()
                .withImagesWithCredits()
                .withLanguages()
                .withLastModifiedBy()
                .withLocations()
                .withMemberOf(mids)
                .withPersons()
                .withPortalRestrictions()
                .withPortals()
                .withPublishedLocations()
                .withPublishStop()
                .withFixedPublishStop()
                .withFixedPublishStart()
                .withRelations()
                .withReleaseYear()
                .withScheduleEvents()
                .withSource()
                .withSubtitles()
                .withTags()
                .withTeletext()
                .withTitles()
                .withTwitterRefs()
                .withWebsites()
                .withWorkflow()
                .withIds(ids)
        ;
    }


    static Image image(OwnerType ownerType, String urn, Workflow workflow) {
        Image image = new Image(ownerType, urn);
        PublishableObjectAccess.setWorkflow(image, workflow);
        return image;
    }


    @Slf4j
    @ToString(callSuper = true)
    class ProgramTestDataBuilder extends MediaBuilder.AbstractProgramBuilder<ProgramTestDataBuilder> implements MediaTestDataBuilder<ProgramTestDataBuilder, Program> {

        ProgramTestDataBuilder() {
            super();
        }
        ProgramTestDataBuilder(Program program) {
            super(program);
        }

        @Override
        public ProgramTestDataBuilder withEverything() {
            AtomicLong mids = new AtomicLong(30000L);
            return MediaTestDataBuilder.super
                .withEverything()
                .withType()
                .withEpisodeOfIfAllowed(null, null, mids)
                .withPoProgType()
                .withPredictions()
                .withSegmentsWithEveryting()
                .withFixedSegmentMids(mids);

        }

        @Override
        public ProgramTestDataBuilder withEverything(Float version) {
            ProgramTestDataBuilder result = MediaTestDataBuilder.super.withEverything(version);
            if (version != null && version < 5.9) {
                for (Segment s : result.getMediaBuilder().build().getSegments()) {
                    for (ScheduleEvent se : s.getScheduleEvents()) {
                        se.setGuideDate(null);
                    }
                }
            }
            return result;
        }
        @Override
        public MediaBuilder<MediaBuilder.ProgramBuilder, Program> getMediaBuilder() {
            ProgramBuilder builder = MediaBuilder.program(mediaObject());
            builder.mid(mid);
            return builder;
        }

        @Override
        public ProgramTestDataBuilder constrainedNew() {
            return MediaTestDataBuilder.super.constrainedNew().withType();
        }

        public ProgramTestDataBuilder withType() {
            if (mediaObject().getType() == null) {
                type(ProgramType.BROADCAST);
            }
            return this;
        }

        public ProgramTestDataBuilder withEpisodeOf()  {
            return withEpisodeOf(null, null);
        }

        public ProgramTestDataBuilder clearEpisodeOf() {
            mediaObject.getEpisodeOf().clear();
            return this;
        }

        public ProgramTestDataBuilder withEpisodeOf(Long seriesId, Long seasonId) {
            return withEpisodeOf(seriesId, seasonId, midBase);
        }

        public ProgramTestDataBuilder withEpisodeOf(Long seriesId, Long seasonId, AtomicLong midId)  {
            Group series = MediaTestDataBuilder.group()
                .constrained()
                .type(GroupType.SERIES)
                .id(seriesId)
                .withMid(midId)
                .build();
            Group season = MediaTestDataBuilder.group()
                .constrained()
                .type(GroupType.SEASON)
                .id(seasonId)
                .withMid(midId)
                .build();
            try {
                season.createMemberOf(series, 1, OwnerType.BROADCASTER);
            } catch(CircularReferenceException e) {
                log.error(e.getMessage());
            }

            return episodeOf(season, 1);
        }
        public ProgramTestDataBuilder withEpisodeOfIfAllowed(Long seriesId, Long seasonId, AtomicLong midId)  {
            if (mediaObject().getType().hasEpisodeOf()) {
                withEpisodeOf(seriesId, seasonId, midId);
            }
            return this;

        }


        public ProgramTestDataBuilder withSegments() {

            new Segment(mediaObject.getMid() + "_1", mediaObject(), Duration.ZERO, AuthorizedDuration.ofMillis(100000));
            new Segment(mediaObject.getMid() + "_2", mediaObject(), Duration.ofMillis(100000), AuthorizedDuration.ofMillis(100000));
            new Segment(mediaObject.getMid() + "_3", mediaObject(), Duration.ofMillis(1000000), AuthorizedDuration.ofMillis(300000));
            return this;
        }

        public ProgramTestDataBuilder withSegmentsWithEveryting() {
            return
                segments(
                    MediaTestDataBuilder.segment().parent(mediaObject())
                        .withEverything()
                        .mid("VPROWON_12345_1")
                        .start(Duration.ZERO)
                        .duration(Duration.ofMillis(100000)).build(),
                    MediaTestDataBuilder.segment().parent(mediaObject()).withEverything().mid("VPROWON_12345_2").start(Duration.ofMillis(100000)).duration(Duration.ofMillis(100000)).build())
                    ;


        }
        public ProgramTestDataBuilder clearSegments() {
            mediaObject().getSegments().clear();
            return this;
        }


        @Override
        public ProgramTestDataBuilder withIds(AtomicLong id) {
            MediaTestDataBuilder.super.withIds(id);
            for (Segment segment : mediaObject.getSegments()) {
                MediaTestDataBuilder.segment(segment).withIds(id);
            }
            return this;

        }

        @Override
        public ProgramTestDataBuilder withMids(AtomicLong id) {
            MediaTestDataBuilder.super.withMids(id);
            for (Segment segment : mediaObject.getSegments()) {
                MediaTestDataBuilder.segment(segment).withMids(id);
            }
            return this;
        }
        protected ProgramTestDataBuilder withFixedSegmentMids(AtomicLong id) {
            for (Segment segment : mediaObject.getSegments()) {
                MediaTestDataBuilder.segment(segment).withMids(id);
            }
            return this;
        }


        public ProgramTestDataBuilder withPoProgType() {
            mediaObject().setPoProgTypeLegacy("Verkeersmagazine");
            return this;
        }

        public ProgramTestDataBuilder withPredictions() {
            Prediction internetVOD = new Prediction(Platform.INTERNETVOD, Prediction.State.REVOKED);
            return predictions(internetVOD, new Prediction(Platform.TVVOD));
        }

        @SuppressWarnings("unchecked")
        public ProgramTestDataBuilder predictions(Platform... platforms) {
            List<Prediction> predictions = new ArrayList<>();
            for(Platform p : platforms) {
                predictions.add(new Prediction(p));
            }
            predictions(predictions.toArray(new Prediction[predictions.size()]));
            return this;
        }
    }

    @Slf4j
    @ToString(callSuper = true)
    class GroupTestDataBuilder extends MediaBuilder.AbstractGroupBuilder<GroupTestDataBuilder> implements MediaTestDataBuilder<GroupTestDataBuilder, Group> {

        GroupTestDataBuilder() {
            super();
        }

        GroupTestDataBuilder(Group group) {
            super(group);
        }
        @Override
        public MediaBuilder<MediaBuilder.GroupBuilder, Group> getMediaBuilder() {
            GroupBuilder builder = MediaBuilder.group(mediaObject());
            builder.mid(mid);
            return builder;
        }

        @Override
        public GroupTestDataBuilder constrainedNew() {
            return MediaTestDataBuilder.super.constrainedNew()
                .withType();
        }

        public GroupTestDataBuilder withType() {
            if (mediaObject().getType() == null) {
                return type(GroupType.PLAYLIST);
            }
            return this;
        }


        public GroupTestDataBuilder withPoSeriesID() {
            return poSeriesID("VPRO_12345");
        }

        @Override
        public GroupTestDataBuilder withEverything() {
            return MediaTestDataBuilder.super.withEverything()
                .withType()
                .withPoSeriesID()
                ;

        }




    }

    @Slf4j
    @ToString(callSuper = true)
    class SegmentTestDataBuilder extends MediaBuilder.AbstractSegmentBuilder<SegmentTestDataBuilder>
        implements MediaTestDataBuilder<SegmentTestDataBuilder, Segment> {

        SegmentTestDataBuilder() {
            super();
        }

        SegmentTestDataBuilder(Segment segment) {
            super(segment);
        }


        @Override
        public MediaBuilder<MediaBuilder.SegmentBuilder, Segment> getMediaBuilder() {
            SegmentBuilder builder = MediaBuilder.segment(mediaObject());
            builder.mid(mid);
            return builder;
        }

        public SegmentTestDataBuilder withStart() {
            return start(Duration.ofMinutes(2));
        }


        @Override
        public SegmentTestDataBuilder constrainedNew() {
            return MediaTestDataBuilder.super.constrainedNew().
                withStart();
        }

        @Override
        public SegmentTestDataBuilder withEverything() {
            return MediaTestDataBuilder.super.withEverything()
                .withStart();

        }
    }
}
