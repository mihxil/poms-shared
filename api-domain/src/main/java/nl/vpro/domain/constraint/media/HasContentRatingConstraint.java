/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.ExistsConstraint;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Michiel Meeuwissen
 * @since 4.9
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "hasContentRatingConstraintType")
public class HasContentRatingConstraint implements ExistsConstraint<MediaObject> {

    @Override
    public String getESPath() {
        return "contentRatings";
    }

    @Override
    public boolean test(MediaObject input) {
        return input.getContentRatings() == null || input.getContentRatings().isEmpty();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
