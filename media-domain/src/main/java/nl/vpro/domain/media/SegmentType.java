package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlEnum
@XmlType(name = "segmentTypeEnum")
public enum SegmentType implements SubMediaType {

    SEGMENT(MediaType.SEGMENT),
    VISUALRADIOSEGMENT(MediaType.VISUALRADIOSEGMENT);


    public static String URN_PREFIX = "urn:vpro:media:segment:";

    private MediaType mediaType;

    SegmentType(MediaType mediaType) {
        this.mediaType = mediaType;
        if (mediaType == null) {
            throw new IllegalStateException();
        }
    }

    public String value() {
        return name();
    }

    public SegmentType fromValue(String v) {
        return valueOf(v);
    }

    @Override
    public String getUrnPrefix() {
        return URN_PREFIX;
    }
    @Override
    public MediaType getMediaType() {
        return mediaType;
    }
}
