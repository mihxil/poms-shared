/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.Group;
import nl.vpro.domain.media.GroupType;
import nl.vpro.domain.media.support.OwnerType;

@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "groupUpdateType",
    propOrder = {
        "poSeriesID"
        })
public final class GroupUpdate extends MediaUpdate<Group> {


    private GroupType groupType;

    private Boolean ordered = true;


    private GroupUpdate() {
        super();
    }


    private GroupUpdate(Group group, OwnerType ownerType) {
        super(group, ownerType);
    }

    public static GroupUpdate create(Group group, OwnerType ownerType) {
        return new GroupUpdate(group, ownerType);
    }

    public static GroupUpdate create(Group group) {
        return create(group, OwnerType.BROADCASTER);
    }


    public static GroupUpdate create() {
        return new GroupUpdate();
    }

    @Override
    protected void fillFrom(Group mediaObject, OwnerType ownerType) {
        this.groupType = mediaObject.getType();
        this.ordered = mediaObject.isOrdered();
    }


    @Override
    public Group fetch(OwnerType ownerType) {
        Group group = super.fetch(ownerType);
        group.setType(groupType);
        group.setOrdered(ordered);
        return group;

    }

    @Override
    protected Group newMedia() {
        return new Group();

    }

    @XmlAttribute
    @Override
    public GroupType getType() {
        return groupType;
    }

    @Override
    protected String getUrnPrefix() {
        return GroupType.URN_PREFIX;

    }

    public void setType(GroupType type) {
        this.groupType = type;
    }

    @XmlAttribute
    public Boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(Boolean ordered) {
        this.ordered = ordered;
    }


    @XmlElement
    public String getPoSeriesID() {
        return getMid();
    }

    public void setPoSeriesID(String poSeriesID) {
        setMid(poSeriesID);
    }
}
