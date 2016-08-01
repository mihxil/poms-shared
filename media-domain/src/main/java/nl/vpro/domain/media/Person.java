package nl.vpro.domain.media;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import nl.vpro.beeldengeluid.gtaa.GTAARecord;
import nl.vpro.domain.media.support.DomainObject;
import nl.vpro.validation.NoHtml;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personType",
    propOrder = {
        "givenName",
        "familyName"
    })
public class Person extends DomainObject {

    @NoHtml
    @XmlElement
    protected String givenName;

    @NoHtml
    @XmlElement
    protected String familyName;

    @Column(nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    protected RoleType role;

    @ManyToOne(targetEntity = MediaObject.class, fetch = FetchType.LAZY)
    @XmlTransient
    protected MediaObject mediaObject;

    @Embedded
    @XmlTransient
    protected GTAARecord gtaaRecord;

    public Person() {
    }

    public Person(String givenName, String familyName, RoleType role) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.role = role;
    }

    public Person(Long id, String givenName, String familyName, RoleType role) {
        this(givenName, familyName, role);
        this.id = id;
    }

    public Person(Person source) {
        this(source, source.mediaObject);
    }

    public Person(Person source, MediaObject parent) {
        this(source.getGivenName(), source.getFamilyName(), source.getRole());
        this.gtaaRecord = source.gtaaRecord;

        this.mediaObject = parent;
    }

    public static Person copy(Person source){
        return copy(source, source.mediaObject);
    }

    public static Person copy(Person source, MediaObject parent){
        if(source == null) {
            return null;
        }

        return new Person(source, parent);
    }

    /**
     * Gets the value of the givenName property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * Sets the value of the givenName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGivenName(String value) {
        this.givenName = value;
    }

    /**
     * Gets the value of the familyName property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Sets the value of the familyName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFamilyName(String value) {
        this.familyName = value;
    }

    /**
     * Sets both the given name and the family name by splitting the String.
     *
     * @param name
     */
    public void setName(String name) {
        String[] split = name.split("\\s+", 2);
        if(split.length == 1) {
            setGivenName("");
            setFamilyName(name);
        } else {
            setGivenName(split[0]);
            setFamilyName(split[1]);
        }
    }

    /**
     * Gets the value of the role property.
     *
     * @return possible object is
     *         {@link RoleType }
     */
    public RoleType getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     *
     * @param value allowed object is
     *              {@link RoleType }
     */
    public void setRole(RoleType value) {
        this.role = value;
    }

    public MediaObject getMediaObject() {
        return mediaObject;
    }

    public void setMediaObject(MediaObject mediaObject) {
        this.mediaObject = mediaObject;
    }

    public GTAARecord getGtaaRecord() {
        return gtaaRecord;
    }

    public void setGtaaRecord(GTAARecord gtaaRecord) {
        this.gtaaRecord = gtaaRecord;
    }

    @Override
    public boolean equals(Object o) {
        if(super.equals(o)) {
            return true;
        }
        if(!(o instanceof Person)) {
            return false;
        }

        Person person = (Person)o;

        if(familyName != null ? !familyName.equals(person.familyName) : person.familyName != null) {
            return false;
        }
        if(givenName != null ? !givenName.equals(person.givenName) : person.givenName != null) {
            return false;
        }
        if(role != person.role) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("givenName", givenName)
            .append("familyName", familyName)
            .append("role", role)
            .toString();
    }
}
