package nl.vpro.domain.subtitles;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author Michiel Meeuwissen
 * @since 4.7.7
 */
public class SubtitlesId implements Serializable {

    private String mid;
    private SubtitlesType type = SubtitlesType.CAPTION;
    private Locale language;

    public static SubtitlesId ebuCaption(String mid) {
        return new SubtitlesId(mid, SubtitlesUtil.DUTCH, SubtitlesType.CAPTION);
    }

    public SubtitlesId()  {

    }

    public SubtitlesId(String mid, Locale language, SubtitlesType type) {
        this.mid = mid;
        this.language = language;
        this.type = type;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public SubtitlesType getType() {
        return type;
    }

    public void setType(SubtitlesType type) {
        this.type = type;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubtitlesId that = (SubtitlesId) o;

        if (!mid.equals(that.mid)) return false;
        if (type != that.type) return false;
        return language.equals(that.language);

    }

    @Override
    public int hashCode() {
        int result = mid.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + language.hashCode();
        return result;
    }
}
