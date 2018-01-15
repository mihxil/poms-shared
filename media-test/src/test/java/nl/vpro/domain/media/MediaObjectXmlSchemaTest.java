/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.domain.media.update.ProgramUpdate;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.i18n.Locales;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.domain.media.MediaTestDataBuilder.program;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

/**
 * This class verifies JAXB XML output format and wether this format complies to the vproMedia.xsd schema definitions.
 * It's located here so it can use the test data builder for more concise code.
 */
@Slf4j
public class MediaObjectXmlSchemaTest {

    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance("nl.vpro.domain.media");
        } catch(JAXBException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Marshaller marshaller;

    static {
        try {
            marshaller = jaxbContext.createMarshaller();
        } catch(JAXBException e) {
            e.printStackTrace();
        }
    }

    public static Validator schemaValidator;

    static {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema;
        try {
            schema = factory.newSchema(new Source[]{
                new StreamSource(factory.getClass().getResourceAsStream("/nl/vpro/domain/media/w3/xml.xsd")),
                new StreamSource(factory.getClass().getResourceAsStream("/nl/vpro/domain/media/vproShared.xsd")),
                new StreamSource(factory.getClass().getResourceAsStream("/nl/vpro/domain/media/vproMedia.xsd"))}
            );
            schemaValidator = schema.newValidator();
        } catch(SAXException e) {
            e.printStackTrace();
        }

        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());
    }

    @Before
    public void init() {
        Locale.setDefault(Locales.DUTCH);
    }

    @Test
    public void testMid() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program mid=\"MID_000001\" embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><credits/><locations/><scheduleEvents/><images/><segments/></program>";

        Program program = program().lean().mid("MID_000001").build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testAvailableSubtitles() throws Exception {
        String expected = "<program embeddable=\"true\" hasSubtitles=\"true\" mid=\"MID_000001\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <availableSubtitles language=\"nl\" type=\"CAPTION\"/>\n" +
            "    <availableSubtitles language=\"nl\" type=\"TRANSLATION\"/>\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";

        Program program = program().lean().mid("MID_000001").build();
    	program.getAvailableSubtitles().add(new AvailableSubtitles(Locales.DUTCH,
            SubtitlesType.CAPTION));
    	program.getAvailableSubtitles().add(new AvailableSubtitles(Locales.DUTCH,
            SubtitlesType.TRANSLATION));


    	JAXBTestUtil.roundTripAndSimilar(program, expected);


    }

    @Test
    public void testMidSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withMid().build()));
    }

    @Test
    public void testHasSubtitles() throws Exception {
        String expected = "<program embeddable=\"true\" hasSubtitles=\"true\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <availableSubtitles language=\"nl\" type=\"CAPTION\"/>\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>\n";

        Program program = program().lean().withSubtitles().build();


        Program rounded = JAXBTestUtil.roundTripAndSimilar(program, expected);

        assertThat(rounded.hasSubtitles()).isTrue();

    }

    @Test
    public void testHasSubtitlesSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withSubtitles().build()));
    }

    @Test
    public void testDatesCreatedAndModified() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" lastModified=\"1970-01-01T03:00:00+01:00\" creationDate=\"1970-01-01T01:00:00+01:00\" sortDate=\"1970-01-01T01:00:00+01:00\"  xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"><credits/><locations/><scheduleEvents/><images/><segments/></program>";

        Program program = program().lean().creationInstant(Instant.EPOCH).lastModified(Instant.ofEpochMilli(2 * 60 * 60 * 1000)).build();
        String actual = toXml(program);
        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testCreatedAndModifiedBy() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><credits/><locations/><scheduleEvents/><images /><segments/></program>";

        Program program = program().lean().withCreatedBy().withLastModifiedBy().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testCreatedAndModifiedBySchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withCreatedBy().withLastModifiedBy().build()));
    }

    @Test
    public void testPublishStartStop() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" publishStop=\"1970-01-01T03:00:00+01:00\" publishStart=\"1970-01-01T01:00:00+01:00\" sortDate=\"1970-01-01T01:00:00+01:00\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><credits/><locations/><scheduleEvents/><images/><segments/></program>";

        Program program = program().lean().publishStart(Instant.EPOCH).publishStop(Instant.ofEpochMilli(2 * 60 * 60 * 1000)).build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testPublishStartStopSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withPublishStart().withPublishStop().build()));
    }

    @Test
    public void testCrids() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><crid>crid://bds.tv/9876</crid><crid>crid://tmp.fragment.mmbase.vpro.nl/1234</crid><credits/><locations/><scheduleEvents/><images/><segments/></program>";

        Program program = program().lean().withCrids().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testCridsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withCrids().build()));
    }

    @Test
    public void testBroadcasters() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\"  xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><broadcaster id=\"BNN\">BNN</broadcaster><broadcaster id=\"AVRO\">AVRO</broadcaster><credits/><locations/><scheduleEvents/><images/><segments/></program>";

        Program program = program().lean().withBroadcasters().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testExclusives() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><exclusive portalId=\"STERREN24\"/><exclusive portalId=\"3VOOR12_GRONINGEN\" stop=\"1970-01-01T01:01:40+01:00\" start=\"1970-01-01T01:00:00+01:00\"/><credits/><locations/><scheduleEvents/><images/><segments/></program>";

        Program program = program().lean().withPortalRestrictions().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testExclusivesSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withPortalRestrictions().build()));
    }

    @Test
    public void testRegions() throws Exception {


        JAXBTestUtil.roundTripAndSimilar(program().lean().withGeoRestrictions().build(),
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <region regionId=\"NL\" platform=\"INTERNETVOD\"/>\n" +
            "    <region regionId=\"BENELUX\" platform=\"INTERNETVOD\" start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\"/>\n" +
            "    <region regionId=\"NL\" platform=\"TVVOD\" start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\"/>\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>\n");
    }

    @Test
    public void testRegionsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withGeoRestrictions().build()));
    }

    @Test
    public void testDuration() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"><duration>P0DT2H0M0.000S</duration><credits/><locations/><scheduleEvents/><images/><segments/></program>";

        Program program = program().lean().withDuration().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testDurationSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withDuration().build()));
    }

    @Test
    public void testPredictions() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\"  xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"><credits/><prediction state=\"REVOKED\">INTERNETVOD</prediction><locations/><scheduleEvents/><images/><segments/></program>";

        Program program = program().lean().build();

        Prediction prediction = new Prediction(Platform.INTERNETVOD);
        prediction.setState(Prediction.State.REVOKED);
        prediction.setIssueDate(Instant.EPOCH);

        program.getPredictions().add(prediction);
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testPredictionsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withPredictions().build()));
    }

    @Test
    public void testTitles() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\"  xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><title type=\"MAIN\" owner=\"BROADCASTER\">Main title</title><title type=\"MAIN\" owner=\"MIS\">Main title MIS</title><title type=\"SHORT\" owner=\"BROADCASTER\">Short title</title><title type=\"SUB\" owner=\"MIS\">Episode title MIS</title><credits/><locations/><scheduleEvents/><images/><segments/></program>";

        Program program = program().lean().withTitles().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testDescriptions() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><description type=\"MAIN\" owner=\"BROADCASTER\">Main description</description><description type=\"MAIN\" owner=\"MIS\">Main description MIS</description><description type=\"SHORT\" owner=\"BROADCASTER\">Short description</description><description type=\"EPISODE\" owner=\"MIS\">Episode description MIS</description><credits/><locations/><scheduleEvents/><images/><segments/></program>";

        Program program = program().lean().withDescriptions().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testDescriptionsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withDescriptions().build()));
    }

    @Test
    public void testGenres() throws Exception {
        Program program = program().withGenres().build();

        Program result = JAXBTestUtil.roundTrip(program, "<genre id=\"3.0.1.7.21\">\n" +
            "        <term>Informatief</term>\n" +
            "        <term>Nieuws/actualiteiten</term>\n" +
            "    </genre>\n" +
            "    <genre id=\"3.0.1.8.25\">\n" +
            "        <term>Documentaire</term>\n" +
            "        <term>Natuur</term>\n" +
            "    </genre>\n" +
            "    <credits/>");

        assertThat(result.getGenres()).hasSize(2);
    }

    @Test
    public void testGenresSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withGenres().build()));
    }

    @Test
    public void testAgeRating() throws Exception {
        Program program = program().withAgeRating().build();

        Program result = JAXBTestUtil.roundTrip(program, "<ageRating>12</ageRating>");

        assertThat(result.getAgeRating()).isNotNull();
    }

    @Test
    public void testAgeRatingSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withAgeRating().build()));
    }

    @Test
    public void testContentRating() throws Exception {
        Program program = program().withContentRating().build();

        Program result = JAXBTestUtil.roundTrip(program, "<contentRating>ANGST</contentRating>\n" +
            "    <contentRating>DRUGS_EN_ALCOHOL</contentRating>");

        assertThat(result.getContentRatings()).hasSize(2);
    }

    @Test
    public void testContentRatingSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withContentRating().build()));
    }

    @Test
    public void testTags() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\"  xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><tag>tag1</tag><tag>tag2</tag><tag>tag3</tag><credits/><locations/><scheduleEvents/><images/><segments/></program>";

        Program program = program().lean().withTags().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testTagsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withTags().build()));
    }

    @Test
    public void testPortals() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><portal id=\"3VOOR12_GRONINGEN\">3voor12 Groningen</portal><portal id=\"STERREN24\">Sterren24</portal><credits/><locations/><scheduleEvents/><images/><segments/></program>";

        Program program = program().lean().withPortals().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testPortalsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withPortals().build()));
    }

    @Test
    public void testMemberOfAndDescendantOfGraph() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"><credits/><descendantOf urnRef=\"urn:vpro:media:group:100\" midRef=\"AVRO_5555555\" type=\"SERIES\"/><descendantOf urnRef=\"urn:vpro:media:group:200\" midRef=\"AVRO_7777777\" type=\"SEASON\"/><memberOf added=\"1970-01-01T01:00:00+01:00\" highlighted=\"false\" midRef=\"AVRO_7777777\" index=\"1\" type=\"SEASON\" urnRef=\"urn:vpro:media:group:200\"/><locations/><scheduleEvents/><images/><segments/></program>";

        Program program = program().lean().withMemberOf().build();
        /* Set MID to null first, then set it to the required MID; otherwise an IllegalArgumentException will be thrown setting the MID to another value */
        program.getMemberOf().first().getOwner().setMid(null);
        program.getMemberOf().first().getOwner().setMid("AVRO_7777777");
        program.getMemberOf().first().getOwner().getMemberOf().first().getOwner().setMid(null);
        program.getMemberOf().first().getOwner().getMemberOf().first().getOwner().setMid("AVRO_5555555");
        program.getMemberOf().first().setAdded(Instant.EPOCH);
        String actual = toXml(program);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testEpisodeOfAndDescendantOfGraph() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program type=\"BROADCAST\" embeddable=\"true\" urn=\"urn:vpro:media:program:100\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"><credits/><descendantOf urnRef=\"urn:vpro:media:group:101\" midRef=\"AVRO_5555555\" type=\"SERIES\"/><descendantOf urnRef=\"urn:vpro:media:group:102\" midRef=\"AVRO_7777777\" type=\"SEASON\"/><locations/><scheduleEvents/><images/><episodeOf added=\"1970-01-01T01:00:00+01:00\" highlighted=\"false\" midRef=\"AVRO_7777777\" index=\"1\" type=\"SEASON\" urnRef=\"urn:vpro:media:group:102\"/><segments/></program>";

        Program program = program().id(100L).lean().type(ProgramType.BROADCAST).withEpisodeOf(101L, 102L).build();
        program.getEpisodeOf().first().setAdded(Instant.EPOCH);
        /* Set MID to null first, then set it to the required MID; otherwise an IllegalArgumentException will be thrown setting the MID to another value */
        program.getEpisodeOf().first().getOwner().setMid(null);
        program.getEpisodeOf().first().getOwner().setMid("AVRO_7777777");
        program.getEpisodeOf().first().getOwner().getMemberOf().first().getOwner().setMid(null);
        program.getEpisodeOf().first().getOwner().getMemberOf().first().getOwner().setMid("AVRO_5555555");
        String actual = toXml(program);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testRelations() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" sortDate=\"1970-01-01T01:00:00+01:00\" creationDate=\"1970-01-01T01:00:00+01:00\" urn=\"urn:vpro:media:program:100\" workflow=\"PUBLISHED\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <relation broadcaster=\"AVRO\" type=\"THESAURUS\">synoniem</relation>\n" +
            "    <relation broadcaster=\"EO\" type=\"KOOR\">Ulfts Mannenkoor</relation>\n" +
            "    <relation broadcaster=\"VPRO\" type=\"ARTIST\">Marco Borsato</relation>\n" +
            "    <relation uriRef=\"http://www.bluenote.com/\" broadcaster=\"VPRO\" type=\"LABEL\">Blue Note</relation>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";

        Program program = program().id(100L).lean().creationDate(Instant.EPOCH).workflow(Workflow.PUBLISHED).withRelations().build();


        JAXBTestUtil.roundTripAndSimilar(program, expected);
    }


    @Test
    public void testRelationsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withRelations().build()));
    }

    @Test
    public void testScheduleEvents() throws Exception {

        Program program = program().id(100L).lean().withScheduleEvents().build();
        String actual = toXml(program);

        assertThat(actual).isXmlEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<program embeddable=\"true\" sortDate=\"1970-01-01T01:00:00.100+01:00\"\n" +
            "    urn=\"urn:vpro:media:program:100\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents>\n" +
            "        <scheduleEvent channel=\"NED3\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "            <guideDay>1969-12-31+01:00</guideDay>\n" +
            "            <start>1970-01-01T01:00:00.100+01:00</start>\n" +
            "            <duration>P0DT0H0M0.200S</duration>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"NED3\" net=\"ZAPP\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "            <repeat isRerun=\"true\"/>\n" +
            "            <guideDay>1970-01-03+01:00</guideDay>\n" +
            "            <start>1970-01-04T01:00:00.300+01:00</start>\n" +
            "            <duration>P0DT0H0M0.050S</duration>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"HOLL\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "            <repeat isRerun=\"true\"/>\n" +
            "            <guideDay>1970-01-08+01:00</guideDay>\n" +
            "            <start>1970-01-09T01:00:00.350+01:00</start>\n" +
            "            <duration>P0DT0H0M0.250S</duration>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"CONS\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "            <repeat isRerun=\"true\"/>\n" +
            "            <guideDay>1970-01-10+01:00</guideDay>\n" +
            "            <start>1970-01-11T01:00:00.600+01:00</start>\n" +
            "            <duration>P0DT0H0M0.200S</duration>\n" +
            "        </scheduleEvent>\n" +
            "    </scheduleEvents>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>");
    }


    @Test
    public void testScheduleEventsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withScheduleEvents().build()));
    }

    @Test
    public void testScheduleEventsWithNet() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" sortDate=\"1970-01-01T01:00:00+01:00\" urn=\"urn:vpro:media:program:100\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents>\n" +
            "        <scheduleEvent channel=\"NED1\" midRef=\"VPRO_123456\" net=\"ZAPP\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "            <guideDay>1970-01-01+01:00</guideDay>\n" +
            "            <start>1970-01-01T01:00:00+01:00</start>\n" +
            "            <duration>P0DT0H1M40.000S</duration>\n" +
            "            <poProgID>VPRO_123456</poProgID>\n" +
            "            <primaryLifestyle>Onbezorgde Trendbewusten</primaryLifestyle>\n" +
            "            <secondaryLifestyle>Zorgzame Duizendpoten</secondaryLifestyle>\n" +
            "        </scheduleEvent>\n" +
            "    </scheduleEvents>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";

        ScheduleEvent event = new ScheduleEvent(Channel.NED1, Instant.EPOCH, java.time.Duration.ofSeconds((100)));
        event.setGuideDate(LocalDate.ofEpochDay(0));
        event.setNet(new Net("ZAPP", "Zapp"));
        event.setPoProgID("VPRO_123456");
        event.setPrimaryLifestyle(new Lifestyle("Onbezorgde Trendbewusten"));
        event.setSecondaryLifestyle(new SecondaryLifestyle("Zorgzame Duizendpoten"));

        Program program = program().lean().id(100L).scheduleEvents(event).build();

        JAXBTestUtil.roundTripAndSimilar(program, expected);

        JAXB.unmarshal(new StringReader(expected), Program.class);
    }

    @Test
    public void testScheduleEventsWithNetSchema() throws Exception {
        ScheduleEvent event = new ScheduleEvent(Channel.NED1, Instant.EPOCH,
            java.time.Duration.ofSeconds(100));
        event.setGuideDate(LocalDate.of(1970, 1, 1));
        event.setNet(new Net("ZAPP", "Zapp"));
        event.setPoProgID("VPRO_123456");

        Program program = program().constrained().scheduleEvents(event).build();

        schemaValidator.validate(new JAXBSource(marshaller, program));
    }

    @Test
    @Ignore("Used to generate an example XML document")
    public void generateExample() throws Exception {
        Segment segment = MediaTestDataBuilder
            .segment()
            .withPublishStart()
            .withPublishStop()
            .duration(java.time.Duration.ofSeconds(100))
            .start(java.time.Duration.ofSeconds(5000))
            .withImages()
            .withTitles()
            .withDescriptions()
            .build();

        MediaTestDataBuilder.ProgramTestDataBuilder testBuilder = MediaTestDataBuilder
            .program()
            .type(ProgramType.BROADCAST)
            .withPublishStart()
            .withPublishStop()
            .withCrids()
            .withBroadcasters()
            .withTitles()
            .withDescriptions()
            .withDuration()
            .withMemberOf()
            .withEmail()
            .withWebsites()
            .withLocations()
            .withScheduleEvents()
            .withRelations()
            .withImages()
            .withEpisodeOf()
            .segments(segment)
            .withSegments();

        ProgramUpdate example = ProgramUpdate.create(testBuilder.build());

        System.out.println(toXml(example));
    }

    @Test
    public void testSchedule() throws Exception {

        Schedule schedule = new Schedule(Channel.NED1, new Date(0), new Date(350 + 8 * 24 * 3600 * 1000));
        Program program = program().id(100L).lean().withScheduleEvents().build();
        schedule.addScheduleEventsFromMedia(Arrays.asList(program));

        String actual = toXml(schedule);

        assertThat(actual).isXmlEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<schedule channel=\"NED1\" start=\"1970-01-01T01:00:00+01:00\"\n" +
            "    stop=\"1970-01-11T01:00:00.800+01:00\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <scheduleEvent channel=\"NED3\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "        <guideDay>1969-12-31+01:00</guideDay>\n" +
            "        <start>1970-01-01T01:00:00.100+01:00</start>\n" +
            "        <duration>P0DT0H0M0.200S</duration>\n" +
            "    </scheduleEvent>\n" +
            "    <scheduleEvent channel=\"NED3\" net=\"ZAPP\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "        <repeat isRerun=\"true\"/>\n" +
            "        <guideDay>1970-01-03+01:00</guideDay>\n" +
            "        <start>1970-01-04T01:00:00.300+01:00</start>\n" +
            "        <duration>P0DT0H0M0.050S</duration>\n" +
            "    </scheduleEvent>\n" +
            "    <scheduleEvent channel=\"HOLL\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "        <repeat isRerun=\"true\"/>\n" +
            "        <guideDay>1970-01-08+01:00</guideDay>\n" +
            "        <start>1970-01-09T01:00:00.350+01:00</start>\n" +
            "        <duration>P0DT0H0M0.250S</duration>\n" +
            "    </scheduleEvent>\n" +
            "    <scheduleEvent channel=\"CONS\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "        <repeat isRerun=\"true\"/>\n" +
            "        <guideDay>1970-01-10+01:00</guideDay>\n" +
            "        <start>1970-01-11T01:00:00.600+01:00</start>\n" +
            "        <duration>P0DT0H0M0.200S</duration>\n" +
            "    </scheduleEvent>\n" +
            "</schedule>");


        Schedule unmarshalled = JAXB.unmarshal(new StringReader(actual), Schedule.class);
        assertThat(unmarshalled.getNet()).isNull();
    }


    @Test
    public void testScheduleWithFilter() throws Exception {

        Schedule schedule = new Schedule(Channel.NED3, new Date(0), new Date(350 + 8 * 24 * 3600 * 1000));
        schedule.setFiltered(true);
        Program program = program().id(100L).lean().withScheduleEvents().build();
        schedule.addScheduleEventsFromMedia(Arrays.asList(program));

        String actual = toXml(schedule);

        assertThat(actual).isXmlEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<schedule channel=\"NED3\" start=\"1970-01-01T01:00:00+01:00\"\n" +
            "    stop=\"1970-01-09T01:00:00.350+01:00\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <scheduleEvent channel=\"NED3\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "        <guideDay>1969-12-31+01:00</guideDay>\n" +
            "        <start>1970-01-01T01:00:00.100+01:00</start>\n" +
            "        <duration>P0DT0H0M0.200S</duration>\n" +
            "    </scheduleEvent>\n" +
            "    <scheduleEvent channel=\"NED3\" net=\"ZAPP\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "        <repeat isRerun=\"true\"/>\n" +
            "        <guideDay>1970-01-03+01:00</guideDay>\n" +
            "        <start>1970-01-04T01:00:00.300+01:00</start>\n" +
            "        <duration>P0DT0H0M0.050S</duration>\n" +
            "    </scheduleEvent>\n" +
            "</schedule>\n" +
            "");
    }

    @Test
    public void testScheduleWithNetFilter() throws Exception {


        Schedule schedule = Schedule.builder()
            .net(new Net("ZAPP"))
            .start(Instant.EPOCH)
            .stop(Instant.EPOCH.plus(Duration.ofDays(8).plusMillis(350)))
            .filtered(true)
            .build();

        Program program = program().id(100L).lean().withScheduleEvents().build();
        schedule.addScheduleEventsFromMedia(Arrays.asList(program));

        assertThat(toXml(schedule)).isXmlEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<schedule net=\"ZAPP\" start=\"1970-01-01T01:00:00+01:00\"\n" +
            "    stop=\"1970-01-09T01:00:00.350+01:00\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <scheduleEvent channel=\"NED3\" net=\"ZAPP\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "        <repeat isRerun=\"true\"/>\n" +
            "        <guideDay>1970-01-03+01:00</guideDay>\n" +
            "        <start>1970-01-04T01:00:00.300+01:00</start>\n" +
            "        <duration>P0DT0H0M0.050S</duration>\n" +
            "    </scheduleEvent>\n" +
            "</schedule>");
    }

    @Test
    public void testCountries() throws Exception {
        Program program = program().withCountries().build();

        Program result = JAXBTestUtil.roundTrip(program, "<country code=\"GB\">Verenigd Koninkrijk</country>");

        assertThat(result.getCountries()).hasSize(2);
    }


    @Test
    public void testCountriesSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withCountries().build()));
    }

    @Test
    public void testLanguages() throws Exception {
        Program program = program().withLanguages().build();

        Program result = JAXBTestUtil.roundTrip(program, "<language code=\"nl\">Nederlands</language>");

        assertThat(result.getLanguages()).hasSize(2);
    }


    @Test
    public void testLanguagesSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withLanguages().build()));
    }

    @Test
    public void testTwitter() throws JAXBException, IOException, SAXException {
        Program program = program().constrained().build();
        program.setTwitterRefs(Arrays.asList(new TwitterRef("@vpro"), new TwitterRef("#vpro")));
        StringWriter writer = new StringWriter();
        JAXB.marshal(program, writer);
        program = JAXB.unmarshal(new StringReader(writer.toString()), Program.class);
        assertThat(program.getTwitterRefs()).containsExactly(new TwitterRef("@vpro"), new TwitterRef("#vpro"));
        schemaValidator.validate(new JAXBSource(marshaller, program));
    }

    @Test
    public void testWithLocations() throws Exception {
        String expected = "<program embeddable=\"true\" urn=\"urn:vpro:media:program:100\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <credits/>\n" +
            "    <locations>\n" +
            "        <location owner=\"BROADCASTER\" creationDate=\"2016-03-04T15:45:00+01:00\" workflow=\"FOR PUBLICATION\">\n" +
            "            <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <avFileFormat>MP4</avFileFormat>\n" +
            "            </avAttributes>\n" +
            "            <offset>P0DT0H13M0.000S</offset>\n" +
            "            <duration>P0DT0H10M0.000S</duration>\n" +
            "        </location>\n" +
            "        <location owner=\"BROADCASTER\" creationDate=\"2016-03-04T14:45:00+01:00\" workflow=\"FOR PUBLICATION\">\n" +
            "            <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <avFileFormat>WM</avFileFormat>\n" +
            "            </avAttributes>\n" +
            "        </location>\n" +
            "        <location owner=\"BROADCASTER\" creationDate=\"2016-03-04T13:45:00+01:00\" workflow=\"FOR PUBLICATION\">\n" +
            "            <programUrl>http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <avFileFormat>WM</avFileFormat>\n" +
            "            </avAttributes>\n" +
            "            <duration>P0DT0H30M33.000S</duration>\n" +
            "        </location>\n" +
            "        <location owner=\"NEBO\" creationDate=\"2016-03-04T12:45:00+01:00\" workflow=\"FOR PUBLICATION\">\n" +
            "            <programUrl>http://player.omroep.nl/?aflID=4393288</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <avFileFormat>HTML</avFileFormat>\n" +
            "            </avAttributes>\n" +
            "        </location>\n" +
            "    </locations>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";

        Program program = program().id(100L).lean().withLocations().build();

        JAXBTestUtil.roundTripAndSimilar(program, expected);

    }

    @Test
    public void testWithLocationWithUnknownOwner() throws Exception {
        String example = "<program embeddable=\"true\" hasSubtitles=\"false\" urn=\"urn:vpro:media:program:100\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <locations>\n" +
            "        <location owner=\"UNKNOWN\" creationDate=\"2016-03-04T15:45:00+01:00\" workflow=\"FOR PUBLICATION\">\n" +
            "            <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <avFileFormat>MP4</avFileFormat>\n" +
            "            </avAttributes>\n" +
            "            <offset>P0DT0H13M0.000S</offset>\n" +
            "            <duration>P0DT0H10M0.000S</duration>\n" +
            "        </location>\n" +
            "    </locations>\n" +
            "</program>";



        Program program = JAXBTestUtil.unmarshal(example, Program.class);
        assertThat(program.getLocations().first().getOwner()).isNull();
    }

        @Test
    public void testWithDescendantOf() throws IOException, SAXException {

        Program program = program().lean().withDescendantOf().build();
        JAXBTestUtil.roundTripAndSimilar(program, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <credits/>\n" +
            "    <descendantOf midRef=\"MID_123456\" type=\"SEASON\"/>\n" +
            "    <descendantOf urnRef=\"urn:vpro:media:group:2\" type=\"SERIES\"/>\n" +
            "    <descendantOf urnRef=\"urn:vpro:media:program:1\" type=\"BROADCAST\"/>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>");


    }







    protected String toXml(Object o) throws JAXBException {
        Writer writer = new StringWriter();
        marshaller.marshal(o, writer);
        return writer.toString();
    }
}
