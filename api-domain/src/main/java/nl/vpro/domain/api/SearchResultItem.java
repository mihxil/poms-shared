/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.*;

import nl.vpro.domain.media.Group;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.Segment;
import nl.vpro.domain.page.Page;

/**
 * @author rico
 * @author Michiel Meeuwissen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"score", "highlights", "result"})
public class SearchResultItem<T> {

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "objectType")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Program.class, name = "program"),
        @JsonSubTypes.Type(value = Group.class, name = "group"),
        @JsonSubTypes.Type(value = Segment.class, name = "segment"),
        @JsonSubTypes.Type(value = Page.class, name = "page")
    })
    private T result;

    @XmlAttribute
    private Float score;

    @XmlElement(name = "highlight")
    @JsonProperty(value = "highlights")
    private List<HighLight> highlights;

    public SearchResultItem() {
    }

    public SearchResultItem(T item) {
        this.result = item;
    }

    public SearchResultItem(T result, Float score, List<HighLight> highlights) {
        this.result = result;
        this.score = score;
        this.highlights = highlights;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public List<HighLight> getHighlights() {
        if(highlights == null) {
            highlights = new ArrayList<>();
        }
        return highlights;
    }

    public void setHighlights(List<HighLight> highlights) {
        this.highlights = highlights;
    }

    @Override
    public String toString() {
        return getResult() + ":" + getScore();
    }

}
