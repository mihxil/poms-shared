package nl.vpro.domain.media.support;

import lombok.ToString;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import org.hibernate.annotations.Type;

import nl.vpro.domain.AbstractOwnedText;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.validation.NoHtml;

/**
 * A {@link MediaObject} can have more than one description which should differ in type and
 * owner. See {@link Title} for further explanation of this class behaviour.
 *
 * @author Roelof Jan Koekoek
 * @see Title
 * @since 0.4
 */
@Entity
@nl.vpro.validation.Description
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "descriptionType",
    namespace = Xmlns.MEDIA_NAMESPACE,
    propOrder = {"description"}
    )
@ToString(exclude = "parent")
public class Description extends AbstractOwnedText<Description> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;


    @ManyToOne(targetEntity = MediaObject.class)
    @XmlTransient
    protected MediaObject parent;

    public Description() {
    }

    public Description(String description, OwnerType owner, TextualType type) {
        super(description, owner, type);
    }

    public Description(Description source) {
        this(source, source.parent);
    }


    public <S extends MediaObject> Description(Description source, MediaObject parent) {
        this(source.get(), source.getOwner(), source.getType());
        this.parent = parent;
    }

    public static Description copy(Description source){
        return copy(source, source.parent);
    }

    public static Description copy(Description source, MediaObject parent){
        if(source == null) {
            return null;
        }

        return new Description(source, parent);
    }

    @XmlValue
    @Column(name = "description", nullable = false)
    @Lob
    @Type(type = "org.hibernate.type.StringType")
    @NoHtml
    public String getDescription() {
        return get();
    }

    public void setDescription(String description) {
        set(description);
    }

    protected static String strip(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("[\f\\u0085\\u2028\\u2029  ]", "\n");
    }

    @XmlTransient
    public MediaObject getParent() {
        return parent;
    }

    public void setParent(MediaObject parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Description desc = (Description)o;

        if (parent == null && desc.getParent() == null) {
            return owner == desc.getOwner() && type == desc.getType();
        }

        return owner == desc.getOwner() && type == desc.getType() && parent.equals(desc.getParent());

    }


    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        this.parent = (MediaObject) parent;
    }
}
