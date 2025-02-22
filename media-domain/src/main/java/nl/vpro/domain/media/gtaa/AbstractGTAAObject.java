/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.gtaa;

import lombok.Data;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import nl.vpro.openarchives.oai.Namespaces;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@XmlAccessorType(XmlAccessType.NONE)
@Data
public abstract class AbstractGTAAObject {

    private UUID uuid;

    @XmlAttribute(name = "about", namespace = Namespaces.RDF)
    private String about;

    protected AbstractGTAAObject() {
    }

    protected AbstractGTAAObject(UUID uuid, String about) {
        this.uuid = uuid;
        this.about = about;
    }
    protected static class AbstractBuilder<T extends AbstractBuilder<T>> {

        UUID uuid;
        String about;

        public T about(String a) {
            this.about = a;
            return (T) this;
        }

    }
}
