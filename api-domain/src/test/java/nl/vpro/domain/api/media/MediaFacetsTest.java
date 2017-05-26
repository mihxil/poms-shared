package nl.vpro.domain.api.media;

import java.io.StringReader;
import java.util.Date;

import javax.xml.bind.JAXB;

import org.junit.Test;

import nl.vpro.domain.api.*;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class MediaFacetsTest {


    @Test
    public void testGetSortDateXml() throws Exception {
        MediaFacets in = new MediaFacets();
        in.setSortDates(new DateRangeFacets(
            DateRangePreset.THIS_WEEK,
            new DateRangeInterval("YEAR"),
            new DateRangeFacetItem(
                "MyFacet",
                new Date(0),
                new Date(1000)
            )
        ));

        assertThat(in.isFaceted()).isTrue();

        MediaFacets out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:mediaFacets xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:sortDates>\n" +
                "        <api:preset>THIS_WEEK</api:preset>\n" +
                "        <api:interval>YEAR</api:interval>\n" +
                "        <api:range>\n" +
                "            <api:name>MyFacet</api:name>\n" +
                "            <api:begin>1970-01-01T01:00:00+01:00</api:begin>\n" +
                "            <api:end>1970-01-01T01:00:01+01:00</api:end>\n" +
                "        </api:range>\n" +
                "    </api:sortDates>\n" +
                "</local:mediaFacets>");

        assertThat(out.getSortDates().getRanges()).hasSize(3);
    }

    @Test
    public void testGetSortDateJson() throws Exception {
        MediaFacets in = new MediaFacets();
        in.setSortDates(new DateRangeFacets(
            DateRangePreset.THIS_WEEK,
            new DateRangeInterval("YEAR"),
            new DateRangeFacetItem(
                "MyFacet",
                new Date(0),
                new Date(1000)
            )
        ));

        String json = Jackson2Mapper.getInstance().writeValueAsString(in);
        assertThat(json).isEqualTo("{\"sortDates\":[\"THIS_WEEK\",\"YEAR\",{\"name\":\"MyFacet\",\"begin\":0,\"end\":1000}]}");

        MediaFacets out = Jackson2Mapper.getInstance().readValue(json, MediaFacets.class);

        assertThat(out.getSortDates().getRanges()).hasSize(3);
    }

    @Test
    public void testGetSortDateJsonNoArray() throws Exception {
        String json = "{\"sortDates\": \"YEAR\"}";

        MediaFacets out = Jackson2Mapper.getInstance().readValue(json, MediaFacets.class);

        assertThat(out.getSortDates().getRanges()).hasSize(1);
    }

    @Test
    public void testGetBroadcaster() throws Exception {
        MediaFacets in = new MediaFacets();
        in.setBroadcasters(new MediaFacet());
        MediaFacets out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:mediaFacets xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:broadcasters sort=\"VALUE_ASC\">\n" +
                "        <api:max>24</api:max>\n" +
                "    </api:broadcasters>\n" +
                "</local:mediaFacets>");
        assertThat(out.getBroadcasters()).isEqualTo(new MediaFacet());
    }

    @Test
    public void testGetGenre() throws Exception {
        MediaFacets in = new MediaFacets();
        in.setGenres(new MediaSearchableTermFacet());

        assertThat(in.isFaceted()).isTrue();

        MediaFacets out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:mediaFacets xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:genres sort=\"VALUE_ASC\">\n" +
                "        <api:max>24</api:max>\n" +
                "    </api:genres>\n" +
                "</local:mediaFacets>"
        );
        assertThat(out.getGenres()).isEqualTo(new MediaFacet());
    }

    @Test
    public void testGetGenreBackwards() throws Exception {
        String backwards = "<local:mediaFacets xmlns=\"urn:vpro:api:2013\" xmlns:local=\"uri:local\" >\n" +
            "    <genres sort=\"COUNT\">\n" +
            "        <threshold>0</threshold>\n" +
            "        <offset>0</offset>\n" +
            "        <max>24</max>\n" +
            "    </genres>\n" +
            "</local:mediaFacets>";
        MediaFacets out = JAXB.unmarshal(new StringReader(backwards), MediaFacets.class);
        assertThat(out.getGenres().getSort()).isEqualTo(FacetOrder.COUNT_DESC);
    }

    @Test
    public void testGetTag() throws Exception {
        MediaFacets in = new MediaFacets();
        in.setTags(new ExtendedMediaFacet());

        assertThat(in.isFaceted()).isTrue();

        MediaFacets out = JAXBTestUtil.roundTrip(in,
            "<api:tags sort=\"VALUE_ASC\">\n" +
                "        <api:max>24</api:max>\n" +
                "    </api:tags>");
        assertThat(out.getTags()).isEqualTo(new MediaFacet());
    }

    @Test
    public void testGetDurations() throws Exception {
        MediaFacets in = new MediaFacets();
        in.setDurations(new DurationRangeFacets(
            new DurationRangeInterval("YEAR")
        ));

        assertThat(in.isFaceted()).isTrue();

        MediaFacets out = JAXBTestUtil.roundTrip(in,
            "<api:durations>\n" +
                "        <api:interval>YEAR</api:interval>\n" +
                "    </api:durations>");
        assertThat(out.getDurations().getRanges()).hasSize(1);
    }

    @Test
    public void testGetMediaSearchFromFacetXml() throws Exception {
        MediaSearch search = new MediaSearch();
        search.setText(new SimpleTextMatcher("find me"));

        MediaFacet facet = new MediaFacet();
        facet.setFilter(search);

        MediaFacet out = JAXBTestUtil.roundTripAndSimilar(facet,
            "<local:mediaFacet sort=\"VALUE_ASC\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:max>24</api:max>\n" +
                "    <api:filter>\n" +
                "        <api:text>find me</api:text>\n" +
                "    </api:filter>\n" +
                "</local:mediaFacet>"
        );
        assertThat(out.getFilter()).isInstanceOf(MediaSearch.class);
        assertThat(out.getFilter().getText().getValue()).isEqualTo("find me");
    }

    @Test
    public void testGetMediaSearchFromFacetJson() throws Exception {
        MediaSearch search = new MediaSearch();
        search.setText(new SimpleTextMatcher("find me"));

        MediaFacet facet = new MediaFacet();
        facet.setFilter(search);

        MediaFacet out = Jackson2TestUtil.roundTripAndSimilar(facet,
            "{\"sort\":\"VALUE_ASC\",\"max\":24,\"filter\":{\"text\":\"find me\"}}"
        );
        assertThat(out.getFilter()).isInstanceOf(MediaSearch.class);
        assertThat(out.getFilter().getText().getValue()).isEqualTo("find me");
    }

}
