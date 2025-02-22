package nl.vpro.domain.api.thesaurus;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.vpro.domain.media.gtaa.GTAAGeographicName;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.Test;

import nl.vpro.domain.media.gtaa.GTAAPerson;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

public class ThesaurusResultTest {

    @Test
    public void json() throws Exception {
        Jackson2TestUtil.roundTripAndSimilar(new ThesaurusResult(Arrays.asList(GTAAPerson.builder().build(), new GTAAGeographicName()), 10), "{\n" +
                "  \"offset\" : 0,\n" +
                "  \"max\" : 10,\n" +
                "  \"items\" : [ {\n" +
                "    \"objectType\" : \"person\",\n" +
                "    \"value\" : \"null\"\n" +
                "  }, {\n" +
                "    \"objectType\" : \"geographicname\"\n" +
                "  } ]\n" +
                "}");
    }




    @Test
    public void xml() throws Exception {
        JAXBTestUtil.roundTripAndSimilar(new ThesaurusResult(Arrays.asList(GTAAPerson.builder().build(), new GTAAGeographicName()), 10),
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<api:thesaurusItems offset=\"0\" max=\"10\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:media=\"urn:vpro:media:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:api=\"urn:vpro:api:2013\" xmlns:gtaa=\"urn:vpro:gtaa:2017\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:openskos=\"http://openskos.org/xmlns#\">\n" +
                        "    <api:items>\n" +
                        "        <api:item xsi:type=\"gtaa:gtaaPerson\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                        "            <gtaa:value>null</gtaa:value>\n" +
                        "        </api:item>\n" +
                        "        <api:item xsi:type=\"gtaa:gtaaGeographicName\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n" +
                        "    </api:items>\n" +
                        "</api:thesaurusItems>");
    }


}



