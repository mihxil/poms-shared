package nl.vpro.domain.page;


import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.media.support.Displayable;
import nl.vpro.domain.page.util.Urls;

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
public class Portal implements Displayable {

    @NotNull
    @URL
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

        if(!url.equals(portal.url)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}

