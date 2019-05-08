package nl.vpro.domain.media.update;

import lombok.*;

import java.time.Duration;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.slf4j.Logger;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Segment;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.logging.simple.Slf4jSimpleLogger;
import nl.vpro.util.IntegerVersion;

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
    OwnerType owner = OwnerType.BROADCASTER;

    @Singular
    List<OwnerType> similarOwnerTypes;

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
    boolean copyTwitterrefs = false;

    @lombok.Builder.Default
    boolean createScheduleEvents = false;

    /**
     * This is mainly targeted at PREPR which does not support programs spanning 0 o'clock.
     * If this is set to >= 0, then schedule merging will merge adjacent scheduleevents if they are of the same MID
     * The size of the duration defines the maximal gap between the events. (For PREPR there is never anything broadcasted in the second before midnight)
     *
     */
    @lombok.Builder.Default
    Duration mergeScheduleEvents = Duration.ofMillis(-1);

    @lombok.Builder.Default
    BiFunction<MediaObject, AssemblageConfig, Boolean> inferDurationFromScheduleEvents = (s, ac) -> false;


    @lombok.Builder.Default
    boolean locationsUpdate = false;

    @lombok.Builder.Default
    Steal stealMids = Steal.NO;

    /**
     * Matching happens on crid. There is a possibility though that the found object is of the wrong type (e.g. a Program and not a Segment)
     * If stealCrids is true, then in that situation the existing object is left, but the matching crid is removed.
     */
    @lombok.Builder.Default
    Steal stealCrids= Steal.NO;

    /**
     * If an incoming segment matches a segment of _different_ program, then disconnect it from that other program
     * Otherwise consider this situation errorneous.
     */
    @lombok.Builder.Default
    Steal stealSegments = Steal.NO;

    /**
     * On default it you merge a program, exsisting segments will not be removed
     * This can be configured using this.
     * See als {@link Builder#deleteSegmentsForOwner()}
     */
    @lombok.Builder.Default
    BiFunction<Segment, AssemblageConfig, Boolean> segmentsForDeletion = (s, ac) -> false;

    @lombok.Builder.Default
    Function<String, Boolean> cridsForDelete = (c) -> false;

    @lombok.Builder.Default
    boolean updateType = false;

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
            owner,
            similarOwnerTypes,
            copyWorkflow,
            copyLanguageAndCountry,
            imageMetaData,
            copyPredictions,
            episodeOfUpdate,
            guessEpisodePosition,
            memberOfUpdate,
            ratingsUpdate,
            copyTwitterrefs,
            createScheduleEvents,
            mergeScheduleEvents,
            inferDurationFromScheduleEvents,
            locationsUpdate,
            stealMids,
            stealCrids,
            stealSegments,
            segmentsForDeletion,
            cridsForDelete,
            updateType,
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
            .copyTwitterrefs(true)
            .imageMetaData(true)
            .createScheduleEvents(true)
            .locationsUpdate(true)
            .stealMids(Steal.YES)
            .stealCrids(Steal.YES)
            .stealSegments(Steal.YES)
            .updateType(true)
            ;
    }

    public boolean considerForDeletion(Segment segment) {
        return segmentsForDeletion.apply(segment, this);
    }

    public void backwardsCompatible(IntegerVersion version) {
        setCopyLanguageAndCountry(version != null && version.isNotBefore(5, 0));
        setCopyPredictions(version != null && version.isNotBefore(5, 6));
        setCopyTwitterrefs(version != null && version.isNotBefore(5, 10));
    }

    public static class Builder {
        /**
         * Since POMS 5.9 a segment can have an owner.
         * This sais that segments that have the configured owner, but are not present in the incoming program are to be deleted from the program to update.
         */
        public Builder deleteSegmentsForOwner() {
            return segmentsForDeletion((s, a) -> s.getOwner() != null && s.getOwner() == a.getOwner());
        }
    }

    public enum Steal {
        YES((w) -> true),
        IF_DELETED(Workflow.PUBLISHED_AS_DELETED::contains),
        NO((w) -> true);

        private final Function<Workflow, Boolean> is;

        Steal(Function<Workflow, Boolean> is) {
            this.is = is;
        }

        public boolean is(Workflow workflow) {
            return is.apply(workflow);
        }
    }
}
