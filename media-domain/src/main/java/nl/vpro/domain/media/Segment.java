package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.SortedSet;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Child;
import nl.vpro.domain.media.support.AuthorizedDuration;
import nl.vpro.domain.media.support.MutableOwnable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.validation.SegmentValidation;
import nl.vpro.xml.bind.DurationXmlAdapter;

/**
 * A segment is a view on a program.
 */

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "segment")
@XmlType(name = "segmentType", propOrder = {
    "start"
})
@JsonTypeName("segment")
@SegmentValidation
public class Segment extends MediaObject implements Comparable<Segment>, Child<Program>, MutableOwnable {


    private static final long serialVersionUID = -868293795041160925L;

    @ManyToOne(targetEntity = Program.class, optional = false)
    protected Program parent;

    @Column(nullable = false)
    @NotNull(message = "start property is required")
    protected java.time.Duration start;

    // Some thing like this will be needed for prepr import
    //protected List<Composite>

    @Transient
    protected String urnRef;

    @Transient
    protected String midRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "no segment type given")
    protected SegmentType type = SegmentType.SEGMENT;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private OwnerType owner;


    public Segment() {
    }

    public Segment(Program program, String midRef, java.time.Duration start, AuthorizedDuration duration) {
        this.start = start;
        this.duration = duration;
        avType = program.getAVType();
        program.addSegment(this);
        this.midRef = midRef == null ? program.getMid() : midRef;
    }

    public Segment(Program program) {
        this(program, program.getMid(), java.time.Duration.ZERO, program.getDuration());
    }

    public Segment(Program program, java.time.Duration start, java.time.Duration duration) {
        this(program, program.getMid(), start, new AuthorizedDuration(duration));
    }

    public Segment(Program program, java.time.Duration start, AuthorizedDuration duration) {
        this(program, program.getMid(), start, duration);
    }

    public Segment(String mid, Program program, java.time.Duration start, AuthorizedDuration duration) {
        this(program, program.getMid(), start, duration);
        this.mid = mid;
    }

    public Segment(AVType avType) {
        this.avType = avType;
    }

    public Segment(AVType avType, java.time.Duration start) {
        this.avType = avType;
        this.start = start;
    }

    public Segment(AVType avType, java.time.Duration start, java.time.Duration duration) {
        this(avType, start, new AuthorizedDuration(duration));
    }

    public Segment(AVType avType, java.time.Duration start, AuthorizedDuration duration) {
        this.avType = avType;
        this.start = start;
        this.duration = duration;
    }

    public Segment(Segment source) {
        super(source);
        this.start = source.start;
        this.midRef = source.midRef;
        this.urnRef = source.urnRef;
    }

    public static Segment copy(Segment source) {
        if (source == null) {
            return null;
        }
        return new Segment(source);
    }


    @Override
    public boolean isActivation() {
        return getParent().isActivation() || super.isActivation();
    }

    @Override
    public boolean isDeactivation() {
        return getParent().isDeactivation() || super.isDeactivation();
    }

    @Override
    public boolean isPublishable() {
        if(parent != null && !parent.isPublishable()) {
            return false;
        }

        return super.isPublishable();
    }

    @Override
    public boolean isRevocable() {
        if(super.isRevocable()) {
            return true;
        }

        return getParent().isRevocable();
    }

    @Override
    public boolean isMerged() {
        return (parent != null && parent.isMerged()) || super.isMerged();
    }

    /**
     * Returns the parent {@link Program} of this segment. Not that this does not work directly after a simple unmarshall of
     * an individual segment because the full program object simply is not available then.
     *
     * Use {@link #getMidRef()} for the mid, and obtain it seperately.
     */
    @Override
    public Program getParent() {
        return parent;
    }

    @Override
    public void setParent(Program parent) {
        if(parent == null) {
            throw new IllegalArgumentException();
        }
        this.parent = parent;
        invalidateSortDate();
        this.midRef = null;
    }

    @XmlAttribute(required = true)
    public String getUrnRef() {
        if(parent != null) {
            return parent.getUrn();
        }

        return urnRef;
    }

    public void setUrnRef(String urnRef) {
        if(parent != null) {
            throw new IllegalStateException("This segments program holds the urnRef for this segment");
        }

        this.urnRef = urnRef;
    }

    /**
     * @since 1.9
     */
    @XmlAttribute(required = true)
    public String getMidRef() {
        if(parent != null) {
            return parent.getMid();
        }

        return midRef;
    }

    /**
     * @since 1.9
     */
    public void setMidRef(String midRef) {
        if(parent != null) {
            if (midRef != null) {
                if (parent.getMid().equals(midRef)) {
                    return;
                } else {
                    throw new IllegalStateException("This segments program holds the midRef for this segment");
                }
            }
        }
        this.midRef = midRef;
    }

    @Override
    protected String getUrnPrefix() {
        return SegmentType.URN_PREFIX;
    }

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    public java.time.Duration getStart() {
        return start;
    }

    public void setStart(java.time.Duration start) {
        this.start = start;
    }

    @Override
    public SortedSet<MediaObject> getAncestors() {
        final SortedSet<MediaObject> ancestors = super.getAncestors();

        if(parent != null) {
            ancestors.add(parent);
            ancestors.addAll(parent.getAncestors());
        }

        return ancestors;
    }

    @Override
    public boolean hasAncestor(MediaObject ancestor) {
        return (super.hasAncestor(ancestor) || parent != null) && parent.hasAncestor(ancestor);
    }

    @Override
    protected void findAncestry(MediaObject ancestor, List<MediaObject> ancestors) {
        super.findAncestry(ancestor, ancestors);

        if(ancestors.isEmpty() && parent != null) {
            parent.findAncestry(ancestor, ancestors);
            ancestors.add(parent);
        }
    }

    @Override
    public int compareTo(@Nonnull Segment o) {
        if(super.equals(o)) {
            return 0;
        }

        if(this.start != null && o.start != null) {
            int compare = this.start.compareTo(o.getStart());
            if (compare != 0) {
                return compare;
            }
        }
        if(this.type != null && o.type != null) {
            int compare = this.type.compareTo(o.getType());
            if (compare != 0) {
                return compare;
            }
        }
        {
            int compare = this.getMainTitle().compareTo(o.getMainTitle());
            if (compare != 0) {
                return compare;
            }

        }

        if (this.getMid() != null && o.getMid() != null) {
            int compare = this.getMid().compareTo(o.getMid());
            if (compare != 0) {
                return compare;
            }
        }
        if (this.getId() != null && o.getId() != null) {
            int compare = this.getId().compareTo(o.getId());
            if (compare != 0) {
                return compare;
            }
        }
        return o.hashCode() - hashCode();
    }



    @XmlAttribute(required = true)
    @NotNull
    @Override
    public SegmentType getType() {
        return type;
    }

    @Override
    public void setMediaType(MediaType type) {
        setType((SegmentType) type.getSubType());
    }

    public void setType(SegmentType segmentType) {
        if(segmentType == null) {
            segmentType = SegmentType.SEGMENT;
        }
        this.type = segmentType;
    }


    @Override
    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if(parent instanceof Program) {
            this.parent = (Program)parent;
            this.urnRef = null;
            this.midRef = null;
        }
    }

    @Override
    public String getCorrelationId() {
        if (parent != null) {
            return parent.getCorrelationId();
        }
        String midRef = getMidRef();
        if (midRef != null) {
            return midRef;
        } else {
            return super.getCorrelationId();
        }
    }
}
