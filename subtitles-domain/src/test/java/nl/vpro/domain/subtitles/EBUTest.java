package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

import org.junit.Test;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public class EBUTest {


    @Test
    public void parse() {
        Stream<Cue> cues = EBU.parse("bla", getClass().getResourceAsStream("/VPWON_1272504.stl"));

        cues.forEach(c -> {
            System.out.println(c);
            }
        );
    }
}
