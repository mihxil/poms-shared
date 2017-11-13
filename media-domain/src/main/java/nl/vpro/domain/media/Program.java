package nl.vpro.domain.media;

import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import org.hibernate.annotations.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import nl.vpro.domain.TextualObjects;
import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.Portal;
import nl.vpro.domain.user.ThirdParty;
import nl.vpro.validation.Broadcast;

import static nl.vpro.domain.TextualObjects.sorted;

/**
 * The main feature that distinguishes a Program from a generic media entity is its ability
 * to become an episode of other media entities. This association type is a functional
 * equivalent of the memberOf association, but complementary, and has its own representation
 * in XML or JSON.
 * <p/>
 * A program can have a {@link nl.vpro.domain.media.ProgramType} when it's a movie or strand
 * program. A strand programs has the ability to become an episode of other strand programs
 * as opposed to strand groups.
 *
 * @author roekoe
 */
@Entity
@Broadcast
@XmlRootElement(name = "program")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "programType", propOrder = {
    "episodeOf",
    "segments",
    "poProgTypeLegacy"

})
@JsonTypeName("program")
public class Program extends MediaObject {
    private static final long serialVersionUID = 6174884273805175998L;

    // DRS I found that the 'hardcoded' mediaobject alias in the filter below changes when
    // relational fields are added; I had to change the alias from mediaobjec_9 to mediaobjec_11
    // when I added field publicationRule below. Needs to be fixed, not sure how...
    @OneToMany(orphanRemoval = true)
    @JoinTable(
        name = "program_episodeof",
        inverseJoinColumns = @JoinColumn(name = "id")
    )
    @org.hibernate.annotations.Cascade({
        org.hibernate.annotations.CascadeType.ALL
    })
    @SortNatural
    //@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)

    // TODO: These filters are EXTREMELY HORRIBLE, actually UNACCEPTABLE
    @FilterJoinTables({
        @FilterJoinTable(name = PUBLICATION_FILTER, condition =
            "((mediaobjec10_.publishstart is null or mediaobjec10_.publishstart < now())" +
                "and (mediaobjec10_.publishstop is null or mediaobjec10_.publishstop > now()))"),
        @FilterJoinTable(name = DELETED_FILTER, condition = "(mediaobjec10_.workflow NOT IN ('FOR_DELETION', 'DELETED') and (mediaobjec10_.mergedTo_id is null))")
    })
    protected Set<MemberRef> episodeOf = new TreeSet<>();

