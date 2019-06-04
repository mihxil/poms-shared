/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.com.neovisionaries.i18n.CountryCode;
import nl.vpro.domain.EmbargoDeprecated;
import nl.vpro.domain.Embargos;
import nl.vpro.domain.TextualObjectUpdate;
import nl.vpro.domain.TextualObjects;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.TwitterRef;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.bind.CountryCodeAdapter;
import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.exceptions.ModificationException;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.Portal;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.util.IntegerVersion;
import nl.vpro.util.IntegerVersionSpecific;
import nl.vpro.util.TimeUtils;
import nl.vpro.util.Version;
import nl.vpro.validation.*;
import nl.vpro.xml.bind.DurationXmlAdapter;
import nl.vpro.xml.bind.InstantXmlAdapter;
import nl.vpro.xml.bind.LocaleAdapter;


/**
 * A MediaUpdate is meant for communicating updates. It is not meant as a complete representation of the object.
 *
 * A MediaUpdate is like a {@link MediaObject} but
 * <ul>
 *  <li>It does not have {@link Ownable} objects. When converting between a MediaUpdate and a MediaObject one need to indicate for which owner type this must happen.
 *  If you are updating you are always associated with a certain owner (normally {@link OwnerType#BROADCASTER}), so there is no case for updating fields of other owners.
 * </li>
 * <li>It contains fewer implicit fields. E.g. a Broadcaster is just an id, and it does not contain a better string representation.
 *  These kind of fields are non modifiable, or are implicitely calculated. So there is no case in updating them.
 * </li>
 * <li>It may contain a 'version'
 * Some code may check this version to know whether certains fields ought to be ignored or not. This is to arrange forward and backwards compatibility.
 * It may e.g. happen that a newer version of POMS has a new field. If you are not aware of this, sending an update XML without the field may result in the value to be emptied.
 * To indicate that you <em>are</em> aware, you should sometimes supply a sufficiently high version.
 * </li>
 * </ul>
 *

 * As {@link MediaObject} it has three extenstions {@link ProgramUpdate}, {@link GroupUpdate} and {@link SegmentUpdate}
 *
 * @param <M>  The {@link MediaObject} extension this is for.
 *
 */

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
        "countries",
        "languages",
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
        "twitterrefs",
        "predictions",
        "locations",
        "scheduleEvents",
        "relations",
        "images",
        "asset"
})
@XmlSeeAlso({SegmentUpdate.class, ProgramUpdate.class, GroupUpdate.class})
@Slf4j
public abstract class  MediaUpdate<M extends MediaObject>
    implements
    EmbargoDeprecated,
    TextualObjectUpdate<TitleUpdate, DescriptionUpdate,  MediaUpdate<M>>,
    IntegerVersionSpecific,
    MediaIdentifiable {

    static final Validator VALIDATOR;

    static {
        Validator validator;
        try {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        } catch (ValidationException ve) {
            log.info(ve.getClass().getName() + " " + ve.getMessage());
            validator = null;

        }
        VALIDATOR = validator;
    }


    @SuppressWarnings("unchecked")
    public static <M extends MediaObject> MediaUpdate<M> create(M object, OwnerType owner) {
        MediaUpdate<M> created;
        if (object instanceof Program) {
            created = (MediaUpdate<M>) ProgramUpdate.create((Program) object, owner);
        } else if (object instanceof Group) {
            created = (MediaUpdate<M>) GroupUpdate.create((Group) object, owner);
        } else {
            created = (MediaUpdate<M>) SegmentUpdate.create((Segment) object, owner);
        }

        return created;
    }

    public static <M extends MediaObject> MediaUpdate<M> create(M object) {
        return create(object, OwnerType.BROADCASTER);
    }


    public static <M extends MediaObject> MediaUpdate<M> create(M object, OwnerType owner, IntegerVersion version) {
        MediaUpdate<M> update = create(object, owner);
        update.setVersion(version);
        return update;
    }

    @SuppressWarnings("unchecked")
    public static <M extends MediaObject, MB extends MediaBuilder<MB, M>> MediaUpdate<M> createUpdate(MB object, OwnerType ownerType) {
        return create(object.build(), ownerType);
    }


    protected IntegerVersion version;

    protected boolean xmlVersion = true;

    @Valid
    protected MediaObject mediaObjectToValidate;

    protected String mid;

    @Pattern(regexp = "^urn:vpro:media:(?:group|program|segment):[0-9]+$")
    protected String urn;

    private List<@CRID
        String> crids;

    protected AVType avType;

    protected Boolean embeddable;

    Boolean isDeleted;

    List<CountryCode> countries;

    List<Locale> languages;


    AVAttributesUpdate avAttributes;

    Instant publishStart;

    Instant publishStop;

    java.time.Duration duration;

    Short releaseYear;

    AgeRating ageRating;

    List<ContentRating> contentRatings;

    List<@Email String> email;

    protected List<ImageUpdate> images;

    @Valid
    protected Asset asset;

    private List<
        @NotNull
        @Size(min = 2, max = 4, message = "2 < id < 5")
        @javax.validation.constraints.Pattern(regexp = "[A-Z0-9_-]{2,4}", message = "Broadcaster id ${validatedValue} should match {regexp}")
            String> broadcasters;

    private List<
        @NotNull

        String
        > portals;

    private SortedSet<
        @NotNull
        @Size(min = 1, max = 255)
        String> tags;


    // jaxb annotations are here, because if on property, the credits wrapper will be marshalled always.
    // This arguably better, but for now we want to be backwards compatible.
    @XmlElementWrapper(name = "credits")
    @XmlElement(name = "person")
    private List<PersonUpdate> persons;

    private List<PortalRestrictionUpdate> portalRestrictions;

    private SortedSet<GeoRestrictionUpdate> geoRestrictions;

    private SortedSet<TitleUpdate> titles;

    private SortedSet<DescriptionUpdate> descriptions;

    private SortedSet<
        @NotNull
        @Pattern(regexp = "3\\.([0-9]+\\.)*[0-9]+")
        String> genres;

    private SortedSet<MemberRefUpdate> memberOf;

    private List<
        @URI(message = "{nl.vpro.constraints.URI}")
        @Size.List({
            @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
            @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
        })
        String> websites;


    private List<@Pattern(message = "{nl.vpro.constraints.twitterRefs.Pattern}", regexp="^[@#][A-Za-z0-9_]{1,139}$")
        String> twitterrefs;

    private SortedSet<LocationUpdate> locations;

    private SortedSet<RelationUpdate> relations;


    private SortedSet<ScheduleEventUpdate> scheduleEvents;

    protected SortedSet<PredictionUpdate> predictions;



    @Getter
    boolean imported = false;


    protected MediaUpdate() {
        fillFromMedia(newMedia(), OwnerType.BROADCASTER);
    }


    protected MediaUpdate(M mediaobject, OwnerType ownerType) {
        fillFromMedia(mediaobject, ownerType);
        fillFrom(mediaobject, ownerType);
    }

    protected final void fillFromMedia(M mediaobject, OwnerType owner) {
        this.mid = mediaobject.getMid();
        this.urn = mediaobject.getUrn();
        this.crids = mediaobject.getCrids();

        this.avType = mediaobject.getAVType();
        this.embeddable = mediaobject.isEmbeddable();
        this.isDeleted = mediaobject.isDeleted();

        this.countries = mediaobject.getCountries();
        this.languages = mediaobject.getLanguages();

        this.avAttributes = AVAttributesUpdate.of(mediaobject.getAvAttributes());

        Embargos.copy(mediaobject, this);

        this.duration = AuthorizedDuration.duration(mediaobject.getDuration());

        this.releaseYear = mediaobject.getReleaseYear();
        this.ageRating = mediaobject.getAgeRating();
        this.contentRatings = mediaobject.getContentRatings();
        this.email = mediaobject.getEmail();

        this.images = toList(
            mediaobject.getImages(),
            (i) -> i.getOwner() == owner,
            ImageUpdate::new,
            false)
        ;
        // asset?

        this.broadcasters = toList(mediaobject.getBroadcasters(), Broadcaster::getId);
        this.portals = toList(mediaobject.getPortals(), Portal::getId);

        this.tags = toSet(mediaobject.getTags(), Tag::getText);
        this.persons = toList(MediaObjects.getPersons(mediaobject), PersonUpdate::new, true);

        this.portalRestrictions = toList(mediaobject.getPortalRestrictions(), PortalRestrictionUpdate::new);
        this.geoRestrictions= toSet(mediaobject.getGeoRestrictions(), GeoRestrictionUpdate::new);


        if (owner == null || owner == OwnerType.BROADCASTER) {
            TextualObjects.copyToUpdate(mediaobject, this);
        } else {
            TextualObjects.copyToUpdate(mediaobject, this, owner);
        }

        this.genres = toSet(mediaobject.getGenres(), Genre::getTermId);
        this.memberOf = toSet(mediaobject.getMemberOf(), MemberRefUpdate::create);
        this.websites = toList(mediaobject.getWebsites(), Website::get);
        this.twitterrefs= toList(mediaobject.getTwitterRefs(), TwitterRef::get);

        this.locations = toSet(mediaobject.getLocations(), (l) -> l.getOwner() == owner, LocationUpdate::new);
        this.relations = toSet(mediaobject.getRelations(), RelationUpdate::new);
        this.scheduleEvents = toSet(mediaobject.getScheduleEvents(), (s) -> new ScheduleEventUpdate(this, s));
        this.predictions = toSet(mediaobject.getPredictions(), Prediction::isPlannedAvailability,PredictionUpdate::of);

    }

    protected abstract void fillFrom(M mediaObject, OwnerType ownerType);


    @Override
    @XmlTransient
    public IntegerVersion getVersion() {
        return version;
    }
    @Override
    public void setVersion(IntegerVersion version) {
        this.version = version;
    }

    @XmlAttribute(name = "version")
    protected String getVersionAttribute() {
        if (xmlVersion) {
            Version version = getVersion();
            return version == null ? null : version.toString();
        } else {
            return null;
        }
    }
    protected void setVersionAttribute(String version) {
        setVersion(Version.parseIntegers(version));
    }


    public boolean isValid() {
        return violations().isEmpty();
    }

    public Set<? extends ConstraintViolation<MediaUpdate<M>>> violations(Class<?>... groups) {
        if (VALIDATOR != null) {
            if (groups.length == 0) {
                groups = new Class<?>[]{
                    Default.class, PomsValidatorGroup.class
                };
            }
            Set<? extends ConstraintViolation<MediaUpdate<M>>> result = VALIDATOR.validate(this, groups);
            if (result.isEmpty()) {
                fetch(OwnerType.BROADCASTER);
                mediaObjectToValidate = fetch(OwnerType.BROADCASTER);
                try {
                    result = VALIDATOR.validate(this, groups);
                    if (result.isEmpty()) {
                        log.debug("validates");
                    }
                    return result;
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                    return Collections.emptySet();
                }
            }
            return result;
        } else {
            log.warn("Cannot validate since no validator available");
            return Collections.emptySet();
        }
    }

    public String violationMessage() {
        Set<? extends ConstraintViolation<? extends MediaUpdate<M>>> violations = violations();
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

    protected abstract M newMedia();

    private final M fetchOwnerless() {
        M media = newMedia();
        media.setUrn(urn);
        media.setMid(mid);
        media.setCreationInstant(null); //   not supported by update format. will be set by persistence layer
        media.setCrids(crids);
        media.setAVType(avType);
        media.setEmbeddable(embeddable);
        media.setCountries(countries);
        media.setLanguages(languages);

        media.setAvAttributes(AVAttributesUpdate.toAvAttributes(avAttributes));

        Embargos.copy(this, media);

        try {
            media.setDuration(duration);
        } catch(ModificationException mfe) {
            log.error(mfe.getMessage());
        }
        media.setReleaseYear(releaseYear);
        media.setAgeRating(ageRating);
        media.setContentRatings(contentRatings);
        media.setEmail(email);

        // images have owner

        media.setBroadcasters(toList(broadcasters, Broadcaster::new));

        media.setPortals(toList(portals, Portal::new));
        media.setTags(toSet(tags, Tag::new));
        media.setPersons(toList(persons, PersonUpdate::toPerson, true));
        media.setPortalRestrictions(toList(portalRestrictions, PortalRestrictionUpdate::toPortalRestriction));
        media.setGeoRestrictions(toSet(geoRestrictions, GeoRestrictionUpdate::toGeoRestriction));

        // titles have owner,

        media.setGenres(toSet(genres, Genre::new));
        media.setMemberOf(toSet(memberOf, this::toMemberRef));

        // locations are owned

        media.setRelations(toSet(relations, RelationUpdate::toRelation));

        // scheduleevents are owned

        media.setPredictions(toSet(predictions, PredictionUpdate::toPrediction));

        if (isDeleted == Boolean.TRUE) {
            MediaObjects.markForDeletion(media, "");
        } else {
            MediaObjects.markForRepublication(media, "");
        }
        return media;
    }

    public M fetch(OwnerType owner) {
        M returnObject = fetchOwnerless();
        TextualObjects.copy(this, returnObject, owner);
        for (Location l : toSet(locations, l -> l.toLocation(owner))) {
            returnObject.addLocation(l);
        }
        Predicate<Image> imageFilter = isImported() ? (i) -> i.getImageUri() != null : (i) -> true;
        returnObject.setImages(toList(images, i -> i.toImage(owner)).stream()
                .filter(imageFilter)
                .collect(Collectors.toList()));

        returnObject.setWebsites(toList(websites, (w) -> new Website(w, owner)));
        returnObject.setTwitterRefs(toList(twitterrefs, (t) -> new TwitterRef(t, owner)));

        returnObject.setScheduleEvents(toSet(scheduleEvents, s -> {
            ScheduleEvent e = s.toScheduleEvent(owner);
            e.setParent(returnObject);
            return e;
        }));
        return returnObject;
    }


    public M fetch() {
        return fetch(OwnerType.BROADCASTER);
    }


    protected <T, U> List<T> toList(List<U> list, Predicate<U> filter, Function<U, T> mapper, boolean nullToNull) {
        if (list == null) {
            if (nullToNull) {
                return null;
            }
            list = new ArrayList<>();
        }
        return list
            .stream()
            .filter(filter)
            .map(mapper)
            .collect(Collectors.toList());
    }
    protected <T, U> List<T> toList(List<U> list, Function<U, T> mapper, boolean nullToNull) {
        return toList(list, (u) -> true, mapper, nullToNull);
    }

    protected <T, U> List<T> toList(List<U> list, Function<U, T> mapper) {
        return toList(list, (u) -> true, mapper, false);
    }





    protected <T, U> TreeSet<T> toSet(Set<U> list, Predicate<U> filter, Function<U, T> mapper) {
        if (list == null) {
            list = new TreeSet<>();
        }
        return list
            .stream()
            .filter(filter)
            .map(mapper)
            .collect(Collectors.toCollection(TreeSet::new));
    }
     protected <T, U> TreeSet<T> toSet(Set<U> list, Function<U, T> mapper) {
         return toSet(list, (u) -> true, mapper);
     }






    /**
     *
     * @since 1.5
     */
    @XmlAttribute
    @Size.List({@Size(max = 255), @Size(min = 4)})
    @Pattern(regexp = "^[ \\.a-zA-Z0-9_-]+$", flags = {Pattern.Flag.CASE_INSENSITIVE}, message = "{nl.vpro.constraints.mid}")
    @Override
    public final String getMid() {
        return mid;
    }

    /**
     * @since 1.8
     */
    public void setMid(String mid) {
        this.mid = mid;
    }



    public abstract SubMediaType getType();

    /**
     * @since 5.6
     */
    @Override
    public final MediaType getMediaType() {
        SubMediaType subMediaType = getType();
        return subMediaType == null ? null : subMediaType.getMediaType();
    }


    @XmlAttribute
    public Boolean isDeleted() {
        return isDeleted ? true : null;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @XmlAttribute
    public String getUrn() {
        return urn;
    }


    public void setUrn(String s) {
        this.urn = s;
    }

    @XmlTransient
    @Override
    public Long getId() {
        String urn = getUrn();
        if (urn == null) {
            return null;
        }
        return Long.valueOf(urn.substring(getUrnPrefix().length() + 1));
    }

    void setId(Long id) {
        setUrn(getUrnPrefix() + id);
    }

    protected abstract String getUrnPrefix();


    @XmlAttribute(name = "avType")
    public AVType getAVType() {
        return avType;
    }

    public void setAVType(AVType avType) {
        this.avType = avType;
    }

    @XmlAttribute
    public Boolean getEmbeddable() {
        return embeddable;
    }

    public void setEmbeddable(Boolean isEmbeddable) {
        this.embeddable = isEmbeddable;
    }

    @Override
    @XmlAttribute(name = "publishStart")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getPublishStartInstant() {
        return publishStart;
    }

    @Nonnull
    @Override
    public MediaUpdate<M> setPublishStartInstant(Instant publishStart) {
        this.publishStart = publishStart;
        return this;
    }

    @Override
    @XmlAttribute(name = "publishStop")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getPublishStopInstant() {
        return publishStop;
    }

    @Nonnull
    @Override
    public MediaUpdate<M> setPublishStopInstant(Instant publishStop) {
        this.publishStop = publishStop;
        return this;
    }

    @XmlElement(name = "crid")
    @StringList(pattern = "(?i)crid://.*/.*", maxLength = 255)
    @Override
    @Nonnull
    public List<String> getCrids() {
        if (crids == null) {
            crids = new ArrayList<>();
        }
        return crids;
    }

    public void setCrids(List<String> crids) {
        this.crids = crids;
    }

    @XmlElement(name = "broadcaster", required = true)
    @Nonnull
    public List<String> getBroadcasters() {
        if (broadcasters == null) {
            broadcasters = new ArrayList<>();
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
    @Nonnull
    public List<String> getPortals() {
        if (portals == null) {
            portals = new ArrayList<>();
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
    @Valid
    public List<PortalRestrictionUpdate> getPortalRestrictions() {
        if (portalRestrictions == null) {
            portalRestrictions = new ArrayList<>();
        }
        return portalRestrictions;
    }

    public void setPortalRestrictions(List<PortalRestrictionUpdate> restrictions) {
        this.portalRestrictions = restrictions;
    }

    public void setPortalRestrictions(String... restrictions) {
        List<PortalRestrictionUpdate> updates = getPortalRestrictions();
        Stream.of(restrictions).forEach(r -> updates.add(PortalRestrictionUpdate.of(r)));
    }

    @XmlElement(name = "region")
    @Valid
    @Nonnull
    public SortedSet<GeoRestrictionUpdate> getGeoRestrictions() {
         if (geoRestrictions == null) {
             geoRestrictions = new TreeSet<>();
         }
        return geoRestrictions;
    }

    public void setGeoRestrictions(SortedSet<GeoRestrictionUpdate> restrictions) {
        this.geoRestrictions = restrictions;
    }

    @Override
    @XmlElement(name = "title", required = true)
    @Valid
    @NotNull
    @Nonnull
    @Size(min = 1)
    public SortedSet<TitleUpdate> getTitles() {
        if (titles == null) {
            titles = new TreeSet<>();
        }
        return titles;
    }
    @Override
    public void setTitles(SortedSet<TitleUpdate> titles) {
        this.titles = titles;
    }
    public void setTitles(TitleUpdate... titles) {
        this.titles = new TreeSet<>(Arrays.asList(titles));
    }

    @Override
    @XmlElement(name = "description")
    @Valid
    @Nonnull
    public SortedSet<DescriptionUpdate> getDescriptions() {
        if (descriptions == null) {
            descriptions = new TreeSet<>();
         }
        return descriptions;
    }

    @Override
    public void setDescriptions(SortedSet<DescriptionUpdate> descriptions) {
        this.descriptions = descriptions;
    }
    public void setDescriptions(DescriptionUpdate... descriptions) {
        this.descriptions = new TreeSet<>(Arrays.asList(descriptions));
    }

    @Override
    public BiFunction<String, TextualType, TitleUpdate> getTitleCreator() {
        return TitleUpdate::new;
    }

    @Override
    public BiFunction<String, TextualType, DescriptionUpdate> getDescriptionCreator() {
        return DescriptionUpdate::new;
    }

    @XmlElement(name = "tag")
    public SortedSet<String> getTags() {
        if (tags == null) {
            tags = new TreeSet<>();
        }
        return tags;
    }

    public void setTags(SortedSet<String> tags) {
        this.tags = tags;
    }

    public void setTags(String... tags) {
        this.tags = new TreeSet<>(Arrays.asList(tags));
    }

    @XmlElement(name = "country")
    @XmlJavaTypeAdapter(CountryCodeAdapter.Code.class)
    public List<CountryCode> getCountries() {
         if (countries == null) {
            countries = new ArrayList<>();
         }
        return countries;
    }

    public void setCountries(List<CountryCode> countries) {
        this.countries = countries;
    }

    @XmlElement(name = "language")
    @XmlJavaTypeAdapter(value = LocaleAdapter.class)
    public List<Locale> getLanguages() {
         if (languages == null) {
            languages = new ArrayList<>();
         }
        return languages;
    }
    public void setLanguages(List<Locale> languages) {
        this.languages = languages;
    }

    @XmlElement(name = "genre")
    @StringList(pattern = "3\\.([0-9]+\\.)*[0-9]+", maxLength = 255)
    @Nonnull
    public SortedSet<String> getGenres() {
        if (genres == null) {
            genres = new TreeSet<>();
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
        return avAttributes;
    }

    public void setAvAttributes(AVAttributesUpdate avAttributes) {
        this.avAttributes = avAttributes;
    }


    @XmlElement
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    public java.time.Duration getDuration() {
        return duration;
    }

    @Deprecated
    public void setDuration(Date duration) {
        this.duration = TimeUtils.durationOf(duration).orElse(null);
    }

    public void setDuration(java.time.Duration duration) {
        this.duration = duration;
    }

    @XmlElement
    public Short getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Short releaseYear) {
        this.releaseYear = releaseYear;
    }

    @Valid
    @Nonnull
    @XmlTransient
    public List<PersonUpdate> getPersons() {
        if (persons == null) {
            persons = new ArrayList<>();
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
    @Nonnull
    public SortedSet<MemberRefUpdate> getMemberOf() {
        if (memberOf == null) {
            memberOf = new TreeSet<>();
        }
        return memberOf;
    }

    protected MemberRef toMemberRef(MemberRefUpdate m) {
        MemberRef ref = new MemberRef();
        //ref.setMember(fetch(OwnerType.BROADCASTER));
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
    @NotNull(groups = {WarningValidatorGroup.class })
    public AgeRating getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(AgeRating ageRating) {
        this.ageRating = ageRating;
    }


    @XmlElement(name = "contentRating")
    @Nonnull
    public List<ContentRating> getContentRatings() {
        if (contentRatings == null) {
            contentRatings = new ArrayList<>();
        }
        return contentRatings;
    }

    public void setContentRatings(List<ContentRating> contentRatings) {
        this.contentRatings = contentRatings;
    }


    @XmlElement
    @Nonnull
    public List<String> getEmail() {
        if (email == null) {
             email = new ArrayList<>();
        }
        return email;
    }

    public void setEmail(List<String> emails) {
        this.email = emails;
    }

    public void setEmail(String... emails) {
        this.email = new ArrayList<>(Arrays.asList(emails));
    }

    @XmlElement(name = "website")
    @Nonnull
    public List<String> getWebsites() {
        if (websites == null) {
             websites = new ArrayList<>();
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
        this.websites = websites.stream()
            .map(Website::getUrl)
            .collect(Collectors.toList());
    }


    @XmlElement(name = "twitterref")
    @Nonnull
    public List<String> getTwitterrefs() {
        if (twitterrefs == null) {
             twitterrefs = new ArrayList<>();
        }
        return twitterrefs;
    }

    public void setTwitterRefs(List<String> twitterRefs) {
        this.twitterrefs = twitterRefs;
    }



    /**
     * @since 5.6
     */
    @XmlElement(name = "prediction")
    @Valid
    @Nonnull
    public SortedSet<PredictionUpdate> getPredictions() {
        if (predictions == null) {
            predictions = new TreeSet<>();
        }
        return predictions;
    }


    /**
     * @since 5.6
     */
    public void setPredictions(SortedSet<PredictionUpdate> predictions) {
        this.predictions = predictions;
    }

    @XmlElementWrapper(name = "locations")
    @XmlElement(name = "location")
    @Valid
    @Nonnull
    public SortedSet<LocationUpdate> getLocations() {
        if (locations == null) {
            locations = new TreeSet<>();
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
    @Nonnull
    public SortedSet<ScheduleEventUpdate> getScheduleEvents() {
        if (scheduleEvents == null) {
            scheduleEvents = new TreeSet<>();
        }
        return scheduleEvents;
    }

    public void setScheduleEvent(ScheduleEventUpdate... events) {
        this.scheduleEvents = new TreeSet<>(Arrays.asList(events));
    }

    @XmlElement(name = "relation")
    @Nonnull
    public SortedSet<RelationUpdate> getRelations() {
        if (relations == null) {
            relations = new TreeSet<>();
        }
        return relations;
    }

    public void setRelations(SortedSet<RelationUpdate> relations) {
        this.relations = relations;
    }

    @XmlElementWrapper(name = "images")
    @XmlElement(name = "image")
    @Valid
    @Nonnull
    public List<ImageUpdate> getImages() {
        if (images == null) {
            images = new ArrayList<>();
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
    @Nullable
    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    @Override
    public String toString() {
        return (isDeleted == Boolean.TRUE ? "DELETED:" : "") + getClass().getSimpleName() + "[" + getType() + ":" + mid + ":" + getMainTitle() + "]";
    }



    void afterUnmarshal(Unmarshaller u, Object parent) {
        if (parent != null) {
            if (parent instanceof IntegerVersionSpecific) {
                version = ((IntegerVersionSpecific) parent).getVersion();
                xmlVersion = false;
            }
        }
    }

    void beforeMarshal(Marshaller marshaller) {
        log.debug("Before");
    }

}
