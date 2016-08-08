package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.time.Duration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Slf4j
class WEBVTT {



    static Stream<Cue> parse(String parent, Reader reader) {
        final Iterator<String> stream = new BufferedReader(reader)
            .lines().iterator();
        Iterator<Cue> cues = new Iterator<Cue>() {

            boolean needsFindNext = true;
            String headLine= null;
            String timeLine = null;
            StringBuilder content = new StringBuilder();

            @Override
            public boolean hasNext() {
                findNext();
                return timeLine != null;
            }

            @Override
            public Cue next() {
                findNext();
                if (timeLine == null || headLine == null) {
                    throw new NoSuchElementException();
                }
                needsFindNext = true;
                try {
                    return parseCue(parent, headLine, timeLine, content.toString());
                } catch (ParseException e) {
                    log.error(e.getMessage(), e);
                    return null;
                }

            }

            protected void findNext() {
                if (needsFindNext) {
                    headLine = null;
                    content.setLength(0);
                    while (stream.hasNext()) {
                        String l = stream.next();
                        if (StringUtils.isNotBlank(l)) {
                            timeLine = l.trim();
                            break;
                        }
                    }
                    timeLine = stream.next();
                    while (stream.hasNext()) {
                        String l = stream.next();
                        if (StringUtils.isBlank(l)) {
                            break;
                        }
                        if (content.length() > 0) {
                            content.append('\n');
                        }
                        content.append(l.trim());
                    }
                    needsFindNext = false;
                }
            }
        };
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(cues, Spliterator.ORDERED), false);

    }


    static Cue parseCue(String parent, String headLine, String timeLine, String content) throws ParseException {
        String[] split = timeLine.split("\\s+");
        return new Cue(
            parent,
            Integer.parseInt(headLine),
            parseDuration(split[0]),
            parseDuration(split[2]),
            content
        );

    }


    public static void toVTT(Iterator<? extends Cue> cueIterator, OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out, Charset.forName("UTF-8"));
        toVTT(cueIterator, writer);
        writer.flush();
    }

    public static void toVTT(Iterator<? extends Cue> cueIterator, Writer writer) throws IOException {
        writer.write("WEBVTT\n\n");
        StringBuilder builder = new StringBuilder();
        while (cueIterator.hasNext()) {
            formatVVT(cueIterator.next(), builder);
            writer.write(builder.toString());
            builder.setLength(0);
        }
    }

    protected static String formatDuration(Duration duration) {
        Long millis = duration.toMillis();
        Long minutes = millis / 60000;
        millis -= minutes * 60000;
        Long seconds = millis / 1000;
        millis -= seconds * 1000;
        return String.format("%d:%02d.%03d", minutes, seconds, millis);
    }

    protected static Duration parseDuration(String duration) {
        String[] split = duration.split(":", 2);
        int index = 0;
        Long minutes;
        if (split.length == 2) {
            minutes =  Long.parseLong(split[0]);
            index++;
        } else {
            minutes = 0L;
        }
        String [] split2 = split[index].split("\\.", 2);
        Long seconds = Long.parseLong(split2[0]);
        Long millis = Long.parseLong(split2[1]);
        return Duration.ofMinutes(minutes).plusSeconds(seconds).plusMillis(millis);
    }

    protected static StringBuilder formatVVT(Cue cue, StringBuilder builder) {
        builder.append(cue.getSequence());
        builder.append("\n");
        if (cue.getStart() != null) {
            builder.append(formatDuration(cue.getStart()));
        }
        builder.append(" --> ");
        if (cue.getEnd() != null) {
            builder.append(formatDuration(cue.getEnd()));
        }
        builder.append("\n");
        if (cue.getContent() != null) {
            builder.append(cue.getContent());
        }
        builder.append("\n\n");
        return builder;
    }

}
