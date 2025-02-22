/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.Group;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.Segment;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlType(name = "embedType", propOrder = {"title", "description", "media"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Embed {

    @XmlElement
    private String title;

    @XmlElement
    private String description;

    @XmlElements({
        @XmlElement(name = "group",   type = Group.class,   namespace = Xmlns.MEDIA_NAMESPACE),
        @XmlElement(name = "program", type = Program.class, namespace = Xmlns.MEDIA_NAMESPACE),
        @XmlElement(name = "segment", type = Segment.class, namespace = Xmlns.MEDIA_NAMESPACE)
    })
    @JsonIgnore
    private MediaObject media;

    public Embed() {
    }

    public Embed(MediaObject media) {
        this.media = media;
    }

    public Embed(MediaObject media, String title, String description) {
        this.media = media;
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    public MediaObject getMedia() {
        return media;
    }

    public void setMedia(MediaObject media) {
        this.media = media;
    }
}
