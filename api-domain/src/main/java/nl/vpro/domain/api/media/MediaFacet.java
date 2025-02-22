/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.api.TextFacet;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "mediaFacetType")
public class MediaFacet extends TextFacet<MediaSearch> {
    public MediaFacet() {
    }

    public MediaFacet(Integer threshold, FacetOrder sort, Integer max) {
        super(threshold, sort, max);
    }
    @lombok.Builder
    private MediaFacet(Integer threshold, FacetOrder sort, Integer max, MediaSearch filter) {
        this(threshold, sort, max);
        this.filter = filter;
    }

    @XmlElement
    @Override
    public MediaSearch getFilter() {
        return this.filter;
    }

    @Override
    public void setFilter(MediaSearch filter) {
        this.filter = filter;
    }
}
