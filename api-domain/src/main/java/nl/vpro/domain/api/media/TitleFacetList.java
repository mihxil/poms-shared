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

import nl.vpro.domain.api.SearchableFacet;
import nl.vpro.domain.api.jackson.media.TitleFacetListJson;

/**
 * @author lies
 * @since 5.5
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaTitleFacetListType", propOrder = {"subSearch", "facets"})
@JsonSerialize(using = TitleFacetListJson.Serializer.class)
@JsonDeserialize(using = TitleFacetListJson.Deserializer.class)
public class TitleFacetList
    extends MediaFacet /* extending MediaFacet is mainly done for backwards compatibility */
    implements SearchableFacet<TitleSearch>, Iterable<TitleFacet> {

    @Valid
    private TitleSearch subSearch;

    @Valid
    @XmlElement(name = "title")
    protected List<TitleFacet> facets;

    public TitleFacetList() {
        super(null, null, null);
    }

    public boolean asMediaFacet() {
        return !(filter != null || subSearch != null || facets != null);
    }

    public TitleFacetList(List<TitleFacet> facets) {
        this.facets = facets;
    }

    @Override
    public TitleSearch getSubSearch() {
        return subSearch;
    }

    @Override
    public void setSubSearch(TitleSearch subSearch) {
        this.subSearch = subSearch;
    }

    /**
     * Use iterator if you want to initialise the facet names. Clients may supply there own custom name, but
     * this is optional
     */
    public List<TitleFacet> getFacets() {
        return facets;
    }

    public void setFacets(List<TitleFacet> facets) {
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
    public Iterator<TitleFacet> iterator() {
        return new Iterator<TitleFacet>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return facets != null && index < facets.size();
            }

            @Override
            public TitleFacet next() {
                TitleFacet titleFacet = facets.get(index++);
                if (titleFacet.getName() == null) {
                    titleFacet.setName("titles" + index);
                }
                return titleFacet;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Unsupported");
            }
        };
    }
}