    @Size.List({@Size(max = 255), @Size(min = 1)})
    protected String poProgType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "no program type given")
    protected ProgramType type;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    @org.hibernate.annotations.Cascade({
        org.hibernate.annotations.CascadeType.ALL
    })
    //@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    // TODO: These filters are EXTREMELY HORRIBLE, actually UNACCEPTABLE
    @Filters({
        @Filter(name = PUBLICATION_FILTER, condition =
            "((segments0_1_.publishstart is null or segments0_1_.publishstart < now())" +
                "and (segments0_1_.publishstop is null or segments0_1_.publishstop > now()))"),

        @Filter(name = DELETED_FILTER, condition = "(segments0_1_.workflow NOT IN ('MERGED', 'FOR_DELETION', 'DELETED') and (segments0_1_.mergedTo_id is null))")
    })

    private Set<Segment> segments;



    public Program() {
        this(null, null);
    }

    public Program(long id) {
        super(id);
    }

    public Program(AVType avType, ProgramType type) {
        this.avType = avType;
        this.type = type;
    }

    public Program(Program source) {
        super(source);
        source.getEpisodeOf().forEach(ref -> this.createEpisodeOf((Group)ref.getOwner(), ref.getNumber()));
        source.getSegments().forEach(segment -> this.addSegment(Segment.copy(segment)));
        this.type = source.type;
        this.poProgType = source.poProgType;
    }

    public static Program copy(Program source) {
        if(source == null) {
            return null;
        }
        return new Program(source);
    }

    private static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public Boolean isEpisodeOfLocked() {
        if(episodeOf != null) {
            for(MemberRef memberRef : episodeOf) {
                MediaObject owner = memberRef.getOwner();
                if(owner instanceof Group && ((Group)owner).isEpisodesLocked()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    void addAncestors(SortedSet<MediaObject> set) {
        super.addAncestors(set);
        if (isEpisode()) {
            for (MemberRef memberRef : episodeOf) {
                final MediaObject reference = memberRef.getOwner();
                if (reference != null && !set.contains(reference)) {
                    set.add(reference);
                    set.addAll(reference.getAncestors());
                }
            }
        }
    }

    @XmlElement
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<MemberRef> getEpisodeOf() {
        if(this.episodeOf == null) {
            this.episodeOf = new TreeSet<>();
        }
        return sorted(episodeOf);
    }

    public void setEpisodeOf(SortedSet<MemberRef> episodeOf) {
        this.episodeOf = episodeOf;
    }

    public MemberRef findEpisodeOfRef(long refId) {
        for(MemberRef memberRef : episodeOf) {
            if(memberRef.getId().equals(refId)) {
                return memberRef;
            }
        }
        return null;
    }

    public MemberRef findEpisodeOfRef(MediaObject owner) {
        for(MemberRef memberRef : episodeOf) {
            if(memberRef.getOwner().equals(owner)) {
                return memberRef;
            }
        }
        return null;
    }

    public MemberRef findEpisodeOfRef(MediaObject owner, Integer number) {
        if(number == null) {
            return findEpisodeOfRef(owner);
        }

        for(MemberRef memberRef : episodeOf) {
            if(owner.equals(memberRef.getOwner()) && number.equals(memberRef.getNumber())) {
                return memberRef;
            }
        }
        return null;
    }

    public MemberRef findEpisodeOf(Long episodeRefId) {
        for(MemberRef episodeRef : episodeOf) {
            if(episodeRefId.equals(episodeRef.getId())) {
                return episodeRef;
            }
        }
        return null;
    }

    public boolean isEpisode() {
        return episodeOf != null && episodeOf.size() > 0;
    }

    public boolean isEpisodeOf(MediaObject owner) {
        if(episodeOf == null) {
            return false;
        }

        for(MemberRef memberRef : episodeOf) {
            if(memberRef.getOwner().equals(owner)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAncestor(MediaObject ancestor) {
        if(super.hasAncestor(ancestor)) {
            return true;
        }

        if(!isEpisode()) {
            return false;
        }

        for(MemberRef memberRef : episodeOf) {
            if(memberRef.getOwner().equals(ancestor) || memberRef.getOwner().hasAncestor(ancestor)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void findAncestry(MediaObject ancestor, List<MediaObject> ancestors) {
        super.findAncestry(ancestor, ancestors);

        if(ancestors.isEmpty() && isEpisode()) {
            for(MemberRef memberRef : episodeOf) {
                if(memberRef.getOwner().equals(ancestor)) {
                    ancestors.add(ancestor);
                    return;
                }

                memberRef.getOwner().findAncestry(ancestor, ancestors);
                if(!ancestors.isEmpty()) {
                    ancestors.add(memberRef.getOwner());
                    return;
                }
            }
        }
    }



    MemberRef createEpisodeOf(Group owner, Integer episodeNr) throws CircularReferenceException {
        if(owner == null) {
            throw new IllegalArgumentException("Must supply an owning group, not null.");
        }

        if(! ProgramType.EPISODES.contains(this.getType())) {
            throw new IllegalArgumentException(String.format("%1$s of type %2$s can not become an episode of %3$s with type %4$s ", this, this.getType(), owner, owner.getType()));
        }

        if(! owner.getType().canContainEpisodes()) {
            throw new IllegalArgumentException("Must supply a group type " + GroupType.EPISODE_CONTAINERS + " when adding episodes.");
        }

        if(owner.hasAncestor(this)) {
            throw new CircularReferenceException(owner, owner.findAncestry(this));
        }

        MemberRef memberRef = new MemberRef(this, owner, episodeNr);

        if(episodeOf == null) {
            episodeOf = new TreeSet<>();
        }

        episodeOf.add(memberRef);

        return memberRef;
    }

    boolean removeEpisodeOf(MediaObject owner) {
        boolean success = false;
        if(episodeOf != null) {
            Iterator<MemberRef> it = episodeOf.iterator();

            while(it.hasNext()) {
                MemberRef memberRef = it.next();

                if(memberRef.getOwner().equals(owner)) {
                    it.remove();
                    success = true;
                }
            }
        }
        return success;
    }

    boolean removeEpisodeOf(MemberRef memberRef) {
        if(episodeOf != null) {
            Iterator<MemberRef> it = episodeOf.iterator();

            while(it.hasNext()) {
                MemberRef existing = it.next();

                if(existing.equals(memberRef)) {
                    it.remove();
                    descendantOf = null;
                    return true;
                }
            }
        }
        return false;
    }

    Program addEpisodeOf(MemberRef memberRef) {
        if(memberRef == null || !memberRef.isValid()) {
            throw new IllegalArgumentException("Must supply a valid MemberRef. Got: " + (memberRef == null ? "NULL" : memberRef.toString()));
        }

        if(episodeOf == null) {
            episodeOf = new TreeSet<>();
        }

        episodeOf.add(memberRef);
        descendantOf = null;

        return this;
    }

    @Deprecated
    public String getEpisodeTitle() {
        return TextualObjects.get(titles, (String)null, TextualType.EPISODE);
    }

    @Deprecated
    public String getEpisodeDescription() {
        return TextualObjects.get(descriptions, (String)null, TextualType.EPISODE);
    }

    public String getPoProgType() {
        return poProgType;
    }

    @XmlElement(name = "poProgType")
    public String getPoProgTypeLegacy() {
        return null;
    }

    public void setPoProgTypeLegacy(String poProgType) {
        this.poProgType = (poProgType == null || poProgType.length() < 255) ? poProgType : poProgType.substring(255);
    }

    @XmlAttribute(required = true)
    @Override
    public ProgramType getType() {
        return type;
    }

    public void setType(ProgramType type) {
        this.type = type;
    }

    @XmlElementWrapper(name = "segments")
    @XmlElement(name = "segment")
    @JsonProperty("segments")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<Segment> getSegments() {
        if(segments == null) {
            segments = new TreeSet<>();
        }
        return sorted(segments);
    }

    public void setSegments(SortedSet<Segment> segments) {
        this.segments = segments;
    }

    public Segment findSegment(Long id) {
        if(segments == null) {
            return null;
        }

        for(Segment segment : segments) {
            if(id.equals(segment.getId())) {
                return segment;
            }
        }

        return null;
    }
    protected Optional<Segment> findSegment(Segment segment) {
        return getSegments().stream().filter(existing -> existing.equals(segment)).findFirst();
    }

    public Program addSegment(Segment segment) {
        if(segment != null) {
            segment.setParent(this);
            if(isEmpty(segment.getBroadcasters()) && !isEmpty(broadcasters)) {
                for(Broadcaster broadcaster : broadcasters) {
                    segment.addBroadcaster(broadcaster);
                }
            }
            if(isEmpty(segment.getPortals()) && !isEmpty(getPortals())) {
                for(Portal portal : getPortals()) {
                    segment.addPortal(portal);
                }
            }
            if(isEmpty(segment.getThirdParties()) && !isEmpty(getThirdParties())) {
                for(ThirdParty thirdParty : getThirdParties()) {
                    segment.addThirdParty(thirdParty);
                }
            }

            if(segments == null) {
                segments = new TreeSet<>();
            }
            segments.add(segment);
        }
        return this;
    }

    public boolean deleteSegment(Segment segment) {
        if(segments == null) {
            return false;
        }
        return findSegment(segment).map((existing) -> {
            existing.setWorkflow(Workflow.FOR_DELETION);
            return true;
            }
        ).orElse(false);
    }

    @Override
    protected String getUrnPrefix() {
        return ProgramType.URN_PREFIX;
    }



    @Override
    public String toString() {
        String mainTitle;
        try {
            String mt = getMainTitle();
            mainTitle = mt == null ? "null" : ('"' + mt + '"');
        } catch(RuntimeException le) {
            mainTitle = "[" + le.getClass() + " " + le.getMessage() + "]"; // (could be a LazyInitializationException)
        }
        return String.format("Program{%1$s mid=\"%2$s\", title=%3$s}", type == null ? "": type + " ", this.getMid(), mainTitle);
    }

    private String findPoSeriesID() {
        String poSeriesID = findPoSeriesID(episodeOf);
        return poSeriesID != null ? poSeriesID : findPoSeriesID(memberOf);
    }

    private String findPoSeriesID(Collection<MemberRef> memberRefs) {
        if(memberRefs == null) {
            return null;
        }

        for(MemberRef memberRef : memberRefs) {
            if(memberRef.getType() == MediaType.SEASON || memberRef.getType() == MediaType.SERIES) {
                return memberRef.getMidRef();
            }
        }

        return null;
    }


}

