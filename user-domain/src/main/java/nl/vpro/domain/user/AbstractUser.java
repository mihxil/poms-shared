/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import nl.vpro.domain.Identifiable;

@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractUser implements Serializable, Identifiable<String>, User  {
    private static final long serialVersionUID = 1L;

    @Transient
    private boolean persisted = false;

    @Id
    @Column(name = "principalid")
    @Getter
    protected String principalId;

    @Getter
    @Setter
    protected String givenName;

    @Getter
    @Setter
    protected String familyName;

    @Column(nullable = false)
    @Getter
    @Setter
    protected String displayName;

    @Column(nullable = false)
    @Pattern(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
        flags = {Pattern.Flag.CASE_INSENSITIVE}
    )
    @Getter
    @Setter
    protected String email;

    @Column
    @Getter
    @Setter
    protected Instant lastLogin;

    @Column(name = "creationDate")
    @Getter
    @Setter
    protected Instant creationInstant;

    protected AbstractUser() {
    }


    protected AbstractUser(AbstractUser user) {
        this.principalId = user.principalId;
        this.givenName = user.givenName;
        this.familyName = user.familyName;
        this.displayName = user.displayName;
        this.email = user.email;
        this.lastLogin = user.lastLogin;
    }

    public AbstractUser(String principalId, String displayName, String email) {
        if (principalId == null) {
            principalId = email;
        }
        this.principalId = principalId == null  ? null : principalId.toLowerCase();
        if (displayName == null) {
            displayName = this.principalId;
        }
        this.displayName = displayName;
        this.email = email;
    }

    public AbstractUser(String principalId, String displayName, String email, String givenName, String familyName, Instant lastLogin) {
        this(principalId, displayName, email);
        this.givenName = givenName;
        this.familyName = familyName;
        this.lastLogin = lastLogin;
    }

    @Override
    @XmlAttribute
    public String getId() {
        return getPrincipalId();
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId.toLowerCase();
    }

    public boolean isNew() {
        return ! persisted;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("User");
        sb.append("{principalId='").append(principalId).append('\'');
        sb.append(", displayName='").append(displayName).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append('}');
        return sb.toString();
    }


}
