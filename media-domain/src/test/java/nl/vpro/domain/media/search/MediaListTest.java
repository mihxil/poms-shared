package nl.vpro.domain.media.search;

import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.util.Arrays;
import java.util.TreeSet;

import javax.xml.bind.JAXB;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.AVType;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.ProgramType;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.Tag;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.user.TestEditors;

import static nl.vpro.test.util.jaxb.JAXBTestUtil.assertThatXml;
import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @since 1.7
 */
public class MediaListTest {


    @Test
    public void marshalMediaListItem() throws IOException, SAXException {
        Program program = JAXB.unmarshal(new StringReader("<program xmlns=\"urn:vpro:media:2009\" urn='urn:vpro:media:program:123'><broadcaster>VPRO</broadcaster></program>"), Program.class);
        program.setCreationInstant(Instant.ofEpochMilli(1343922085885L));
        program.setLastModifiedInstant(Instant.ofEpochMilli(1343922085885L));
        program.setCreatedBy(TestEditors.vproEditor());
        program.setPublishStopInstant(Instant.ofEpochMilli(1343922085885L));
        program.setType(ProgramType.CLIP);
        program.setAVType(AVType.VIDEO);
        program.setTags(new TreeSet<>(Arrays.asList(new Tag("foo"), new Tag("bar"))));
        program.setMainTitle("");
        program.addTitle("", OwnerType.BROADCASTER, TextualType.SUB);


        MediaList<MediaListItem> xmlList = new MediaList<>(
            MediaPager.builder()
                .offset(1)
                .max(10)
                .sort(MediaSortField.creationDate)
                .build()
            , 1000, new MediaListItem(program));


        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<s:list totalCount=\"1000\" sort=\"creationDate\" offset=\"1\" max=\"10\" order=\"ASC\" size=\"1\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:s=\"urn:vpro:media:search:2012\" xmlns:update=\"urn:vpro:media:update:2009\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <s:item xsi:type=\"s:mediaListItem\" avType=\"VIDEO\" id=\"123\" mediaType=\"nl.vpro.domain.media.Program\" urn=\"urn:vpro:media:program:123\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "        <s:broadcaster>VPRO</s:broadcaster>\n" +
            "        <s:title></s:title>\n" +
            "        <s:subTitle></s:subTitle>\n" +
            "        <s:creationDate>2012-08-02T17:41:25.885+02:00</s:creationDate>\n" +
            "        <s:lastModified>2012-08-02T17:41:25.885+02:00</s:lastModified>\n" +
            "        <s:createdBy>editor@vpro.nl</s:createdBy>\n" +
            "        <s:sortDate>2012-08-02T17:41:25.885+02:00</s:sortDate>\n" +
            "        <s:type>CLIP</s:type>\n" +
            "        <s:publishStop>2012-08-02T17:41:25.885+02:00</s:publishStop>\n" +
            "        <s:numberOfLocations>0</s:numberOfLocations>\n" +
            "        <s:tag>bar</s:tag>\n" +
            "        <s:tag>foo</s:tag>\n" +
            "    </s:item>\n" +
            "</s:list>";


        MediaList<MediaListItem> list = assertThatXml(xmlList)
            .isSimilarTo(expected)
            .isValid(Xmlns.SCHEMA.newValidator())
            .get();

        assertEquals("urn:vpro:media:program:123", list.getList().get(0).getUrn());
    }


    protected void validate(String string) throws SAXException, IOException {
        Validator validator = Xmlns.SCHEMA.newValidator();
        validator.validate(new StreamSource(new StringReader(string)));
    }
}
