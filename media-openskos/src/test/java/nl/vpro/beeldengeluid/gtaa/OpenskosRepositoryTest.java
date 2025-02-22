package nl.vpro.beeldengeluid.gtaa;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.web.client.HttpServerErrorException;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import nl.vpro.domain.media.gtaa.GTAANewThesaurusObject;
import nl.vpro.domain.media.gtaa.Status;
import nl.vpro.openarchives.oai.Record;
import nl.vpro.util.CountedIterator;
import nl.vpro.w3.rdf.Description;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

@Ignore  //This gives an Out of Memory error in test env.
public class OpenskosRepositoryTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().dynamicPort());

    private OpenskosRepository repo;
    @Before
    public void createRepo() {
        repo = new OpenskosRepository("http://localhost:" + wireMockRule.port(), "");
    }

    @Test
    public void test() throws IOException {
        wireMockRule.stubFor(get(urlPathEqualTo("/api/find-concepts")).willReturn(okXml(f("/find-person-test.xml"))));

        List<Description> persons = repo.findPersons("test", 1);
        assertThat(persons).isNotEmpty();
        Description description = persons.get(0);
        assertThat(description.getStatus()).isNotNull();
        assertThat(description.getPrefLabel().getValue()).isEqualTo("test2, test.");
        assertThat(description.getStatus().equals(Status.candidate));
    }

    @Test
    public void testAddItem() throws IOException {
        wireMockRule.stubFor(post(urlPathEqualTo("/api/concept")).willReturn(okXml(f("/submit-person-response.xml")).withStatus(201)));

        repo.setUseXLLabels(true);
        GTAANewThesaurusObject testNameX = GTAANewThesaurusObject.builder().build().builder()
            .value("Testlabel1")
            .note("Note123")
            .build();

        repo.submit(testNameX, "testCreatorX");
        wireMockRule.verify(postRequestedFor(urlPathEqualTo("/api/concept")).withRequestBody(
                matchingXPath("//skosxl:literalForm[text() = 'Testlabel1']").withXPathNamespace("skosxl", "http://www.w3.org/2008/05/skos-xl#")));
    }

    @Test
    public void updatesNoResults() throws Exception {
        wireMockRule.stubFor(get(urlPathEqualTo("/oai-pmh")).willReturn(okXml(f("no-updates.xml"))));

        try (CountedIterator<Record> updates = repo.getPersonUpdates(Instant.EPOCH, Instant.now())) {
            assertThat(updates.hasNext()).isFalse();
        }
    }

    @Test
    public void updates() throws Exception {
        wireMockRule.stubFor(get(urlPathEqualTo("/oai-pmh")).willReturn(okXml(f("updates.xml"))));
        try (CountedIterator<Record> updates = repo.getPersonUpdates(Instant.EPOCH, Instant.now())) {
            Record next = updates.next();
            assertThat(next).isNotNull();
            assertThat(next.getHeader().getDatestamp()).isNotNull();
            assertThat(next.getMetaData().getFirstDescription().getPrefLabel().getValue()).isEqualTo("Benoist, André");
        }
    }

    @Test
    public void anyUpdates() throws Exception {
        wireMockRule.stubFor(get(urlPathEqualTo("/oai-pmh"))
            .willReturn(okXml(f("any-updates.xml"))));
        try (CountedIterator<Record> updates = repo.getAllUpdates(Instant.EPOCH, Instant.now())) {
            int count = 0;
            Record next = updates.next(); // update 1
            count++;
            assertThat(next).isNotNull();
            assertThat(next.getHeader().getDatestamp()).isNotNull();
            assertThat(next.getMetaData().getFirstDescription().getPrefLabel().getValue()).isEqualTo("Giotakes, Nico");
            assertThat(StringUtils.deleteWhitespace(next.getMetaData().getFirstDescription().getChangeNote().get(0)))
                    .isEqualTo("Forward:http://data.beeldengeluid.nl/gtaa/1672578");
            while(updates.hasNext()) {
                count++;
                next = updates.next();
                try {
                    if (next.getMetaData().getFirstDescription().getAbout().equals("http://data.beeldengeluid.nl/gtaa/1011506")) {
                        assertThat(next.getMetaData().getFirstDescription().getRedirectedFrom())
                                .isEqualTo("http://data.beeldengeluid.nl/gtaa/29654");
                    }
                } catch (Exception e) {

                }
                if (next.getMetaData() == null) {
                    assertThat(next.getHeader().getStatus()).isEqualTo("deleted");
                }
            }
            assertThat(count).isEqualTo(200);
        }
    }

    private String f(String file) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream(StringUtils.prependIfMissing(file, "/")), StandardCharsets.UTF_8);
    }

    @Test
    public void testRetrieveItemStatus() throws Exception {
        wireMockRule.stubFor(get(urlPathEqualTo("/api/find-concepts")).willReturn(okXml(f("retrieve-status.xml"))));
        Optional<Description> description = repo.retrieveItemStatus("http://data.beeldengeluid.nl/gtaa/1672723");
        assertThat(description.get().getStatus().toString()).isEqualTo("approved");
    }

    @Test
    public void retrieveItemStatusShouldReturnIllegalArgumentEx() throws Exception {
        wireMockRule.stubFor(get(urlPathEqualTo("/api/find-concepts")).willReturn(status(500).withBody(f("retrieve-status-not-found.xml"))));
        Optional<Description> desc = repo.retrieveItemStatus("blabla");
        assertThat(desc.isPresent()).isFalse();
    }

    @Test(expected = HttpServerErrorException.class)
    public void retrieveItemStatusShouldReturnUnexpectedError() {
        wireMockRule.stubFor(get(urlPathEqualTo("/api/find-concepts")).willReturn(status(500).withBody("Random error")));
        Optional<Description> desc = repo.retrieveItemStatus("http://data.beeldengeluid.nl/gtaa/1672723");
    }



}
