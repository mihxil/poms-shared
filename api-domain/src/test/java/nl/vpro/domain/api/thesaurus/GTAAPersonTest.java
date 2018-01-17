package nl.vpro.domain.api.thesaurus;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.assertj.core.api.Java6Assertions;
import org.junit.Test;

import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.media.gtaa.*;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.openarchives.oai.Label;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GTAAPersonTest {
    @Test
    public void json() throws Exception {
        GTAAPerson person = GTAAPerson.builder()
            .givenName("Pietje").familyName("puk")
            .lastModified(LocalDateTime.of(2018, 1, 16, 19, 44).atZone(Schedule.ZONE_ID).toInstant())
            .build();

        Jackson2TestUtil.roundTripAndSimilarAndEquals(person, "{\n" +
            "  \"objectType\" : \"person\",\n" +
            "  \"givenName\" : \"Pietje\",\n" +
            "  \"familyName\" : \"puk\",\n" +
            "  \"value\" : \"puk, Pietje\",\n" +
            "  \"lastModified\" : 1516128240000\n" +
            "}");

    }


    @Test
    public void json2() throws IOException {
        String example = "{ \"objectType\" : \"person\", \"familyName\":\"Puk\",\"givenName\":\"Pietje\",\"notes\":[null,\"vanuit POMS voor: POW_00700386\"]}\n" +
            "Name\n";
        GTAAPerson person = Jackson2Mapper.getLenientInstance().readValue(new StringReader(example), GTAAPerson.class);
        Java6Assertions.assertThat(person.getFamilyName()).isEqualTo("Puk");
        Java6Assertions.assertThat(person.getGivenName()).isEqualTo("Pietje");
    }

    @Test
    public void jsonAsThesaurusObjectReturnsPerson() throws Exception {


        ThesaurusObject object = Jackson2Mapper.getInstance().readValue(new StringReader("{\n" +
            "  \"objectType\" : \"person\",\n" +
            "  \"familyName\" : \"puk\",\n" +
            "  \"value\" : \"null puk\",\n" +
            "  \"prefLabel\" : \"null puk\"\n" +
            "}"), ThesaurusObject.class);

        assertThat(object).isInstanceOf(GTAAPerson.class);

    }

    @Test
    public void jsonAsThesaurusObjectReturnsItem() throws Exception {


        ThesaurusObject object = Jackson2Mapper.getInstance().readValue(new StringReader("{\n" +
            "  \"objectType\" : \"item\",\n" +
            "  \"prefLabel\" : \"augustijnen\"\n" +
            "}"), ThesaurusObject.class);

        assertThat(object).isInstanceOf(ThesaurusItem.class);

    }

    @Test
    public void xml() throws Exception {
        GTAAPerson person = GTAAPerson.builder()
            .notes(Arrays.asList(Label.builder().value("bla").lang("nl").build()))
            .knownAs(Arrays.asList(Names.builder().familyName("pietje").build()))
            .familyName("puk")
            .gtaaUri("http://gtaa/1234")
            .status(Status.approved)
            .lastModified(LocalDateTime.of(2017, 9, 20, 10, 43, 0).atZone(Schedule.ZONE_ID).toInstant())
            .build();

        JAXBTestUtil.roundTripAndSimilarAndEquals(person, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<gtaa:person gtaa:status=\"approved\" gtaa:lastModified=\"2017-09-20T10:43:00+02:00\" gtaa:gtaaUri=\"http://gtaa/1234\" gtaa:id=\"http://gtaa/1234\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:gtaa=\"urn:vpro:gtaa:2017\" xmlns:openskos=\"http://openskos.org/xmlns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
            "    <gtaa:value>puk</gtaa:value>\n" +
            "    <gtaa:familyName>puk</gtaa:familyName>\n" +
            "    <gtaa:notes xml:lang=\"nl\">bla</gtaa:notes>\n" +
            "    <gtaa:knownAs>\n" +
            "        <gtaa:familyName>pietje</gtaa:familyName>\n" +
            "    </gtaa:knownAs>\n" +
            "</gtaa:person>\n");

    }
}
