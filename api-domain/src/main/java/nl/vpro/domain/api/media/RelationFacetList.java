/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.AbstractFacet;
import nl.vpro.domain.api.SearchableFacet;
import nl.vpro.domain.api.jackson.media.RelationFacetListJson;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaRelationFacetListType", propOrder = {"filter", "subSearch", "facets"})
@JsonSerialize(using = RelationFacetListJson.Serializer.class)
@JsonDeserialize(using = RelationFacetListJson.Deserializer.class)
public class RelationFacetList extends AbstractFacet<MediaSearch> implements SearchableFacet<RelationSearch>, Iterable<RelationFacet> {

    @Valid
    private MediaSearch filter;

    @Valid
    private RelationSearch subSearch;

    @XmlElement(name = "facet")
    @Valid
    protected List<RelationFacet> facets;

    public RelationFacetList() {
    }

    public RelationFacetList(List<RelationFacet> facets) {
        this.facets = facets;
    }

    @Override
    public MediaSearch getFilter() {
        return filter;
    }

    @Override
    public void setFilter(MediaSearch filter) {
        this.filter = filter;
    }

    @Override
    public RelationSearch getSubSearch() {
        return subSearch;
    }

    @Override
    public void setSubSearch(RelationSearch subSearch) {
        this.subSearch = subSearch;
    }

    /**
     * Use iterator if you want to initialise the facet names. Clients may supply there own custom name, but
     * this is optional
     */
    public List<RelationFacet> getFacets() {
        return facets;
    }

    public void setFacets(List<RelationFacet> facets) {
        this.facets = facets;
    }

    public boolean isEmpty() {
        return facets == null || facets.isEmpty();
    }

    public int size() {
        return facets == null ? 0 : facets.size();
    }

    @Override
    public boolean hasSubSearch() {
        return subSearch != null && subSearch.hasSearches();
    }

    @Override
    public Iterator<RelationFacet> iterator() {
        return new Iterator<RelationFacet>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return facets != null && index < facets.size();
            }

            @Override
            public RelationFacet next() {
                RelationFacet relationFacet = facets.get(index++);
                if(relationFacet.getName() == null) {
                    relationFacet.setName("relations" + index);
                }
                return relationFacet;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Unsupported");
            }
        };
    }
}
