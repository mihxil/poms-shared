package nl.vpro.domain.api.media;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.api.NameableSearchableFacet;


/**
 * @author lies
 * @since 5.5
 */


@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "mediaTitleFacetType", propOrder = {"name", "subSearch"})
@JsonPropertyOrder({"name","subSearch"})
public class TitleFacet implements NameableSearchableFacet<TitleSearch>  {

    private String name;

    @XmlAttribute
    private Boolean caseSensitive;


    @Valid
    private TitleSearch subSearch;

    public TitleFacet() {
    }


    @Override
    public boolean hasSubSearch() {
        return subSearch != null && subSearch.hasSearches();
    }

    @Override
    @XmlElement
    public TitleSearch getSubSearch() {
        return subSearch;
    }

    @Override
    public void setSubSearch(TitleSearch subSearch) {
        this.subSearch = subSearch;
    }

    @XmlAttribute
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }


    public Boolean getCaseSensitive() {
        return caseSensitive;
    }
    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    @XmlTransient
    public boolean isCaseSensitive() {
        return caseSensitive == null || caseSensitive;
    }

}
