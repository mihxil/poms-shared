/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.EnumConstraint;
import nl.vpro.domain.media.AVType;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "avTypeConstraintType")
public class AvTypeConstraint extends EnumConstraint<AVType, MediaObject> {

    public AvTypeConstraint() {
        super(AVType.class);
    }


    public AvTypeConstraint(AVType value) {
        super(AVType.class, value);
    }

    @Override
    public String getESPath() {
        return "avType";
    }

    @Override
    protected Collection<AVType> getEnumValues(MediaObject input) {
        return asCollection(input.getAVType());

    }

}
