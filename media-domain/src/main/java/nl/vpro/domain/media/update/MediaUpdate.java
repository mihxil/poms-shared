/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.exceptions.ModificationException;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.Organization;
import nl.vpro.domain.user.Portal;
import nl.vpro.validation.StringList;
import nl.vpro.xml.bind.DurationXmlAdapter;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(
    name = "mediaUpdateType",
    propOrder = {
    "crids",
    "broadcasters",
    "portals",
    "portalRestrictions",
    "geoRestrictions",
    "titles",
    "descriptions",
    "tags",
    "genres",
    "avAttributes",
    "releaseYear",
    "duration",
    "persons",
    "memberOf",
    "ageRating",
    "contentRatings",
    "email",
    "websites",
    "locations",
    "scheduleEvents",
    "relations",
    "images",
    "asset"
})
@XmlSeeAlso({SegmentUpdate.class, ProgramUpdate.class, GroupUpdate.class})
public  abstract class MediaUpdate<M extends MediaObject> {

    private static final Logger LOG = LoggerFactory.getLogger(MediaUpdate.class);

    private static final Validator VALIDATOR;

    static {
        Validator validator;
        try {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        } catch (ValidationException ve) {
            LOG.info(ve.getClass().getName() + " " + ve.getMessage());
            validator = null;

        }
        VALIDATOR = validator;
    }



    public static <M extends MediaObject> MediaUpdate<M> create(M object) {
        if(object instanceof Program) {
            return (MediaUpdate<M>) ProgramUpdate.create((Program)object);
        } else if(object instanceof Group) {
            return (MediaUpdate<M>) GroupUpdate.create((Group)object);
        } else {
            return (MediaUpdate<M>) SegmentUpdate.create((Segment)object);
        }
    }

    @Valid
    protected MediaBuilder<?, M>  builder;


    @Valid
    protected List<ImageUpdate> images;

    @Valid
    protected Asset asset;

    private List<String> broadcasters;
    private List<String> portals;
    private SortedSet<String> tags;
    private List<PersonUpdate> persons;
    private List<PortalRestrictionUpdate> portalRestrictions;
    private List<GeoRestrictionUpdate> geoRestrictions;
    private SortedSet<TitleUpdate> titles;
    private SortedSet<DescriptionUpdate> descriptions;
    private SortedSet<String> genres;
    private SortedSet<MemberRefUpdate> memberOf;
    private List<String> websites;
    private SortedSet<LocationUpdate> locations;
    private SortedSet<RelationUpdate> relations;
    private SortedSet<ScheduleEventUpdate> scheduleEvents;


    private boolean imported = false;

    private final OwnerType owner;

    protected MediaUpdate() {
        this(OwnerType.BROADCASTER);
    }


    protected MediaUpdate(OwnerType type) {
        this.builder = null;
        this.owner = type;
    }

    protected <T extends MediaBuilder<T, M>> MediaUpdate(T builder) {
        this(builder, OwnerType.BROADCASTER);
    }

    protected <T extends MediaBuilder<T, M>> MediaUpdate(T builder, OwnerType type) {
        this.builder = builder;
        this.owner = type;

    }

    public boolean isValid() {
        return violations().isEmpty();
    }

    public Set<ConstraintViolation<MediaUpdate<M>>> violations() {
        fetch();
        if (VALIDATOR != null) {
            return VALIDATOR.validate(this);
        } else {
            LOG.warn("Cannot validate since no validator available");
            return Collections.emptySet();
        }
    }

