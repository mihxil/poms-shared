package nl.vpro.domain.media;

import javax.xml.bind.JAXB;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class GenreTest {
    @Before
    public void setup() {
        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());
    }


    @Test
    public void matchLegacy() {
        assertThat(EpgGenreType.valueOfLegacy(Arrays.asList(MisGenreType.YOUTH, MisGenreType.ENTERTAINMENT))).containsOnly(EpgGenreType._0106);
        assertThat(EpgGenreType.valueOfLegacy(Arrays.asList(MisGenreType.ENTERTAINMENT, MisGenreType.YOUTH))).containsOnly(EpgGenreType._0106);
        assertThat(EpgGenreType.valueOfLegacy(Arrays.asList(MisGenreType.RELIGIOUS))).containsOnly(EpgGenreType._0726);
    }

    @Test
    public void legacy2() {
        assertThat(EpgGenreType.valueOf(MisGenreType.ENTERTAINMENT, MisGenreType.YOUTH)).containsOnly(EpgGenreType._0106);
        assertThat(EpgGenreType.valueOf(MisGenreType.ENTERTAINMENT, MisGenreType.INFORMATIVE, MisGenreType.YOUTH)).containsOnly(EpgGenreType._0106, EpgGenreType._07);
    }

    @Test
    public void testGetEpgTerms() throws Exception {
        Genre genre = new Genre("3.0.1.2.10");

        JAXB.marshal(genre, System.out);
        JAXBTestUtil.roundTripAndSimilar(genre, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<local:genre id=\"3.0.1.2.10\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:local=\"uri:local\">\n" +
            "    <term>Film</term>\n" +
            "    <term>Spanning</term>\n" +
            "</local:genre>");
    }

/*
    @Test
    public void testGetEbuTerms() throws Exception {
        Genre genre = new Genre("3.1.1.1");

        JAXBTestUtil.roundTripAndSimilar(genre, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<genre id=\"3.1.1.1\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <term>News / Pure information</term>\n" +
            "    <term>Daily news</term>\n" +
            "</genre>\n");

    }
*/


    @Test
    public void testCompareToWhenSmaller() {
        assertThat(new Genre("3.0.1.7.27").compareTo(new Genre("3.0.100.7.27"))).isNegative();
    }

    @Test
    public void testCompareToWhenGreater() {
        assertThat(new Genre("3.0.100.7.27").compareTo(new Genre("3.0.1.7.27"))).isPositive();
    }

    @Test
    public void testEquals() {
        assertThat(new Genre("3.0.1.7.27").equals(new Genre("3.0.1.7.27"))).isTrue();
    }
}
