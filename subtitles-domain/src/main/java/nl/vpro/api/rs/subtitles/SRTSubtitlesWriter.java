package nl.vpro.api.rs.subtitles;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesUtil;

import static nl.vpro.api.rs.subtitles.Constants.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Provider
@Produces(SRT)
public class SRTSubtitlesWriter extends AbstractSubtitlesWriter {


    public SRTSubtitlesWriter() {
        super(SRT_TYPE, SRT_EXTENSION);
    }

    @Override
    protected void stream(Subtitles subtitles, OutputStream entityStream) throws IOException {
        SubtitlesUtil.toSRT(SubtitlesUtil.iterator(subtitles), entityStream);
    }

}
