/*
/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 * Creation date 3 nov 2008.
 */
package nl.vpro.domain.media;

import java.time.Instant;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.support.OwnerType;

@Entity
@Table(name = "group_table")
@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "groupType", propOrder = {
    "poSequenceInformation"
})
@JsonTypeName("group")
public class Group extends MediaObject {

    private static final long serialVersionUID = 1L;


    public static MediaBuilder.GroupBuilder builder() {
        return MediaBuilder.group();
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    protected GroupType type;

    @Column(nullable = false)
    @NotNull
    protected Boolean episodesLocked = false;

    @Column(nullable = false)
    @NotNull
    protected Boolean isOrdered = true;

    protected Long defaultElement;

    @Column
    @XmlElement
    protected String poSequenceInformation;

    public Group() {
    }

    public Group(long id) {
        super(id);
    }

    public Group(GroupType type) {
        this(null, type, true);
    }

    public Group(GroupType type, boolean isOrdered) {
        this(null, type, isOrdered);
    }

    public Group(AVType avType, GroupType type) {
        this(avType, type, true);
    }

    public Group(AVType avType, GroupType type, boolean isOrdered) {
        setAVType(avType);
        setType(type);
        setOrdered(isOrdered);
    }

    public Group(Group source) {
        super(source);
        this.type = source.type;
        this.isOrdered = source.isOrdered;
        this.episodesLocked = source.episodesLocked;
        this.defaultElement = source.defaultElement;
        this.poSequenceInformation = source.poSequenceInformation;
    }

    public static Group copy(Group source) {
        if(source == null) {
            return null;
        }
        return new Group(source);
    }

    MemberRef createMember(MediaObject member, OwnerType owner) throws CircularReferenceException {
        if(this.isOrdered) {
            throw new IllegalArgumentException("Can not add a member to an ordered group without supplying an ordering number.");
        }

        if(member.equals(this) || member.hasDescendant(this)) {
            throw new CircularReferenceException(this, findAncestry(member));
        }

        if(member.memberOf == null) {
            member.memberOf = new TreeSet<>();
        }

        MemberRef memberRef = new MemberRef(member, this, null, owner);
        member.memberOf.add(memberRef);
        return memberRef;
    }

    @Override
    MemberRef createMember(
        @Nonnull MediaObject member,
        Integer number,
        OwnerType owner) throws CircularReferenceException {
        if(number == null) {
            return createMember(member, owner);
        }

        return super.createMember(member, number, owner);
    }


    MemberRef createEpisode(Program episode, Integer episodeNumber, OwnerType owner) {
        return episode.createEpisodeOf(this, episodeNumber, owner);
    }

    @Override
    protected String getUrnPrefix() {
        return GroupType.URN_PREFIX;
    }


    @Override // to make it public
    public void setSortInstant(Instant date) {
        super.setSortInstant(date);
    }

    @XmlAttribute(required = true)
    @Override
    public GroupType getType() {
        return type;
    }

    @Override
    public void setMediaType(MediaType type) {
        setType((GroupType) type.getSubType());

    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public boolean isEpisodesLocked() {
        return episodesLocked != null ? episodesLocked : false;
    }

    public void setEpisodesLocked(boolean episodesLocked) {
        this.episodesLocked = episodesLocked;
    }

    @XmlAttribute(name = "isOrdered", required = true)
    @JsonProperty("isOrdered")
    public Boolean isOrdered() {
        return isOrdered;
    }

    public void setOrdered(Boolean ordered) {
        isOrdered = ordered;
    }

    @XmlAttribute
    public Long getDefaultElement() {
        return defaultElement;
    }

    public void setDefaultElement(Long defaultElement) {
        this.defaultElement = defaultElement;
    }

    public String getPoSequenceInformation() {
        return poSequenceInformation;
    }

    public void setPoSequenceInformation(String poSequenceInformation) {
        this.poSequenceInformation = poSequenceInformation;
    }

    public String getPoSeriesID() {
        return getMid();
    }

    public Group setPoSeriesID(String poSeriesID) {
        setMid(poSeriesID);
        return this;
    }

}
