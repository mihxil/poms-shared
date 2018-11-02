package nl.vpro.domain.media.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.function.BiFunction;

import org.slf4j.Logger;

import nl.vpro.domain.media.Segment;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.logging.simple.Slf4jSimpleLogger;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")
@Data
@ToString
public class AssemblageConfig {

    @lombok.Builder.Default
    OwnerType ownerType = OwnerType.BROADCASTER;

    @lombok.Builder.Default
    boolean copyWorkflow = false;

    @lombok.Builder.Default
    boolean copyLanguageAndCountry = false;

    @lombok.Builder.Default
    boolean imageMetaData = false;

    @lombok.Builder.Default
    boolean copyPredictions = false;

    @lombok.Builder.Default
    boolean episodeOfUpdate = true;

    @lombok.Builder.Default
    boolean guessEpisodePosition = false;

    @lombok.Builder.Default
    boolean memberOfUpdate = true;

    @lombok.Builder.Default
    boolean ratingsUpdate = true;

    @lombok.Builder.Default
    boolean createScheduleEvents = false;

    @lombok.Builder.Default
    boolean locationsUpdate = false;

    @lombok.Builder.Default
    boolean stealMids = false;

    /**
     * Matching happens on crid. There is a possibility though that the found object is of the wrong type (e.g. a Program and not a Segment)
     * If stealCrids is true, then in that situation the existing object is left, but the matching crid is removed.
     */
    @lombok.Builder.Default
    boolean stealCrids= false;

    /**
     * If an incoming segment matches a segment of _different_ program, then disconnect it from that other program
     * Otherwise consider this situation errorneous.
     */
    @lombok.Builder.Default
    boolean stealSegments = false;

    /**
     * On default it you merge a program, exsisting segments will not be removed
     * This can be configured using this.
     * See als {@link Builder#deleteSegmentsForOwner()}
     */
    @lombok.Builder.Default
    BiFunction<Segment, AssemblageConfig, Boolean> segmentsForDeletion = (s, ac) -> false;

    SimpleLogger logger;

    public SimpleLogger loggerFor(Logger log) {
        if (logger == null) {
            return Slf4jSimpleLogger.of(log);
        } else {
            return logger.chain(Slf4jSimpleLogger.of(log));
        }
    }
    public AssemblageConfig copy() {
        return new AssemblageConfig(
            ownerType,
            copyWorkflow,
            copyLanguageAndCountry,
            imageMetaData,
            copyPredictions,
            episodeOfUpdate,
            guessEpisodePosition,
            memberOfUpdate,
            ratingsUpdate,
            createScheduleEvents,
            locationsUpdate,
            stealMids,
            stealCrids,
            stealSegments,
            segmentsForDeletion,
            logger);
    }
    public AssemblageConfig withLogger(SimpleLogger logger) {
        AssemblageConfig copy = copy();
        copy.setLogger(logger);
        return copy;
    }
    public AssemblageConfig withThreadLocalLogger() {
        return withLogger(SimpleLogger.THREAD_LOCAL.get());
    }




    public static Builder withAllTrue() {
        return builder()
            .copyWorkflow(true)
            .copyLanguageAndCountry(true)
            .copyPredictions(true)
            .episodeOfUpdate(true)
            .guessEpisodePosition(true)
            .memberOfUpdate(true)
            .ratingsUpdate(true)
            .imageMetaData(true)
            .createScheduleEvents(true)
            .locationsUpdate(true)
            .stealMids(true)
            .stealCrids(true)
            .stealSegments(true)
            ;
    }

    public boolean considerForDeletion(Segment segment) {
        return segmentsForDeletion.apply(segment, this);
    }

    public static class Builder {
        /**
         * Since POMS 5.9 a segment can have an owner.
         * This sais that segments that have the configured owner, but are not present in the incoming program are to be deleted from the program to update.
         */
        public Builder deleteSegmentsForOwner() {
            return segmentsForDeletion((s, a) -> s.getOwner() != null && s.getOwner() == a.getOwnerType());
        }
    }

}
