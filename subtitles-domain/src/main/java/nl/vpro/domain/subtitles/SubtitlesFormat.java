package nl.vpro.domain.subtitles;

import lombok.Getter;

import java.nio.charset.Charset;
import java.util.Optional;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.api.rs.subtitles.Constants;
import nl.vpro.util.ISO6937CharsetProvider;


/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
@XmlEnum
@XmlType(name = "subtitlesFormatEnum")
public enum SubtitlesFormat {

    WEBVTT("vtt", Constants.VTT, Charset.forName("UTF-8")),
    TT888("txt", Constants.TT888, ISO6937CharsetProvider.ISO6937),
    EBU("stl", Constants.EBU, null),
    SRT("srt", Constants.SRT, Charset.forName("cp1252"))
    ;

    @Getter
    private final String extension;
    @Getter
    private final String mediaType;
    @Getter
    private final Charset charset;

    SubtitlesFormat(String extension, String mediaType, Charset charset) {
        this.extension = extension;
        this.mediaType = mediaType;
        this.charset = charset;
    }

    public static Optional<SubtitlesFormat> ofExtension(String extension) {
        for (SubtitlesFormat sf : values()) {
            if (sf.extension.equalsIgnoreCase(extension)) {
                return Optional.of(sf);
            }
        }
        return Optional.empty();
    }
}
