package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.*;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.user.Broadcaster;

@Entity
@IdClass(RelationDefinitionIdentifier.class)
@FilterDefs({
    @FilterDef(name = "broadcasterFilter", parameters = {
        @ParamDef(name = "broadcasters", type = "string")})
})
@Filters({
    @Filter(name = "broadcasterFilter",
        condition = "broadcaster in (:broadcasters)")
})
public class RelationDefinition implements Serializable, Identifiable<RelationDefinitionIdentifier> {

    public static RelationDefinition of(String type, Broadcaster broadcaster) {
        return new RelationDefinition(type, broadcaster.getId());
    }
    public static RelationDefinition of(String type, String broadcaster) {
        return new RelationDefinition(type, broadcaster);
    }

    @Id
    @NotNull(message = "{nl.vpro.constraints.NotEmpty}")
    @Pattern(regexp = "[A-Z0-9_-]{4,}", message = "{nl.vpro.constraints.relationDefinition.Pattern}")
    @Getter
    private String type;

    @Id
    @NotNull(message = "{nl.vpro.constraints.NotEmpty}")
    @Size(min = 1)
    @Getter
    @Setter
    private String broadcaster;

    @Getter
    @Setter
    private String displayText;

    protected RelationDefinition() {
    }

    public RelationDefinition(String type, String broadcaster) {
        this(type, broadcaster, null);
    }

    @lombok.Builder
    public RelationDefinition(String type, String broadcaster, String displayText) {
        setType(type);
        this.broadcaster = broadcaster;
        this.displayText = displayText;
    }


    public void setType(@Nonnull String type) {
        if(type != null) {
            type = type.toUpperCase();
        }

        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RelationDefinition that = (RelationDefinition)o;
        return
            (type == null ? that.getType() == null : type.equals(that.getType()))
                &&
            (broadcaster == null ? that.getBroadcaster() == null : broadcaster.equals(that.getBroadcaster()));
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (broadcaster != null ? broadcaster.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("type", type)
            .append("broadcaster", broadcaster)
            .append("displayText", displayText)
            .toString();
    }

    @Override
    public RelationDefinitionIdentifier getId() {
        return new RelationDefinitionIdentifier(type, broadcaster);
    }
}
