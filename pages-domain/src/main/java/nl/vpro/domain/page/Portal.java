package nl.vpro.domain.page;


import lombok.Builder;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.Displayable;
import nl.vpro.domain.page.util.Urls;
import nl.vpro.validation.URI;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlType(name = "portalType", propOrder = {"displayName", "section"})
@XmlAccessorType(XmlAccessType.NONE)
@JsonPropertyOrder(
    {
        "id",
        "url",
        "value",
        "section"
    }
)
public class Portal implements Displayable, Serializable {

    @NotNull
    @URI
    private String url;

    @NotNull
    private String id;

    @NotNull
    private String displayName;

    private Section section;

    public Portal() {
    }


    public Portal(String id, String url, String displayName) {
        this.id = id;
        this.displayName = displayName;
        setUrl(url);
    }

    @Builder
    public Portal(String id, String url, String displayName, Section section) {
        this.id = id;
        this.displayName = displayName;
        setUrl(url);
        this.section = section;
    }
    @XmlAttribute(required = true)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = Urls.normalize(url);
    }

    @XmlAttribute(required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "name", required = true)
    @JsonProperty("value")
    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @XmlElement
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        if (section != null) {
            section.setPortal(this);
        }
        this.section = section;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Portal)) {
            return false;
        }

        Portal portal = (Portal)o;

        return url.equals(portal.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}

