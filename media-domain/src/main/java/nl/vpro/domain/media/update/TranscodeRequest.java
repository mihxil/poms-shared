package nl.vpro.domain.media.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.Displayable;
import nl.vpro.domain.media.Encryption;
import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.MediaIdentifiable;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Data
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transcodeType")
@XmlRootElement(name = "transcode")
public class TranscodeRequest implements MediaIdentifiable {


    @NotNull
    @XmlAttribute
    private String mid;

    /**
     * File name (not prefixed by ftp account)
     */
    @NotNull
    private String fileName;


    @NotNull
    private Encryption encryption;

    @NotNull
    @lombok.Builder.Default
    private Priority priority = Priority.NORMAL;


    public TranscodeRequest() {

    }

    @Override
    public MediaType getMediaType() {
        return MediaType.MEDIA;
    }

    @Override
    public List<String> getCrids() {
        return Arrays.asList();

    }

    public static class Builder {

    }

    @XmlType(name = "priorityType")
    public enum Priority implements Displayable {
        LOW("Laag"),
        NORMAL("Normaal"),
        HIGH("Hoog"),
        URGENT("Urgent")
        ;

        @Getter
        private final String displayName;

        Priority(String displayName) {
            this.displayName = displayName;
        }
    }
}
