/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import static nl.vpro.domain.Xmlns.SEARCH_NAMESPACE;

@SuppressWarnings("WSReferenceInspection")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaPagerType", namespace = SEARCH_NAMESPACE, propOrder = {
        "offset",
        "max",
        "sort",
        "order"
        })
public class MediaPager extends Pager<MediaSortField> {


    @lombok.Builder
    public MediaPager(long offset, Integer max, MediaSortField sort, Direction order) {
        super(offset, max, sort, order);
    }

    public MediaPager(Integer max) {
        this(0, max, null, Direction.ASC);
    }


    public MediaPager() {
        this(0, null, null, Direction.ASC);
    }

    @Override
    @XmlElement
    public MediaSortField getSort() {
        return super.getSort();
    }

    @Override
    public void setSort(MediaSortField sort) {
        super.setSort(sort);
    }

}
