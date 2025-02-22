package nl.vpro.domain.media.bind;

import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;
import org.meeuw.i18n.CountrySubdivision;
import org.meeuw.i18n.CurrentCountry;

import static com.neovisionaries.i18n.CountryCode.GB;
import static com.neovisionaries.i18n.CountryCode.NL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.meeuw.i18n.Country.of;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class CountryWrapperTest {
    @Test
    public void getNameUKNL() {
        CountryWrapper wrapper = new CountryWrapper(of(GB));
        assertThat(wrapper.getName()).isEqualTo("Verenigd Koninkrijk");

    }

    @Test
    public void getNameGBNL() {
        CountryWrapper wrapper = new CountryWrapper(CountrySubdivision.of(GB, "GBN").get());
        assertThat(wrapper.getName()).isEqualTo("Groot-Brittannië");

    }

    @Test
    @Ignore
    public void getNameGBUK() {
        Locale.setDefault(Locale.UK);
        CountryWrapper wrapper = new CountryWrapper(CountrySubdivision.of(GB, "GBN").get());
        assertThat(wrapper.getName()).isEqualTo("Great Britain");

    }

    @Test
    @Ignore
    public void getNameUS() {
        Locale.setDefault(Locale.US);
        CountryWrapper wrapper = new CountryWrapper(new CurrentCountry(GB));
        assertThat(wrapper.getName()).isEqualTo("United Kingdom");

    }


    @Test
    public void getNameENGNL() {
        //Locale.setDefault(Locales.DUTCH);
        CountryWrapper wrapper = new CountryWrapper(CountrySubdivision.of(GB, "ENG").get());
        assertThat(wrapper.getName()).isEqualTo("Engeland");

    }

    @Test
    @Ignore
    public void getNameENGUK() {
        Locale.setDefault(Locale.UK);
        CountryWrapper wrapper = new CountryWrapper(CountrySubdivision.of(GB, "ENG").get());
        assertThat(wrapper.getName()).isEqualTo("England");

    }

    @Test
    public void getNameUTUK() {
        Locale.setDefault(Locale.UK);
        CountryWrapper wrapper = new CountryWrapper(CountrySubdivision.of(NL, "UT").get());
        assertThat(wrapper.getName()).isEqualTo("Utrecht");

    }

}