    public String violationMessage() {
        Set<ConstraintViolation<MediaUpdate<M>>> violations = violations();
        if(violations.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder("List of constraint violations: [\n");
        for(ConstraintViolation violation : violations) {
            sb.append('\t')
                .append(violation.toString())
                .append('\n');
        }
        sb.append(']');
        return sb.toString();
    }

    @XmlTransient
    public abstract MediaUpdateConfig getConfig();

    public M fetch() {
        build().setCreationDate(null);
        if (notTransforming(broadcasters)) {
            build().setBroadcasters(broadcasters.stream().map(Broadcaster::new).collect(Collectors.toList()));
            broadcasters = null;
        }
        if (notTransforming(portals)) {
            build().setPortals(portals.stream().map(p -> new Portal(p, p)).collect(Collectors.toList()));
            portals = null;
        }
        if (notTransforming(tags)) {
            build().setTags(tags.stream().map(Tag::new).collect(Collectors.toCollection(TreeSet::new)));
            tags = null;
        }
        if (notTransforming(persons)) {
            build().setPersons(persons.stream().map(PersonUpdate::toPerson).collect(Collectors.toList()));
            persons = null;

        }
        if (notTransforming(portalRestrictions)) {
            build().setPortalRestrictions(portalRestrictions.stream().map(PortalRestrictionUpdate::toPortalRestriction).collect(Collectors.toList()));
            portalRestrictions = null;
        }
        if (notTransforming(geoRestrictions)) {
            build().setGeoRestrictions(geoRestrictions.stream().map(GeoRestrictionUpdate::toGeoRestriction).collect(Collectors.toList()));
            geoRestrictions = null;
        }
        if (notTransforming(titles)) {
            build().setTitles(titles.stream().map(t -> new Title(t.getTitle(), owner, t.getType())).collect(Collectors.toCollection(TreeSet::new)));
            titles = null;
        }
        if (notTransforming(descriptions)) {
            build().setDescriptions(descriptions.stream().map(d -> new Description(d.getDescription(), owner, d.getType())).collect(Collectors.toCollection(TreeSet::new)));
            descriptions = null;
        }
        if (notTransforming(websites)) {
            build().setWebsites(websites.stream().map(Website::new).collect(Collectors.toList()));
            websites = null;
        }
        if (notTransforming(genres)) {
            build().setWebsites(websites.stream().map(Website::new).collect(Collectors.toList()));
            websites = null;
        }
        if (notTransforming(memberOf)) {
            build().setMemberOf(memberOf.stream().map(this::toMemberRef).collect(Collectors.toCollection(TreeSet::new)));
            memberOf = null;
        }
        if (notTransforming(locations)) {
            build().setLocations(locations.stream().map(LocationUpdate::toLocation).collect(Collectors.toCollection(TreeSet::new)));
            locations = null;
        }
        if (notTransforming(relations)) {
            build().setRelations(relations.stream().map(RelationUpdate::toRelation).collect(Collectors.toCollection(TreeSet::new)));
            relations = null;
        }
        if (notTransforming(scheduleEvents)) {
            build().setScheduleEvents(scheduleEvents.stream().map(ScheduleEventUpdate::toScheduleEvent).collect(Collectors.toCollection(TreeSet::new)));
            scheduleEvents = null;
        }

        return build();
    }
    boolean notTransforming(Collection<?> col) {
        return col != null && !(col instanceof TransformingCollection);
    }

    M fetch(OwnerType owner) {
        M returnObject = fetch();
        MediaObjects.forOwner(returnObject, owner);
        return returnObject;
    }

    /**
     * Please use MediaUpdateService#fetch in stead.
     */
    M fetch(ImageImporter importer, OwnerType owner) {
        if(!imported && images != null) {
            for(ImageUpdate imageUpdate : images) {
                Image image = importer.save(imageUpdate);
                if(builder != null) {
                    builder.images(image);
                } else {
                    throw new RuntimeException("Both builder and media are NULL; therefore cannot add image");
                }
            }
        }

        imported = true;
        return fetch(owner);
    }


    /**
     * We will eventually support 'mid' id's. So this would be convenient.
     *
     * @since 1.5
     */
    @XmlAttribute
    @Size.List({@Size(max = 255), @Size(min = 4)})
    @Pattern(regexp = "^[ \\.a-zA-Z0-9_-]+$", flags = {Pattern.Flag.CASE_INSENSITIVE}, message = "{nl.vpro.constraints.mid}")
    public final String getMid() {
        return build().getMid();
    }

    /**
     * @since 1.8
     */
    public void setMid(String mid) {
        builder.mid(mid);
    }


    public SubMediaType getType() {
        return build().getType();
    }

    @XmlAttribute
    public Boolean isDeleted() {
        if (build().isDeleted()) {
            return Boolean.TRUE;
        }
        return null;
    }

    public void setDeleted(Boolean deleted) {
        if (deleted != null && deleted) {
            builder.workflow(Workflow.FOR_DELETION);
        }
    }

    @XmlAttribute
    public String getUrn() {
        if(build().getId() == null) {
            return null;
        }
        return build().getUrn();
    }


    public void setUrn(String s) {
        builder.urn(s);
    }

    @XmlAttribute(name = "avType")
    public AVType getAVType() {
        return build().getAVType();
    }

    public void setAVType(AVType avType) {
        builder.avType(avType);
    }

    @XmlAttribute
    public Boolean getEmbeddable() {
        return build().isEmbeddable();
    }

    public void setEmbeddable(Boolean isEmbeddable) {
        builder.embeddable(isEmbeddable);
    }

    @XmlAttribute
    public Date getPublishStart() {
        return build().getPublishStart();
    }

    public void setPublishStart(Date publishStart) {
        builder.publishStart(publishStart);
    }

    @XmlAttribute
    public Date getPublishStop() {
        return build().getPublishStop();
    }

    public void setPublishStop(Date publishStop) {
        builder.publishStop(publishStop);
    }

    @XmlElement(name = "crid")
    @StringList(pattern = "(?i)crid://.*/.*")
    public List<String> getCrids() {
        return build().getCrids();
    }

    public void setCrids(List<String> crids) {
        build().setCrids(crids);
    }

    @XmlElement(name = "broadcaster", required = true)
    public List<String> getBroadcasters() {
        if (broadcasters == null) {
            broadcasters = new TransformingList<>(build().getBroadcasters(),
                Organization::getId,
                Broadcaster::new
            );
        }
        return broadcasters;
    }

    public void setBroadcasters(List<String> broadcasters) {
        this.broadcasters = broadcasters;
    }

    public void setBroadcasters(String... broadcasters) {
        this.broadcasters = Arrays.asList(broadcasters);
    }

    @XmlElement(name = "portal", required = false)
    public List<String> getPortals() {
        if (portals == null) {
            portals  = new TransformingList<>(build().getPortals(),
                Organization::getId,
                p -> new Portal(p, p)
            );
        }
        return portals;
    }

    public void setPortals(List<String> portals) {
        this.portals = portals;
    }
    public void setPortals(String... portals) {
        this.portals = Arrays.asList(portals);
    }

    @XmlElement(name = "exclusive")
    public List<PortalRestrictionUpdate> getPortalRestrictions() {
        if (portalRestrictions == null) {
            portalRestrictions = new TransformingList<>(build().getPortalRestrictions(),
                PortalRestrictionUpdate::new,
                PortalRestrictionUpdate::toPortalRestriction
            );
        }
        return portalRestrictions;
    }

    public void setPortalRestrictions(List<PortalRestrictionUpdate> restrictions) {
        this.portalRestrictions = restrictions;
    }

    public void setPortalRestrictions(String... restrictions) {
        List<PortalRestrictionUpdate> updates = getPortalRestrictions();
        Stream.of(restrictions).forEach(r -> {
                updates.add(PortalRestrictionUpdate.of(r));
            }
        );
    }

    @XmlElement(name = "region")
    public List<GeoRestrictionUpdate> getGeoRestrictions() {
        if (geoRestrictions == null) {
            geoRestrictions = new TransformingList<>(build().getGeoRestrictions(),
                GeoRestrictionUpdate::new,
                GeoRestrictionUpdate::toGeoRestriction
            );
        }
        return geoRestrictions;
    }

    public void setGeoRestrictions(List<GeoRestrictionUpdate> restrictions) {
        this.geoRestrictions = restrictions;
    }

    @XmlElement(name = "title", required = true)
    public SortedSet<TitleUpdate> getTitles() {
        if (titles == null) {
            titles =
                new TransformingSortedSet<TitleUpdate, Title>(build().getTitles(),
                    t -> new TitleUpdate(t.getTitle(), t.getType(), MediaUpdate.this),
                    t -> new Title(t.getTitle(), owner, t.getType())
                ).filter(); // update object filter titles with same type different owner
        }
        return titles;
    }

    public void setTitles(SortedSet<TitleUpdate> titles) {
        this.titles = titles;
    }
    public void setTitles(TitleUpdate... titles) {
        this.titles = new TreeSet<>(Arrays.asList(titles));
    }
    public void setMainTitle(String title) {
        setTitle(title, TextualType.MAIN);
    }

    public void setTitle(String title, TextualType type) {
        for (TitleUpdate t : getTitles()) {
            if (t.getType() == type) {
                t.setTitle(title);
                return;
            }
        }
        getTitles().add(new TitleUpdate(title, type));
    }

    @XmlElement(name = "description")
    public SortedSet<DescriptionUpdate> getDescriptions() {
        if (descriptions == null) {
            descriptions = new TransformingSortedSet<DescriptionUpdate, Description>(build().getDescriptions(),
                d -> new DescriptionUpdate(d.getDescription(), d.getType(), MediaUpdate.this),
                d -> new Description(d.getDescription(), owner, d.getType())).filter();
        }
        return descriptions;
    }

    public void setDescriptions(SortedSet<DescriptionUpdate> descriptions) {
        this.descriptions = descriptions;
    }
    public void setDescriptions(DescriptionUpdate... descriptions) {
        this.descriptions = new TreeSet<>(Arrays.asList(descriptions));
    }

    public void setMainDescription(String description) {
        setDescription(description, TextualType.MAIN);
    }

    public void setDescription(String description, TextualType type) {
        for (DescriptionUpdate t : getDescriptions()) {
            if (t.getType() == type) {
                t.setDescription(description);
                return;
            }
        }
        getDescriptions().add(new DescriptionUpdate(description, type));
    }

    @XmlElement(name = "tag")
    public SortedSet<String> getTags() {
        if (tags == null) {
            tags = new TransformingSortedSet<>(build().getTags(),
                Tag::getText,
                Tag::new
            );
        }
        return tags;
    }

    public void setTags(SortedSet<String> tags) {
        this.tags = tags;
    }

    public void setTags(String... tags) {
        this.tags = new TreeSet<>(Arrays.asList(tags));
    }

    @XmlElement(name = "genre")
    public SortedSet<String> getGenres() {
        if (genres == null) {
            genres = new TransformingSortedSet<>(build().getGenres(),
                Genre::getTermId,
                Genre::new);
        }
        return genres;
    }

    public void setGenres(SortedSet<String> genres) {
        this.genres = genres;
    }

    public void setGenres(String... genres) {
        this.genres = new TreeSet<>(Arrays.asList(genres));
    }


    @XmlElement(name = "avAttributes")
    public AVAttributesUpdate getAvAttributes() {
        if(build().getAvAttributes() == null) {
            return null;
        }
        return new AVAttributesUpdate(build().getAvAttributes());
    }

    public void setAvAttributes(AVAttributesUpdate avAttributes) {
        builder.avAttributes(avAttributes == null ? null : avAttributes.toAvAttributes());
    }


    @XmlElement
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    public java.time.Duration getDuration() {
        Duration dur = build().getDuration();
        return dur == null ? null : dur.get();
    }

    @Deprecated
    public void setDuration(Date duration) throws ModificationException {
        builder.duration(duration);
    }

    public void setDuration(java.time.Duration duration) throws ModificationException {
        builder.duration(duration);
    }

    @XmlElement
    public Short getReleaseYear() {
        return build().getReleaseYear();
    }

    public void setReleaseYear(Short releaseYear) {
        builder.releaseYear(releaseYear);
    }

    @XmlElementWrapper(name = "credits")
    @XmlElement(name = "person")
    public List<PersonUpdate> getPersons() {
        if (persons == null && ! build().getPersons().isEmpty()) {
            persons = new TransformingList<>(build().getPersons(),
                PersonUpdate::new,
                PersonUpdate::toPerson);
        }
        return persons;
    }

    public void setPersons(List<PersonUpdate> persons) {
        this.persons = persons;
    }
    public void setPersons(PersonUpdate... persons){
        this.persons = new ArrayList<>(Arrays.asList(persons));
    }

    @XmlElement
    public SortedSet<MemberRefUpdate> getMemberOf() {
        if (memberOf == null) {
            memberOf = new TransformingSortedSet<>(build().getMemberOf(),
                MemberRefUpdate::create,
                this::toMemberRef
            );
        }
        return memberOf;
    }
    protected MemberRef toMemberRef(MemberRefUpdate m) {
        MemberRef ref = new MemberRef();
        ref.setMember(build());
        ref.setMediaRef(m.getMediaRef());
        ref.setNumber(m.getPosition());
        ref.setHighlighted(m.isHighlighted());
        ref.setAdded(null);
        return ref;
    }

    public void setMemberOf(SortedSet<MemberRefUpdate> memberOf) throws CircularReferenceException {
       this.memberOf = memberOf;
    }


    @XmlElement
    public AgeRating getAgeRating() {
        return build().getAgeRating();
    }

    public void setAgeRating(AgeRating ageRating) {
        builder.ageRating(ageRating);
    }


    @XmlElement(name = "contentRating")
    public List<ContentRating> getContentRatings() {
        return build().getContentRatings();
    }

    public void setContentRatings(List<ContentRating> list) {
        builder.contentRatings(list.toArray(new ContentRating[list.size()]));
    }


    @XmlElement
    public List<String> getEmail() {
        return build().getEmail();
    }

    public void setEmail(List<String> emails) {
        builder.emails(emails.toArray(new String[emails.size()]));
    }

    public void setEmail(String... emails) {
        builder.emails(emails);
    }

    @XmlElement(name = "website")
    public List<String> getWebsites() {
        if (websites == null) {
            websites = new TransformingList<>(build().getWebsites(),
                Website::getUrl,
                Website::new
            );
        }
        return websites;
    }

    public void setWebsites(List<String> websites) {
        this.websites = websites;
    }

    public void setWebsites(String... websites) {
        this.websites = new ArrayList<>(Arrays.asList(websites));
    }

    public void setWebsiteObjects(List<Website> websites) {
        build().setWebsites(websites);
        this.websites = null;
    }

    @XmlElementWrapper(name = "locations")
    @XmlElement(name = "location")
    public SortedSet<LocationUpdate> getLocations() {
        if (locations == null) {
            locations = new TransformingSortedSet<>(build().getLocations(),
                LocationUpdate::new,
                LocationUpdate::toLocation)
                .filter(l -> l.getOwner() == MediaUpdate.this.owner)  // MSE-2261
            ;
        }
        return locations;
    }

    public void setLocations(SortedSet<LocationUpdate> locations) {
        this.locations = locations;
    }
    public void setLocations(LocationUpdate... locations) {
        this.locations = new TreeSet<>(Arrays.asList(locations));
    }

    @XmlElementWrapper(name = "scheduleEvents")
    @XmlElement(name = "scheduleEvent")
    public Set<ScheduleEventUpdate> getScheduleEvents() {
        if (scheduleEvents == null) {
            scheduleEvents = new TransformingSortedSet<>(build().getScheduleEvents(),
                ScheduleEventUpdate::new,
                ScheduleEventUpdate::toScheduleEvent);
        }
        return scheduleEvents;
    }

    public void setScheduleEvent(ScheduleEventUpdate... events) {
        this.scheduleEvents = new TreeSet<>(Arrays.asList(events));
    }

    @XmlElement(name = "relation")
    public SortedSet<RelationUpdate> getRelations() {
        if (relations == null) {
            relations = new TransformingSortedSet<>(build().getRelations(),
                RelationUpdate::new,
                RelationUpdate::toRelation
            );
        }
        return relations;
    }

    public void setRelations(SortedSet<RelationUpdate> relations) {
        this.relations = relations;
    }

    @XmlElementWrapper(name = "images")
    @XmlElement(name = "image")
    public List<ImageUpdate> getImages() {
        if(images == null) {
            images = new ArrayList<>();
            for(Image image : build().getImages()) {
                if(this.owner == null || image.getOwner() == this.owner) { // MSE-2261
                    images.add(new ImageUpdate(image));
                }
            }
        }
        return images;
    }

    public void setImages(List<ImageUpdate> images) {
        // Leave builder.images to the fetch(ImageImporter) method
        this.images = images;
    }
    public void setImages(ImageUpdate... images) {
        this.images = Arrays.asList(images);
    }

    /**
     * Get asset containing the location source to be encoded.
     *
     * @return asset or null when unavailable
     * @since 2.1
     */
    @XmlElement(name = "asset")
    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    @XmlTransient
    public MediaBuilder<?, M> getBuilder() {
        return builder;
    }

    @Override
    public String toString() {
        return "update[" + builder + "]";
    }

    protected M build() {
        return builder.build();
    }
}
