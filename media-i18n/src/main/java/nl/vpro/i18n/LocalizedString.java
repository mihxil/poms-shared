package nl.vpro.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.com.neovisionaries.i18n.LanguageCode;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
@XmlAccessorType(XmlAccessType.NONE)
public class LocalizedString {

    private static final Logger LOG = LoggerFactory.getLogger(LocalizedString.class);

    private static final Map<String, String> MAP_TO_ISO = new HashMap<>();

    static {
        // These codes are mainly used in tva-xml's.
        //  they just make up stuff.
        //see http://www-01.sil.org/iso639-3/documentation.asp?id=zxx             -
        MAP_TO_ISO.put("xx", "zxx");
        MAP_TO_ISO.put("cz", "cs");

    }


    public static LocalizedString of(String value, Locale locale) {
        if (value == null) {
            return null;
        } else {
            LocalizedString string = new LocalizedString();
            string.value = value;
            string.locale = locale;
            return string;
        }
    }



    @XmlAttribute(name = "xml:lang")
    @XmlJavaTypeAdapter(value = XmlLangAdapter.class)
    private Locale locale;

    @XmlValue
    private String value;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static String get(Locale locale, Iterable<LocalizedString> strings) {
        LocalizedString candidate = null;
        if (strings != null) {
            int score = -1;
            for (LocalizedString string : strings) {
                int s = string.getScore(locale);
                if (string.getScore(locale) > score) {
                    candidate = string;
                    score = s;
                }
            }
        }
        return candidate == null ? null : candidate.getValue();

    }
    private int getScore(Locale locale) {
        int score = 0;
        if (Objects.equals(locale.getLanguage(), this.locale.getLanguage())) {
            score++;
        } else {
            return score;
        }
        if (Objects.equals(locale.getCountry(), this.locale.getCountry())) {
            score++;
        } else {
            return score;
        }
        if (Objects.equals(locale.getVariant(), this.locale.getVariant())) {
            score++;
        }
        return score;
    }

    public static class XmlLangAdapter extends XmlAdapter<String, Locale> {

        @Override
        public Locale unmarshal(String v) throws Exception {
            return adapt(v);

        }

        @Override
        public String marshal(Locale v) throws Exception {
            return v == null ? null : v.toString();

        }
    }


    public static Locale adapt(String v) {
        String[] split = v.split("[_-]", 3);
        String replace = MAP_TO_ISO.get(split[0].toLowerCase());
        if (replace != null) {
            LOG.warn("Found unknown iso language code {}, replaced with {}", split[0], replace);
            split[0] = replace;
        }
        LanguageCode languageCode = LanguageCode.getByCode(split[0], false);
        String language = languageCode == null ? split[0] : languageCode.name().toLowerCase();

        switch (split.length) {
            case 1:
                return new Locale(language);
            case 2:
                return new Locale(language, split[1].toUpperCase());
            default:
                return new Locale(language, split[1].toUpperCase(), split[2]);
        }
    }
}
