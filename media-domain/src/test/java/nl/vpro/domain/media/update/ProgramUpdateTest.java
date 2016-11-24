/**
 /**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Title;
import nl.vpro.domain.user.Portal;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.validation.WarningValidatorGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class ProgramUpdateTest extends MediaUpdateTest {

    @Test
    public void testIsValidWhenInvalid() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        assertThat(update.isValid()).isFalse();
    }

    @Test
    public void testErrorsWhenInvalid() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.MAIN))));
        assertThat(update.violations()).hasSize(2);
    }

    @Test
    public void testCridValidation() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.MAIN))));
        update.setAVType(AVType.AUDIO);
        update.setType(ProgramType.BROADCAST);
        update.setCrids(Collections.singletonList("crids://aa"));
        System.out.println(update.violationMessage());
        assertThat(update.violations()).hasSize(1);
    }

    @Test
    public void testIsValidForImages() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.MAIN))));
        update.setType(ProgramType.CLIP);
        update.setAVType(AVType.AUDIO);
        update.setImages(Collections.singletonList(new ImageUpdate(ImageType.BACKGROUND, "Title", "Description", new ImageLocation(null))));
        Set<ConstraintViolation<MediaUpdate<Program>>> errors = update.violations();
        assertThat(errors).hasSize(1);
    }

    @Test
    public void testFetchForOwner() throws Exception {
        SegmentUpdate segment = SegmentUpdate.create();
        segment.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.MAIN))));

        ProgramUpdate program = ProgramUpdate.create();
        program.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.EPISODE))));

        program.setSegments(new TreeSet<>(Collections.singletonList(segment)));

        Program result = program.fetch(OwnerType.MIS);

        assertThat(result.getTitles().first().getOwner()).isEqualTo(OwnerType.MIS);
        assertThat(result.getSegments().first().getTitles().first().getOwner()).isEqualTo(OwnerType.MIS);
    }

    @Test
    public void testGetAVType() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setAVType(AVType.MIXED);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" avType=\"MIXED\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\"><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetEmbeddable() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setEmbeddable(false);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"false\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetPublishStart() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setPublishStart(new Date(4444));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program publishStart=\"1970-01-01T01:00:04.444+01:00\" embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetPublishStop() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setPublishStop(new Date(4444));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program publishStop=\"1970-01-01T01:00:04.444+01:00\" embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetCrids() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setCrids(Collections.singletonList("crid://bds.tv/23678459"));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><crid>crid://bds.tv/23678459</crid><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetBroadcasters() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setBroadcasters(Collections.singletonList("MAX"));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><broadcaster>MAX</broadcaster><locations/><scheduleEvents/><images/><segments/></program>";

        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(update, expected);
        rounded.getBroadcasters().add("VPRO");
        assertThat(rounded.fetch().getBroadcasters()).hasSize(2);
        rounded.getBroadcasters().remove(0);
        assertThat(rounded.fetch().getBroadcasters()).hasSize(1);
        assertThat(rounded.fetch().getBroadcasters().get(0).getId()).isEqualTo("VPRO");
        rounded.setBroadcasters(Arrays.asList("EO"));
        assertThat(rounded.fetch().getBroadcasters().get(0).getId()).isEqualTo("EO");


    }

    @Test
    public void testGetPortalRestrictions() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setPortalRestrictions(Arrays.asList(new PortalRestrictionUpdate(new PortalRestriction(new Portal("3VOOR12_GRONINGEN", "3voor12 Groningen"))), new PortalRestrictionUpdate(new PortalRestriction(new Portal("STERREN24", "Sterren24"), new Date(0), new Date(1000000)))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><exclusive>3VOOR12_GRONINGEN</exclusive><exclusive stop=\"1970-01-01T01:16:40+01:00\" start=\"1970-01-01T01:00:00+01:00\">STERREN24</exclusive><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetGeoRestrictions() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setGeoRestrictions(Arrays.asList(new GeoRestrictionUpdate(new GeoRestriction(Region.BENELUX)), new GeoRestrictionUpdate(new GeoRestriction(Region.NL, new Date(0), new Date(1000000)))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><region>BENELUX</region><region stop=\"1970-01-01T01:16:40+01:00\" start=\"1970-01-01T01:00:00+01:00\">NL</region><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetGeoRestrictionsReverse() throws Exception {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><region>BENELUX</region><region stop=\"1970-01-01T01:16:40+01:00\" start=\"1970-01-01T01:00:00+01:00\">NL</region><locations/><scheduleEvents/><images/><segments/></program>";
        ProgramUpdate update = JAXB.unmarshal(new StringReader(input), ProgramUpdate.class);

        assertThat(update.fetch().getGeoRestrictions()).isNotEmpty();
    }

    @Test
    public void testGetTitles() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("Hoofdtitel", TextualType.MAIN))));

         String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><title type=\"MAIN\">Hoofdtitel</title><locations/><scheduleEvents/><images/><segments/></program>";
        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(update, expected);
        assertThat(rounded.getTitles()).hasSize(1);
        assertThat(rounded.fetch().getTitles()).hasSize(1);
        assertThat(rounded.fetch().getTitles().first().getTitle()).isEqualTo("Hoofdtitel");


    }

    @Test
    public void testGetTitlesWitOwner() throws Exception {
        ProgramUpdate program = ProgramUpdate.create(MediaBuilder.program().titles(
            new Title("hoofdtitel omroep", OwnerType.BROADCASTER, TextualType.MAIN),
            new Title("hoofdtitel mis", OwnerType.MIS, TextualType.MAIN)).build());

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><title type=\"MAIN\">hoofdtitel omroep</title><locations/><scheduleEvents/><images/><segments/></program>";
        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(program, expected);
        assertThat(rounded.getTitles()).hasSize(1);
        assertThat(rounded.fetch().getTitles()).hasSize(1);
        assertThat(rounded.fetch().getTitles().first().getTitle()).isEqualTo("hoofdtitel omroep");

    }

    @Test
    public void testGetDescriptions() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setDescriptions(new TreeSet<>(Collections.singletonList(new DescriptionUpdate("Beschrijving", TextualType.MAIN))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><description type=\"MAIN\">Beschrijving</description><locations/><scheduleEvents/><images/><segments/></program>";
        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetAVAttributes() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setAvAttributes(avAttributes());

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <avAttributes>\n" +
            "        <bitrate>1000</bitrate>\n" +
            "        <avFileFormat>H264</avFileFormat>\n" +
            "        <videoAttributes width=\"320\" height=\"180\">\n" +
            "            <aspectRatio>16:9</aspectRatio>\n" +
            "        </videoAttributes>\n" +
            "        <audioAttributes>\n" +
            "            <channels>2</channels>\n" +
            "            <coding>AAC</coding>\n" +
            "        </audioAttributes>\n" +
            "    </avAttributes>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>\n";
        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetDuration() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setDuration(new Date(656565));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><duration>P0DT0H10M56.565S</duration><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetDuration2() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setDuration(new Date(1000L * (3 * 3600 + 46 * 60)));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><duration>P0DT3H46M0.000S</duration><locations/><scheduleEvents/><images/><segments/></program>";
        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetMemberOf() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setMemberOf(new TreeSet<>(Collections.singletonList(new MemberRefUpdate(20, "urn:vpro:media:group:864"))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><memberOf highlighted=\"false\" position=\"20\">urn:vpro:media:group:864</memberOf><locations/><scheduleEvents/><images/><segments/></program>";

        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(update, expected);
        assertThat(rounded.fetch().getMemberOf()).hasSize(1);

        rounded.getMemberOf().add(new MemberRefUpdate(2, "MID_123"));
        assertThat(rounded.fetch().getMemberOf()).hasSize(2);

        MemberRefUpdate first = rounded.getMemberOf().first();
        rounded.getMemberOf().remove(first);
        assertThat(rounded.fetch().getMemberOf()).hasSize(1);
        assertThat(rounded.fetch().getMemberOf().first().getMediaRef()).isEqualTo("urn:vpro:media:group:864");
        rounded.setMemberOf(new TreeSet<>(Arrays.asList(new MemberRefUpdate(3, "MID_12356"))));
        assertThat(rounded.fetch().getMemberOf().first().getMediaRef()).isEqualTo("MID_12356");


    }

    @Test
    public void testGetEmail() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setEmail(Collections.singletonList("info@vpro.nl"));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><email>info@vpro.nl</email><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetWebsites() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setWebsiteObjects(Collections.singletonList(new Website("www.vpro.nl")));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><website>www.vpro.nl</website><locations/><scheduleEvents/><images/><segments/></program>";

        ProgramUpdate found = JAXBTestUtil.roundTripAndSimilar(update, expected);
        found.getWebsites().add("http://www.npo.nl");
        assertThat(found.fetch().getWebsites()).hasSize(2);
        assertThat(found.fetch().getWebsites().get(1).getUrl()).isEqualTo("http://www.npo.nl");


    }

    @Test
    public void testGetLocations() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setLocations(new TreeSet<>(Collections.singletonList(new LocationUpdate("rtsp:someurl", new Date(100000), 320, 180, 1000000, AVFileFormat.M4V))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <locations>\n" +
            "        <location>\n" +
            "            <programUrl>rtsp:someurl</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <bitrate>1000000</bitrate>\n" +
            "                <avFileFormat>M4V</avFileFormat>\n" +
            "                <videoAttributes width=\"320\" height=\"180\">\n" +
            "                    <aspectRatio>16:9</aspectRatio>\n" +
            "                </videoAttributes>\n" +
            "            </avAttributes>\n" +
            "            <duration>P0DT0H1M40.000S</duration>\n" +
            "        </location>\n" +
            "    </locations>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetPerson() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setPersons(Collections.singletonList(new PersonUpdate("Pietje", "Puk", RoleType.DIRECTOR)));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><credits><person role='DIRECTOR'><givenName>Pietje</givenName><familyName>Puk</familyName></person></credits><locations /><scheduleEvents/><images/><segments/></program>";

        Program program = JAXBTestUtil.roundTripAndSimilar(update, expected).fetch();

        assertThat(program.getPersons().size()).isEqualTo(1);
        assertThat(program.getPersons().get(0).getGivenName()).isEqualTo("Pietje");
        assertThat(program.getPersons().get(0).getFamilyName()).isEqualTo("Puk");
        assertThat(program.getPersons().get(0).getRole()).isEqualTo(RoleType.DIRECTOR);
    }

    @Test
    public void testGetScheduleEvent() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setScheduleEvent(new ScheduleEventUpdate(
            Channel.RAD5,
            new Date(97779),
            new Date(100))
        );

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents><scheduleEvent channel=\"RAD5\"><start>1970-01-01T01:01:37.779+01:00</start><duration>P0DT0H0M0.100S</duration></scheduleEvent></scheduleEvents><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetRelations() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setRelations(new TreeSet<>(Collections.singletonList(new RelationUpdate(
            "ARTIST",
            "VPRO",
            "http://3voor12.vpro.nl/artists/444555",
            "Radiohead")
        )));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><relation uriRef=\"http://3voor12.vpro.nl/artists/444555\" broadcaster=\"VPRO\" type=\"ARTIST\">Radiohead</relation><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetImages() throws Exception {
        Image image = new Image(null, "urn:vpro.image:12345");
        image.setType(ImageType.ICON)
            .setTitle("Titel")
            .setDescription("Beschrijving")
            .setWidth(500)
            .setHeight(200).setOwner(OwnerType.BROADCASTER);

        Image image2 = new Image(null, "urn:vpro.image:12346");
        image2.setType(ImageType.ICON)
            .setTitle("Nebo Titel")
            .setDescription("Nebo Beschrijving")
            .setWidth(500)
            .setHeight(200).setOwner(OwnerType.NEBO);

        Program program = MediaBuilder.program().images(image, image2).build();
        ProgramUpdate update = ProgramUpdate.create(program);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><images><image highlighted=\"false\" type=\"ICON\"><title>Titel</title><description>Beschrijving</description><width>500</width><height>200</height><urn>urn:vpro.image:12345</urn></image></images><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetEpisodeOf() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setEpisodeOf(new TreeSet<>(Collections.singletonList(new MemberRefUpdate(20, "urn:vpro:media:group:864"))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><images/><episodeOf highlighted=\"false\" position=\"20\">urn:vpro:media:group:864</episodeOf><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testUnmarshalEpisodeOf() {
        String xml = "<program xmlns=\"urn:vpro:media:update:2009\"><episodeOf highlighted=\"false\" position=\"20\">urn:vpro:media:group:864</episodeOf></program>";
        ProgramUpdate update = JAXB.unmarshal(new StringReader(xml), ProgramUpdate.class);
        assertThat(update.getEpisodeOf().size()).isEqualTo(1);
    }

    @Test
    public void testGetSegments() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setSegments(new TreeSet<>(Collections.singletonList(SegmentUpdate.create(new Segment(update.fetch(), new Date(5555), new Date(100))))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><images/><segments><segment embeddable=\"true\"><duration>P0DT0H0M0.100S</duration><locations/><scheduleEvents/><images/><start>P0DT0H0M5.555S</start></segment></segments></program>";

        ProgramUpdate unmarshal = JAXBTestUtil.roundTripAndSimilar(update, expected);
        assertEquals(1, unmarshal.getSegments().size());
    }

    @Test
    public void testPortal() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setPortals(Collections.singletonList("STERREN24"));


        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><portal>STERREN24</portal><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testAgeRating() throws JAXBException, IOException, SAXException {
        ProgramUpdate update  = ProgramUpdate.create();
        update.setAgeRating(AgeRating._6);

        assertThat(update.getAgeRating()).isEqualTo(AgeRating._6);
        assertThat(update.fetch().getAgeRating()).isEqualTo(AgeRating._6);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><ageRating>6</ageRating><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }


    @Test
    public void testContentRating() throws JAXBException, IOException, SAXException {
        ProgramUpdate update = ProgramUpdate.create();
        update.setContentRatings(Arrays.asList(ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL));

        assertThat(update.getContentRatings()).containsExactly(ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL);
        assertThat(update.fetch().getContentRatings()).containsExactly(ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><contentRating>ANGST</contentRating><contentRating>DRUGS_EN_ALCOHOL</contentRating><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetTags() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setId(10L);
        update.setTags(new TreeSet<>(Arrays.asList("foo", "bar")));

        String expected = "<program embeddable=\"true\" urn=\"urn:vpro:media:program:10\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <tag>bar</tag>\n" +
            "    <tag>foo</tag>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";
        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(update, expected);
        assertThat(rounded.getTags()).hasSize(2);
        assertThat(rounded.fetch().getTags()).hasSize(2);
        rounded.setTags(new TreeSet<>(Arrays.asList("foo")));
        assertThat(rounded.fetch().getTags()).hasSize(1);

    }

    @Test
    public void testImageWithoutCredits() {
        ProgramUpdate update = ProgramUpdate.create();
        update.setAgeRating(AgeRating._6);
        update.setImages(new ImageUpdate(ImageType.LOGO, "title", null, new ImageLocation("https://placeholdit.imgix.net/~text?txt=adsfl")));
        Set<ConstraintViolation<MediaUpdate<Program>>> violations = update.violations(WarningValidatorGroup.class);
        System.out.println(violations);
        assertThat(violations).isNotEmpty();
    }

    @Test
    public void testXSD() throws Exception {
        Source xmlFile = new StreamSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program avType=\"VIDEO\" embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><broadcaster>VPRO</broadcaster><portal>STERREN24</portal><title>bla</title><credits><person role='DIRECTOR'><givenName>Pietje</givenName><familyName>Puk</familyName></person></credits><locations/><scheduleEvents/><images/><segments/></program>"));
        SchemaFactory schemaFactory = SchemaFactory
            .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        System.out.println(getClass().getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd"));
        Schema schema = schemaFactory.newSchema(getClass().getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd"));
        Validator validator = schema.newValidator();
        validator.validate(xmlFile);
    }
}
