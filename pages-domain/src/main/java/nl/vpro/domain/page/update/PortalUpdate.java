package nl.vpro.domain.page.update;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.domain.page.Portal;
import nl.vpro.domain.page.Section;
import nl.vpro.domain.page.validation.ValidPortal;
import nl.vpro.domain.user.PortalService;
import nl.vpro.domain.user.ServiceLocator;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
@XmlType(name = "portalUpdateType", propOrder = {"section"})
@XmlAccessorType(XmlAccessType.NONE)
public class PortalUpdate {

    private static final Logger LOG = LoggerFactory.getLogger(PortalUpdate.class);

    @ValidPortal
    @NotNull
    private String id;

    @NotNull
    private String url;

    @Valid
    private Section section;

    private PortalUpdate() {

    }

    public PortalUpdate(String id, String url) {
        this.id = id;
        this.url = url;
    }

    @XmlAttribute(required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(required = true)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlElement
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Portal toPortal() {
        PortalService portalService = ServiceLocator.getPortalService();
        nl.vpro.domain.user.Portal userPortal;
        if (portalService != null) {
            userPortal = portalService.find(getId());
            if (userPortal == null) {
                LOG.warn("Could not find portal " + getId() + " in " + portalService);
            }
        } else {
            LOG.warn("No portalService found!");
            userPortal = null;
        }
        Portal portal = new Portal(getId(), getUrl(), userPortal == null ? getId() : userPortal.getDisplayName());
        portal.setSection(getSection());
        return portal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PortalUpdate that = (PortalUpdate) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (section != null ? !section.equals(that.section) : that.section != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (section != null ? section.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return id + ":" + url;
    }
}
