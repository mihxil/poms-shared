/*
 * Copyright (C) 2007/2008 All rights reserved
 * VPRO, The Netherlands
 */

package nl.vpro.domain.media.support;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.Xmlns;

/**
 * @author roekoe
 * @version $Id$
 */
@SuppressWarnings("serial")
@MappedSuperclass
@XmlType(name = "domainObjectType", namespace = Xmlns.SHARED_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class DomainObject implements Identifiable<Long>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient // Don't remove!
    protected Long id;

    protected DomainObject() {
    }

    protected DomainObject(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Under normal operation this should not be used!
     * <p/>
     * While testing it sometimes comes in handy to be able to set an Id to simulate
     * a persisted object.
     *
     * @param id
     */
    public DomainObject setId(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Checks for database identity or object identity if one side of the comparison can
     * not supply a database identity. It is advised to override this method with a more
     * accurate test which should not rely on database identity. You can rely on this
     * criterion when equality can not be deducted programmatic and a real and final
     * check is in need of human interaction. In essence this check then states that two
     * objects are supposed to be different if they can't supply the same database Id.
     *
     * @param object the object to compare with
     * @return true if both objects are equal
     */
    @Override
    public boolean equals(Object object) {
        if(object == null) {
            return false;
        }

        if(this.getClass() != object.getClass()) {
            return false;
        }

        DomainObject that = (DomainObject)object;

        if(this.getId() != null && that.getId() != null) {
            return this.getId().equals(that.getId());
        }

        return this == that;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .toString();
    }
}
