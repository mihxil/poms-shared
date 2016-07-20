package nl.vpro.media.domain.es;

import java.util.Arrays;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public enum MediaESType {
    program,
    group,
    segment,
    deletedprogram("program"),
    deletedgroup("group"),
    deletedsegment("segment"),
    memberRef;


    private final String source;

    MediaESType(String s) {
        source = s;
    }

    MediaESType() {
        source = name();
    }
    public String source() {
        return ApiMediaIndex.source("mapping/" + source + ".json");
    }

    public static MediaESType[] MEDIAOBJECTS = {program, group, segment};


    public static String[] toString(MediaESType... types) {
        return Arrays.stream(types).map(Enum::name).toArray(String[]::new);

    }

    public static String[] mediaObjects() {
        return toString(MEDIAOBJECTS);
    }
}
