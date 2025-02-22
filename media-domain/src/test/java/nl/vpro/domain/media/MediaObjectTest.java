/*
 * Copyright (C) 2008 All rights reserved VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.io.StringReader;
import java.time.Instant;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.xml.bind.JAXB;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.i18n.Locales;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.domain.media.MediaDomainTestHelper.validator;
import static nl.vpro.domain.media.support.OwnerType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("deprecation")
public class MediaObjectTest {

    @BeforeClass
    public static void init() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void testIdFromUrn() {
        assertThat(Program.idFromUrn("urn:vpro:media:program:12463402")).isEqualTo(12463402L);
    }

    @Test
    public void testAddCrid() {
        MediaObject mediaObject = new Program();
        mediaObject.addCrid(null);
        assertThat(mediaObject.getCrids()).isEmpty();

        mediaObject.addCrid("Crid 1");
        assertThat(mediaObject.getCrids()).containsExactly("Crid 1");

        mediaObject.addCrid("Crid 2");
        assertThat(mediaObject.getCrids()).containsExactly("Crid 1", "Crid 2");

        mediaObject.addCrid("Crid 1");
        mediaObject.addCrid("Crid 2");
        assertThat(mediaObject.getCrids()).containsExactly("Crid 1", "Crid 2");
    }

    @Test
    public void testAddTitle() {
        MediaObject mediaObject = new Program();
        mediaObject.addTitle(null);
        assertThat(mediaObject.getTitles()).isEmpty();

        mediaObject.addTitle(new Title("Title 1", OwnerType.MIS, TextualType.MAIN));
        assertThat(mediaObject.getTitles().first().getParent()).isEqualTo(mediaObject);
        assertThat(mediaObject.getTitles()).hasSize(1);
        assertThat(mediaObject.getTitles().last().get()).isEqualTo("Title 1");

        mediaObject.addTitle(new Title("Title 2", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getTitles()).hasSize(2);
        assertThat(mediaObject.getTitles().last().get()).isEqualTo("Title 2");

        mediaObject.addTitle(new Title("Title 3", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getTitles()).hasSize(2);
        assertThat(mediaObject.getTitles().last().get()).isEqualTo("Title 3");
    }

    @Test
    public void testAddDescription() {
        MediaObject mediaObject = new Program();
        mediaObject.addDescription(null);
        assertThat(mediaObject.getDescriptions()).isEmpty();

        mediaObject.addDescription(new Description("Des 1", OwnerType.MIS, TextualType.MAIN));
        assertThat(mediaObject.getDescriptions().first().getParent()).isEqualTo(mediaObject);
        assertThat(mediaObject.getDescriptions()).hasSize(1);
        assertThat(mediaObject.getDescriptions().last().get()).isEqualTo("Des 1");

        mediaObject.addDescription(new Description("Des 2", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getDescriptions()).hasSize(2);
        assertThat(mediaObject.getDescriptions().last().get()).isEqualTo("Des 2");

        mediaObject.addDescription(new Description("Des 3", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getDescriptions()).hasSize(2);
        assertThat(mediaObject.getDescriptions().last().get()).isEqualTo("Des 3");
    }

    @Test
    public void testGetAncestors() throws CircularReferenceException {
        Program program = new Program();
        Group group1 = new Group(GroupType.PLAYLIST);
        Group group2 = new Group(GroupType.PLAYLIST);
        Group root = new Group(GroupType.PLAYLIST);

        program.createMemberOf(group1, 1, null);
        group1.createMemberOf(group2, 1, null);
        group2.createMemberOf(root, 1, null);

        SortedSet<MediaObject> ancestors = program.getAncestors();

        assertThat(ancestors).hasSize(3);
    }

    @Test
    public void testGetAncestorsForUniqueReferences() throws CircularReferenceException {
        Program program = new Program();
        Group group1 = new Group(GroupType.PLAYLIST);
        Group group2 = new Group(GroupType.PLAYLIST);
        Group root = new Group(GroupType.PLAYLIST);

        program.createMemberOf(group1, 1, null);
        program.createMemberOf(group2, 2, null);
        group1.createMemberOf(root, 1, null);
        group2.createMemberOf(root, 2, null);

        SortedSet<MediaObject> ancestors = program.getAncestors();

        assertThat(ancestors).hasSize(3); // somebody thought this should be 4?
    }

    @Test
    public void testGetAncestorsForUniqueReferencesWithId() throws CircularReferenceException {
        Program program = new Program();
        Group group1 = new Group(GroupType.PLAYLIST);
        group1.setId(1L);
        Group group2 = new Group(GroupType.PLAYLIST);
        group2.setId(2L);
        Group root = new Group(GroupType.PLAYLIST);
        root.setId(3L);

        program.createMemberOf(group1, 1, null);
        program.createMemberOf(group2, 2, null);
        group1.createMemberOf(root, 1, null);
        group2.createMemberOf(root, 2, null);

        SortedSet<MediaObject> ancestors = program.getAncestors();

        assertThat(ancestors).hasSize(3);
    }

    @Test(expected = CircularReferenceException.class)
    public void testCreateMemberOfForSelf() throws CircularReferenceException {
        Group g1 = new Group();

        g1.createMemberOf(g1, 1, null);
    }

    @Test(expected = CircularReferenceException.class)
    public void testCreateMemberOfForCircularity() throws CircularReferenceException {
        Group g1 = new Group(GroupType.PLAYLIST);
        Group g2 = new Group(GroupType.PLAYLIST);
        Group g3 = new Group(GroupType.PLAYLIST);
        Group g4 = new Group(GroupType.PLAYLIST);

        g1.createMemberOf(g2, 1, null);
        g2.createMemberOf(g3, 1, null);
        g3.createMemberOf(g4, 1, null);
        g4.createMemberOf(g1, 1, null);

        //assertThat(g1.getAncestors()).hasSize(4);
    }

    protected Program getTestProgram() throws CircularReferenceException {
        Program program = new Program(1L);
        program.setPredictions(Arrays.asList(new Prediction(Platform.INTERNETVOD)));
        program.setUrn("urn:vpro:media:program:123");
        program.setCreationInstant(Instant.EPOCH);
        Title t = new Title("bla", OwnerType.BROADCASTER, TextualType.MAIN);
        program.addTitle(t);
        program.addDescription("bloe", OwnerType.BROADCASTER, TextualType.MAIN);

        Group group = new Group();
        group.setUrn("urn:vpro:media:group:122");
        program.addBroadcaster(new Broadcaster("VPRO", "V.P.R.O"));
        MemberRef ref = group.createMember(program, 1, null);
        ref.setHighlighted(true);
        ref.setAdded(Instant.EPOCH);

        return program;
    }

    @Test
    public void program() throws CircularReferenceException {
        Program p1 = new Program();
        assertThat(p1.getMemberOf()).isEmpty();
        Program program = getTestProgram();
        assertThat(program.getMemberOf()).hasSize(1);
    }

    @Test
    public void testProgramValidation() {
        Program p = new Program();
        p.setType(ProgramType.BROADCAST);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        Set<ConstraintViolation<Program>> constraintViolations = validator.validate(p);
        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    public void testMidValidation() {
        Program p = new Program();
        p.setType(ProgramType.BROADCAST);
        p.setAVType(AVType.MIXED);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        p.setMid("foo/bar");
        Set<ConstraintViolation<Program>> constraintViolations = validator.validate(p);
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{nl.vpro.constraints.mid}");
    }



    @Test
    public void testLanguageValidation() {
        Program p = new Program();
        p.setType(ProgramType.BROADCAST);
        p.setAVType(AVType.MIXED);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        p.setLanguages(Arrays.asList(new Locale("ZZ"), Locales.DUTCH));

        Set<ConstraintViolation<Program>> constraintViolations = validator.validate(p);
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessageTemplate()).startsWith("{nl.vpro.constraints.language}");
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("[zz, nl] contains an invalid ISO 639 language code: zz");

    }

    @Test
    public void testRelationValidation() {
        Relation r = new Relation(new RelationDefinition("AAAA", "a", "a"));
        r.setUriRef(":");
        Program p = new Program(AVType.AUDIO, ProgramType.BROADCAST);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        p.setType(ProgramType.CLIP);
        p.addRelation(r);

        Set<ConstraintViolation<Program>> constraintViolations = validator.validate(p);
        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    public void sortDate() {
        Program program = new Program();
        assertThat(Math.abs(program.getSortInstant().toEpochMilli() - System.currentTimeMillis())).isLessThan(10000);
        Instant publishDate = Instant.ofEpochMilli(1344043500362L);
        program.setPublishStartInstant(publishDate);
        assertThat(program.getSortInstant()).isEqualTo(publishDate);
        ScheduleEvent se = new ScheduleEvent();
        se.setStartInstant(Instant.ofEpochMilli(1444043500362L));
        program.addScheduleEvent(se);
        assertThat(program.getSortInstant()).isEqualTo(se.getStartInstant());
        Segment segment = new Segment();
        program.addSegment(segment);
        assertThat(segment.getSortInstant()).isEqualTo(se.getStartInstant());
    }

    @Test
    public void testAddLocationOnDuplicates() {
        Location l1 = new Location("TEST_URL", OwnerType.NEBO);
        l1.setAvAttributes(new AVAttributes(100000, AVFileFormat.WM));

        Location l2 = new Location("TEST_URL", OwnerType.NEBO);
        l2.setAvAttributes(new AVAttributes(110000, AVFileFormat.H264));

        Program p = MediaBuilder.program().build();
        p.addLocation(l1);
        p.addLocation(l2);

        Assertions.assertThat(p.getLocations()).hasSize(1);
        Assertions.assertThat(p.getLocations().first().getBitrate()).isEqualTo(110000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddLocationOnDuplicatesCollisions() {
        Location l1 = new Location("TEST_URL", OwnerType.NEBO);
        l1.setAvAttributes(new AVAttributes(100000, AVFileFormat.WM));

        Location l2 = new Location("TEST_URL", OwnerType.MIS);
        l2.setAvAttributes(new AVAttributes(110000, AVFileFormat.H264));

        Program p = MediaBuilder.program().build();
        p.addLocation(l1);
        p.addLocation(l2);

        Assertions.assertThat(p.getLocations()).hasSize(1);
        Assertions.assertThat(p.getLocations().first().getBitrate()).isEqualTo(110000);
    }

    @Test
    public void testAddTwoLocationsWithSameAuthorityRecords() {
        Program program = new Program(1L);


        Location l1 = new Location("aaa", OwnerType.BROADCASTER);
        Location l2 = new Location("bbb", OwnerType.BROADCASTER);

        program.addLocation(l1);
        program.addLocation(l2);

        l1.setPlatform(Platform.INTERNETVOD);
        l2.setPlatform(Platform.INTERNETVOD);



        assertThat(program.getLocations()).hasSize(2);

        Prediction record = program.getPrediction(Platform.INTERNETVOD);
        assertThat(record).isNotNull();
        assertThat(record).isSameAs(program.getLocations().first().getAuthorityRecord());
        assertThat(record).isSameAs(program.getLocations().last().getAuthorityRecord());
    }


    @Test
    public void testAddLocationsOnlyUpdateCeresPredictions() {
        Location l1 = new Location("aaa", OwnerType.BROADCASTER);

        Program target = new Program(1L);


        target.addLocation(l1);

        Prediction plus = target.getPrediction(Platform.PLUSVOD);
        assertThat(plus).isNull();
    }

    @Test
    public void testAddLocationsOnlyUpdatePlatformPredictions() {
        Program target = new Program(1L);
        Location l1 = new Location("aaa", OwnerType.BROADCASTER);

        target.addLocation(l1);

        l1.setPlatform(Platform.PLUSVOD);

        Prediction plus = target.getPrediction(Platform.PLUSVOD);
        assertThat(plus.getPlatform()).isEqualTo(Platform.PLUSVOD); // used to be 'isNull' but I don't understand that.
    }

    @Test
    // MSE-2313
    public void testSilentlyFixStateOfPredictionIfLocationsAndOnlyAnnounced() {
        Location l1 = new Location("http://aaa.a/a", OwnerType.BROADCASTER);
        l1.setPlatform(Platform.INTERNETVOD);

        Program program = new Program(1L);
        program.setWorkflow(Workflow.PUBLISHED);


        program.addLocation(l1);

        program.setPredictions(Arrays.asList(new Prediction(Platform.INTERNETVOD, Prediction.State.ANNOUNCED)));

        assertThat(program.getPrediction(Platform.INTERNETVOD).getState()).isEqualTo(Prediction.State.REALIZED);
        assertThat(program.getWorkflow()).isEqualTo(Workflow.FOR_REPUBLICATION);

    }

    @Test
    // MSE-2313
    public void testDontSilentlyFixStateOfPredictionIfLocationsAndOnlyAnnounced() {
        Location l1 = new Location("http://aaa", OwnerType.BROADCASTER);
        l1.setPlatform(Platform.PLUSVOD);

        Program program = new Program(1L);


        program.addLocation(l1);

        program.setPredictions(Arrays.asList(new Prediction(Platform.INTERNETVOD, Prediction.State.ANNOUNCED)));

        assertThat(program.getPrediction(Platform.INTERNETVOD).getState()).isEqualTo(Prediction.State.ANNOUNCED);
    }


    @Test
    public void testAddLocationsOnPredictionUpdate() {
        Program target = new Program(1L);
        target.findOrCreatePrediction(Platform.PLUSVOD);

        Location l1 = new Location("aaa", OwnerType.BROADCASTER);

        target.addLocation(l1);

        l1.setPlatform(Platform.PLUSVOD);
        l1.setPublishStartInstant(Instant.ofEpochMilli(5));
        l1.setPublishStopInstant(Instant.ofEpochMilli(10));




        Prediction plus = target.getPrediction(Platform.PLUSVOD);
        assertThat(plus).isNotNull();
        assertThat(plus.getState()).isEqualTo(Prediction.State.REALIZED);
        assertThat(plus.getPublishStartInstant()).isEqualTo(Instant.ofEpochMilli(5));
        assertThat(plus.getPublishStopInstant()).isEqualTo(Instant.ofEpochMilli(10));
    }

    @Test
    public void testSortDateWithScheduleEvents() {
        final Program program = MediaBuilder.program()
            .creationInstant(Instant.ofEpochMilli(1))
            .publishStart(Instant.ofEpochMilli(2))
            .scheduleEvents(
                ScheduleEvent.builder().channel(Channel.NED2).start(Instant.ofEpochMilli(13)).duration(java.time.Duration.ofMillis(10)).rerun(true).build(),
                ScheduleEvent.builder().channel(Channel.NED1).start(Instant.ofEpochMilli(3)).duration(java.time.Duration.ofMillis(10)).build()
            )
            .build();

        assertThat(program.getSortInstant()).isEqualTo(Instant.ofEpochMilli(3));
    }

    @Test
    public void testSortDateWithPublishStart() {
        final Program program = MediaBuilder.program()
            .creationInstant(Instant.ofEpochMilli(1))
            .publishStart(Instant.ofEpochMilli(2))
            .build();

        assertThat(program.getSortInstant()).isEqualTo(Instant.ofEpochMilli(2));
    }

    @Test
    public void testSortDateWithCreationDate() {
        final Program program = MediaBuilder.program()
            .creationInstant(Instant.ofEpochMilli(1))
            .build();

        assertThat(program.getSortInstant()).isEqualTo(Instant.ofEpochMilli(1));
    }



    @Test
    public void testRealizePrediction() {
        final Program program = MediaBuilder.program()
            .id(1L)
            .build();


        final Location location1 = new Location("http://bla/1", OwnerType.BROADCASTER);
        location1.setPlatform(Platform.INTERNETVOD);
        program.addLocation(location1);

        program.realizePrediction(location1);

        final Location location2 = new Location("http://bla/2", OwnerType.BROADCASTER);
        location2.setPlatform(Platform.INTERNETVOD);
        program.addLocation(location2);




        assertThat(program.getPrediction(Platform.INTERNETVOD).getState()).isEqualTo(Prediction.State.REALIZED);
        assertThat(program.getPrediction(Platform.INTERNETVOD).getPublishStartInstant()).isNull();
        assertThat(program.getPrediction(Platform.INTERNETVOD).getPublishStopInstant()).isNull();


    }

    @Test
    public void testUnmarshal() {
        final Program program = MediaBuilder.program()
            .id(1L)
            .build();

        program.findOrCreatePrediction(Platform.INTERNETVOD)
            .setState(Prediction.State.ANNOUNCED);

        Program result = JAXBTestUtil.roundTrip(program);

        assertThat(result.getPrediction(Platform.INTERNETVOD)).isNull(); // it's not available.

        program.findOrCreatePrediction(Platform.INTERNETVOD).setPlannedAvailability(true);

        result = JAXBTestUtil.roundTrip(program);


        assertThat(result.getPrediction(Platform.INTERNETVOD)).isNotNull();
        assertThat(result.getPrediction(Platform.INTERNETVOD).getState()).isEqualTo(Prediction.State.ANNOUNCED);
        assertThat(result.getPrediction(Platform.INTERNETVOD).isPlannedAvailability()).isTrue();


    }

    @Test
    public void testHash() {
        final Program program = MediaBuilder.program()
            .lastModified(Instant.now())
            .creationInstant(Instant.ofEpochMilli(10000))
            .lastPublished(Instant.now())
            .id(1L)
            .build();
        program.acceptChanges();
        byte[] bytes = program.prepareForCRCCalc();
        assertThat(new String(bytes)).isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<program embeddable=\"true\" sortDate=\"1970-01-01T01:00:10+01:00\"  creationDate=\"1970-01-01T01:00:10+01:00\" urn=\"urn:vpro:media:program:1\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
                "    <credits/>\n" +
                "    <locations/>\n" +
                "    <scheduleEvents/>\n" +
                "    <images/>\n" +
                "    <segments/>\n" +
                "</program>\n");


        assertThat(program.getHash()).isEqualTo(2687135793L);
    }


    @Test
    public void testHasChanges() {
        final Program program = MediaBuilder.program()
            .lastModified(Instant.now())
            .lastPublished(Instant.now())
            .id(1L)
            .build();

        assertThat(program.hasChanges()).isTrue();
        program.acceptChanges();
        assertThat(program.hasChanges()).isFalse();
        program.setPublishStartInstant(Instant.now());
        assertThat(program.hasChanges()).isTrue();
        program.acceptChanges();
        assertThat(program.hasChanges()).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetWorkflowWhenMerged() {
        final Program merged = new Program();

        merged.setMergedTo(new Group());
        merged.setWorkflow(Workflow.PUBLISHED);
    }

    @Test
    public void testFindAncestry() {
        final Group grandParent = MediaBuilder.group().titles(new Title("Grand parent", OwnerType.BROADCASTER, TextualType.MAIN)).build();
        final Program parent = MediaBuilder.program().titles(new Title("Parent", OwnerType.BROADCASTER, TextualType.MAIN)).memberOf(grandParent, 1).build();
        final Program child = MediaBuilder.program().titles(new Title("Child", OwnerType.BROADCASTER, TextualType.MAIN)).memberOf(parent, 1).build();

        final List<MediaObject> ancestry = child.findAncestry(grandParent);
        assertThat(ancestry).hasSize(2);
        assertThat(ancestry.get(0)).isSameAs(grandParent);
        assertThat(ancestry.get(1)).isSameAs(parent);
    }

    @Test
    public void testAddImageWithMultipleOwners() {
        Image imgn1 = Image.builder().imageUri("urn:image:1").owner(NEBO).build();
        Image imgn2 = Image.builder().imageUri("urn:image:2").owner(NEBO).build();
        Image imgn3 = Image.builder().imageUri("urn:image:3").owner(NEBO).build();

        Image imgc1 = Image.builder().imageUri("urn:image:ceres1").owner(CERES).build();
        Image imgc2 = Image.builder().imageUri("urn:image:ceres2").owner(CERES).build();
        Image imgc3 = Image.builder().imageUri("urn:image:ceres3").owner(CERES).build();

        Program existing = MediaBuilder.program().images(
            imgn1, imgn2, //nebo
            imgc1, imgc2, imgc3 // ceres
        ).build();


        // incoming has ceres images too, this is odd, but they will be ignored
        Program incoming = MediaBuilder.program().images(
            imgn3, imgn1, imgn2, // nebo, added one and ordered
            imgc1, imgc3 // other images should be ignored
        ).build();

        existing.mergeImages(incoming, NEBO); // because we are nebo, and should not have shipped any thing different

        // arrived and in correct order
        assertEquals("urn:image:3", existing.getImages().get(0).getImageUri());
        assertEquals("urn:image:1", existing.getImages().get(1).getImageUri());
        assertEquals("urn:image:2", existing.getImages().get(2).getImageUri());

        // other images remain untouched and in same  same order
        assertEquals("urn:image:ceres1", existing.getImages().get(3).getImageUri());
        assertEquals("urn:image:ceres2", existing.getImages().get(4).getImageUri());
        assertEquals("urn:image:ceres3", existing.getImages().get(5).getImageUri());
    }


    @Test
    public void testMergeImagesChange() {
        Image existingImage1 = Image.builder().imageUri("urn:image:1").owner(BROADCASTER).build();
        Image existingImage2 = Image.builder().imageUri("urn:image:2").owner(BROADCASTER).build();
        Image existingImage3= Image.builder().imageUri("urn:image:ceres1").owner(CERES).build();


        Image incomingImage1 = Image.builder().imageUri("urn:image:1").owner(BROADCASTER).title("Updated title").build();
        Image incomingImage2 = Image.builder().imageUri("urn:image:2").owner(BROADCASTER).build();

        Program existing = MediaBuilder.program().images(
            existingImage1, existingImage2, existingImage3
        ).build();

        // incoming has ceres images too, this is odd, but they will be ignored
        Program incoming = MediaBuilder.program().images(
            incomingImage2, incomingImage1
        ).build();

        existing.mergeImages(incoming, BROADCASTER);

        // arrived and in correct order
        assertEquals("urn:image:2", existing.getImages().get(0).getImageUri());
        assertEquals("urn:image:1", existing.getImages().get(1).getImageUri());
        assertEquals("urn:image:ceres1", existing.getImages().get(2).getImageUri());

        // fields are updated too
        assertThat(existing.getImages().get(1).getTitle()).isEqualTo("Updated title");
    }


    @Test
    @Ignore("Fails, but I think it may have to be fixed?")
    public void testMergeImagesExistingForDifferentOwner() {
        Image existingImage1 = Image.builder().imageUri("urn:image:1").owner(BROADCASTER).title("broadcaster owner").build();
        Image existingImage2 = Image.builder().imageUri("urn:image:2").owner(RADIOBOX).title("radiobox owner").build();

        Image incomingImage1 = Image.builder().imageUri("urn:image:1").owner(RADIOBOX).title("broadcaster owner updated by radiobox").build();
        Image incomingImage2 = Image.builder().imageUri("urn:image:2").owner(BROADCASTER).title("radiobox owner updated by broadcaster").build();

        Program existing = MediaBuilder.program().images(
            existingImage1, existingImage2
        ).build();

        Program incoming = MediaBuilder.program().images(
            incomingImage2, incomingImage1
        ).build();

        existing.mergeImages(incoming, BROADCASTER);

        // arrived and in correct order
        assertEquals("urn:image:1", existing.getImages().get(0).getImageUri()); // FAILS, but I think it may be
        assertEquals("urn:image:2", existing.getImages().get(1).getImageUri());

        // fields are updated too
        assertThat(existing.getImages().get(1).getTitle()).isEqualTo("Updated title");
    }


    @Test
    public void addLocationToProgramWithSystemAuthorizedPrediction() {
        Program program = MediaBuilder.program().build();
        Prediction prediction = new Prediction(Platform.INTERNETVOD);
        prediction.setAuthority(Authority.SYSTEM);
        prediction.setPublishStartInstant(Instant.now());
        program.setPredictions(Arrays.asList(prediction));
        Location l1 = new Location("TEST_URL", OwnerType.AUTHORITY);
        l1.setPlatform(Platform.INTERNETVOD);
        program.addLocation(l1);
        assertNotNull(program.getLocation(l1));
    }


    @Test
    public void testMemberOf() {
        Group group = JAXB.unmarshal(new StringReader("<group xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" isOrdered=\"true\" type=\"SEASON\" avType=\"VIDEO\" embeddable=\"true\" mid=\"VPWON_1240914\" sortDate=\"2016-09-15T09:15:00+02:00\" workflow=\"PUBLISHED\" creationDate=\"2015-02-18T06:51:59.964+01:00\" lastModified=\"2016-12-21T11:20:37.369+01:00\" publishDate=\"2016-12-21T11:23:53.445+01:00\" urn=\"urn:vpro:media:group:51613423\">\n" +
            "<broadcaster id=\"VPRO\">VPRO</broadcaster>\n" +
            "<title owner=\"BROADCASTER\" type=\"MAIN\">VPRO Tegenlicht 2015 (HH)</title>\n" +
            "<title owner=\"MIS\" type=\"MAIN\">VPRO Tegenlicht</title>\n" +
            "<title owner=\"WHATS_ON\" type=\"MAIN\">VPRO Tegenlicht</title>\n" +
            "<title owner=\"BROADCASTER\" type=\"SUB\">Seizoen 2015 (HH)</title>\n" +
            "<description owner=\"MIS\" type=\"MAIN\">\n" +
            "VPRO Tegenlicht speurt in binnen- en buitenland naar ontwikkelingen in de politiek, economie, maatschappij en wetenschap die onze nabije toekomst zullen bepalen.\n" +
            "</description>\n" +
            "<releaseYear>2015</releaseYear>\n" +
            "<credits/>\n" +
            "<descendantOf urnRef=\"urn:vpro:media:group:45760423\" midRef=\"POMS_S_VPRO_652484\" type=\"COLLECTION\"/>\n" +
            "<descendantOf urnRef=\"urn:vpro:media:group:58901677\" midRef=\"POMS_S_VPRO_1405375\" type=\"COLLECTION\"/>\n" +
            "<memberOf added=\"2015-08-14T14:02:59.793+02:00\" highlighted=\"false\" midRef=\"POMS_S_VPRO_1405375\" index=\"5\" type=\"COLLECTION\" urnRef=\"urn:vpro:media:group:58901677\"/>\n" +
            "<locations/>\n" +
            "<scheduleEvents/>\n" +
            "<images/>\n" +
            "</group>"), Group.class);

        Group owner = new Group(0L);
        owner.setMid("POMS_S_VPRO_1405375");
        assertThat(group.isMemberOf(owner)).isTrue();

    }

}
