package nl.vpro.api.rs.subtitles;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import nl.vpro.domain.subtitles.Subtitles;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
abstract class AbstractSubtitlesWriter implements MessageBodyWriter<Subtitles> {

    private final MediaType mediaType;

    public AbstractSubtitlesWriter(MediaType mediaType) {
        this.mediaType = mediaType;
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
        //Iterator<? extends Cue> i = Util.headers(cueIterator, httpHeaders, "vtt");
        stream(subtitles, entityStream);
    }

    abstract protected void stream(Subtitles subtitles, OutputStream entityStream) throws IOException;


}
