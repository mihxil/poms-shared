package nl.vpro.domain.media;

import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class RelationDefinitionIdentifier implements Serializable {

    private String type;

    private String broadcaster;

    protected RelationDefinitionIdentifier() {
    }

    public RelationDefinitionIdentifier(String type, String broadcaster) {
        this.type = type.toUpperCase();
        this.broadcaster = broadcaster;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type.toUpperCase();
    }

    public String getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(String broadcaster) {
        this.broadcaster = broadcaster;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RelationDefinitionIdentifier other = (RelationDefinitionIdentifier) obj;
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if ((this.broadcaster == null) ? (other.broadcaster != null) : !this.broadcaster.equals(other.broadcaster)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 67 * hash + (this.broadcaster != null ? this.broadcaster.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "RelationDefinitionType " + broadcaster + "/" + type;
    }
}
