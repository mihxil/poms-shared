package nl.vpro.domain.media;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "avattributes")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "avAttributesType", propOrder = {
        "bitrate",
        "avFileFormat",
        "videoAttributes",
        "audioAttributes"
        })
public class AVAttributes implements Serializable {
    private static final long serialVersionUID = 1651506882422062995L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;

    @XmlElement
    private Integer bitrate;

    @XmlElement
    @Enumerated(EnumType.STRING)
    private AVFileFormat avFileFormat;

    @XmlElement
    @OneToOne(optional = true, orphanRemoval = true)
    @org.hibernate.annotations.Cascade({
        org.hibernate.annotations.CascadeType.ALL
    })
    private AudioAttributes audioAttributes;

    @XmlElement
    @OneToOne(optional = true, orphanRemoval = true)
    @org.hibernate.annotations.Cascade({
        org.hibernate.annotations.CascadeType.ALL
    })
    private VideoAttributes videoAttributes;

    public AVAttributes() {
    }

    public AVAttributes(AVFileFormat avFileFormat) {
        this.avFileFormat = avFileFormat;
    }

    public AVAttributes(Integer bitrate, AVFileFormat avFileFormat) {
        this.bitrate = bitrate;
        this.avFileFormat = avFileFormat;
    }

    public AVAttributes(AVAttributes source) {
        this(source.bitrate, source.avFileFormat);
        this.audioAttributes = AudioAttributes.copy(source.audioAttributes);
        this.videoAttributes = VideoAttributes.copy(source.videoAttributes);
    }

    public static AVAttributes copy(AVAttributes source){
        if(source == null) {
            return null;
        }
        return new AVAttributes(source);
    }

    public static AVAttributes update(AVAttributes from, AVAttributes to) {
        if(from != null) {
            if(to == null) {
                to = new AVAttributes();
            }

            to.setAvFileFormat(from.getAvFileFormat());
            to.setBitrate(from.getBitrate());

            to.setAudioAttributes(AudioAttributes.update(from.getAudioAttributes(), to.getAudioAttributes()));
            to.setVideoAttributes(VideoAttributes.update(from.getVideoAttributes(), to.getVideoAttributes()));

        } else if(from == null) {
            to = null;
        }

        return to;
    }

    public Long getId() {
        return id;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public AVAttributes setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
        return this;
    }

    public AVFileFormat getAvFileFormat() {
        return (avFileFormat != null) ? avFileFormat : AVFileFormat.UNKNOWN;
    }

    public AVAttributes setAvFileFormat(AVFileFormat avFileFormat) {
        this.avFileFormat = avFileFormat;
        return this;
    }

    public AudioAttributes getAudioAttributes() {
        return audioAttributes;
    }

    public AVAttributes setAudioAttributes(AudioAttributes audioAttributes) {
        this.audioAttributes = audioAttributes;
        return this;
    }

    public VideoAttributes getVideoAttributes() {
        return videoAttributes;
    }

    public AVAttributes setVideoAttributes(VideoAttributes videoAttributes) {
        this.videoAttributes = videoAttributes;
        return this;
    }

    /**
     * checks if it is video or audio.
     *
     * @return true if video
     */
    public boolean hasVideo() {
        return videoAttributes != null;

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("bitrate", bitrate)
            .append("avFileFormat", avFileFormat)
            .append("audioAttributes", audioAttributes)
            .append("videoAttributes", videoAttributes)
            .toString();
    }
}
