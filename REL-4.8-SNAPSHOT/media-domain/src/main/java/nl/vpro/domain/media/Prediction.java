package nl.vpro.domain.media;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A prediction is related to a program and indicates that locations (for a certain platform) <em>will be</em> available.
 *
 * @author Michiel Meeuwissen
 * @since 1.6
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "predictionType",
        propOrder = {
})
@Table(
    uniqueConstraints = {@UniqueConstraint(columnNames = {"mediaobject_id", "platform"})}
)
public class Prediction implements Comparable<Prediction>, Updatable<Prediction>,  Serializable {

    private static final long serialVersionUID = 0l;

    @XmlEnum
    @XmlType(name = "predictionStateEnum")
    public enum State {
        ANNOUNCED,
        REALIZED,
        REVOKED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;

    @XmlTransient
    @Column(nullable = false)
    @NotNull
    protected Date issueDate = new Date();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @XmlAttribute
    @NotNull
    protected State state = State.ANNOUNCED;

    @XmlAttribute
    protected Date publishStart;

    @XmlAttribute
    protected Date publishStop;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    @XmlValue
    @JsonProperty("platform")
    protected Platform platform;

    @ManyToOne
    @XmlTransient
    protected MediaObject mediaObject;

    private Prediction() {
    }

    /**
     * Constructor needed for jackson unmarshalling
     */
    public Prediction(String platform) {
        this.platform = Platform.valueOf(platform);
    }

    public Prediction(Platform platform) {
        this.platform = platform;
    }

    public Prediction(Platform platform, State state) {
        this.platform = platform;
        this.state = state;
    }

    public Prediction(Platform platform, Date publishStart, Date publishStop) {
        this.platform = platform;
        this.publishStart = publishStart;
        this.publishStop = publishStop;
    }

    public Prediction(Prediction source) {
        this(source, source.mediaObject);
    }

    public Prediction(Prediction source, MediaObject parent) {
        this(source.getPlatform(), source.getPublishStart(), source.getPublishStop());
        this.issueDate = source.issueDate;
        this.state = source.state;
        this.mediaObject = parent;
    }

    public static Prediction copy(Prediction source){
        return copy(source, source.mediaObject);
    }

    public static Prediction copy(Prediction source, MediaObject parent){
        if(source == null) {
            return null;
        }

        return new Prediction(source, parent);
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public MediaObject getMediaObject() {
        return mediaObject;
    }

    public void setMediaObject(MediaObject mediaObject) {
        this.mediaObject = mediaObject;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Date getPublishStart() {
        return publishStart;
    }

    public void setPublishStart(Date publishStart) {
        this.publishStart = publishStart;
    }

    public Date getPublishStop() {
        return publishStop;
    }

    public void setPublishStop(Date publishStop) {
        this.publishStop = publishStop;
    }


    @Override
    public int compareTo(Prediction o) {
        if (platform == null) {
            return o == null ? 0 : o.platform == null ? 0 : 1;
        } else {
            return o == null || o.platform == null ? 1 : platform.compareTo(o.platform);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Prediction)) {
            return false;
        }
        return platform == ((Prediction) o).platform;
    }

    @Override
    public int hashCode() {
        return platform.hashCode();
    }

    @Override
    public void update(Prediction from) {
        state = from.state;
    }

    @Override
    public String toString() {
        return
            "Prediction{platform=" + platform  + ", issueDate=" + issueDate + ", state=" + state + "}";
    }


    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {

        if (parent instanceof MediaObject) {
            MediaObject mediaObject = (MediaObject) parent;
            if (this.platform != null) {
                LocationAuthorityRecord.unknownAuthority(mediaObject, platform);
            }
        }
    }


}
