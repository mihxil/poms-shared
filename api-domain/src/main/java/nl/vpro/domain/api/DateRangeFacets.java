/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.util.*;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.jackson.DateRangeFacetsToJson;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlType(name = "dateRangeFacetsType", propOrder = {
    "ranges"})
@XmlAccessorType(XmlAccessType.FIELD)
@JsonSerialize(using = DateRangeFacetsToJson.Serializer.class)
@JsonDeserialize(using = DateRangeFacetsToJson.Deserializer.class)
public class DateRangeFacets<T extends AbstractSearch> extends AbstractFacet<T> implements Facet<T> {

    @XmlElements({
        @XmlElement(name = "interval", type = DateRangeInterval.class),
        @XmlElement(name = "preset", type = DateRangePreset.class),
        @XmlElement(name = "range", type = DateRangeFacetItem.class)
    })
    @JsonIgnore
    private List<RangeFacet<Date>> ranges;

    public DateRangeFacets() {
    }

    @SafeVarargs
    public DateRangeFacets(RangeFacet<Date>... ranges) {
        if(ranges != null && ranges.length > 0) {
            this.ranges = Arrays.asList(ranges);
        }
    }

    public List<RangeFacet<Date>> getRanges() {
        return ranges;
    }

    public void setRanges(List<RangeFacet<Date>> ranges) {
        this.ranges = ranges;
    }

    @SafeVarargs
    public final void addRanges(RangeFacet<Date>... ranges) {
        if(this.ranges == null) {
            this.ranges = new ArrayList<>(ranges.length);
        }

        Collections.addAll(this.ranges, ranges);
    }

    @Override
    public T getFilter() {
        return this.filter;
    }

    @Override
    public void setFilter(T filter) {
        this.filter = filter;
    }
}
