package nl.vpro.api.rs.subtitles;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import nl.vpro.domain.Changeables;
import nl.vpro.domain.subtitles.Cue;
import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesFormat;
import nl.vpro.domain.subtitles.SubtitlesUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
abstract class AbstractSubtitlesWriter implements MessageBodyWriter<Subtitles> {

    public static final String OFFSET_HEADER = "X-subtitles-offset";

    private final MediaType mediaType;
    private final String extension;

    public AbstractSubtitlesWriter(SubtitlesFormat format) {
        this.mediaType = MediaType.valueOf(format.getMediaType());
        this.extension = format.getExtension();
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return mediaType.isCompatible(this.mediaType) && Subtitles.class.isAssignableFrom(type);

    }

    @Override
    public long getSize(Subtitles subtitles, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;

    }

    @Override
    public void writeTo(Subtitles subtitles, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        Util.headers(subtitles.getId(), httpHeaders, extension);
        Changeables.headers(subtitles, httpHeaders);
        httpHeaders.putSingle(OFFSET_HEADER, subtitles.getOffset() == null ? "GUESSED" : subtitles.getOffset().toString());
        stream(subtitles, entityStream);
    }


    protected Iterator<Cue> iterate(Subtitles subtitles) {
        return SubtitlesUtil.iterator(subtitles, true);
    }
    abstract protected void stream(Subtitles subtitles, OutputStream entityStream) throws IOException;


}
