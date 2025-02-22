/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.page.Crid;
import nl.vpro.domain.page.PageType;
import nl.vpro.domain.page.util.Urls;
import nl.vpro.domain.page.validation.ValidBroadcaster;
import nl.vpro.domain.page.validation.ValidGenre;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.jackson2.Views;
import nl.vpro.validation.NoHtml;
import nl.vpro.validation.NoHtmlList;
import nl.vpro.validation.URI;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Roelof Jan Koekoek
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageUpdateType")
@XmlRootElement(name = "page")
public class PageUpdate implements Serializable {


    public static PageUpdateBuilder builder() {
        return PageUpdateBuilder.page(null, null);
    }

    public static PageUpdateBuilder builder(PageType pt, String url) {
        return PageUpdateBuilder.page(pt, url);
    }

    @NotNull
    @XmlAttribute(required = true)
    @Getter
    @Setter
    protected PageType type;

    @NotNull
    @URI
    @XmlAttribute(required = true)
    @Getter
    protected String url;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @Getter
    @Setter
    protected Instant publishStart;

    @XmlElement(name = "crid")
    @JsonProperty("crids")
    @Valid
    @Setter
    protected List<Crid> crids;

    @XmlElement(name = "alternativeUrl")
    @JsonProperty("alternativeUrls")
    @Valid
    @Setter
    protected List<String> alternativeUrls;

    @NotNull
    @Size(min = 1)
    @XmlElement(name = "broadcaster", required = true)
    @JsonProperty("broadcasters")
    @ValidBroadcaster
    @Setter
    protected List<String> broadcasters;

    @Valid
    @Getter
    @Setter
    protected PortalUpdate portal;

    @NotNull
    @Size(min = 1)
    @XmlElement(required = true, nillable = false)
    @NoHtml
    @Getter
    @Setter
    protected String title;

    @NoHtml
    @Getter
    @Setter
    protected String subtitle;

    @XmlElement(name = "keyword")
    @JsonProperty("keywords")
    @NoHtmlList
    @Setter
    protected List<String> keywords;

    @NoHtml
    @Getter
    @Setter
    protected String summary;

    @XmlElementWrapper(name = "paragraphs")
    @XmlElement(name = "paragraph")
    @JsonProperty("paragraphs")
    @Valid
    @Setter
    protected List<ParagraphUpdate> paragraphs;

    @XmlElement(name = "tag")
    @JsonProperty("tags")
    //@Pattern("(?i)[a-z]")
    @NoHtmlList
    @Setter
    protected List<String> tags;


    @XmlElement(name = "genre")
    @JsonProperty("genres")
    @ValidGenre
    @Setter
    protected List<String> genres;

    @XmlElement(name = "link")
    @JsonProperty("links")
    @Valid
    @Setter
    private List<LinkUpdate> links;

    @XmlElementWrapper(name = "embeds")
    @XmlElement(name = "embed")
    @JsonProperty("embeds")
    @Valid
    @Setter
    private List<EmbedUpdate> embeds;

    @XmlElement(name = "statRef")
    @JsonProperty("statRefs")
    @NoHtmlList
    @Getter
    @Setter
    private List<String> statRefs;

    @XmlElement(name = "image")
    @JsonProperty("images")
    @Valid
    @Setter
    protected List<ImageUpdate> images;


    @XmlElement(name = "relation")
    @JsonProperty("relations")
    @Valid
    @Setter
    protected List<RelationUpdate> relations;

    @XmlTransient
    private String rev;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @Getter
    @Setter
    private Instant lastPublished;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @Getter
    @Setter
    private Instant creationDate;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @Getter
    @Setter
    private Instant lastModified;

    @XmlTransient
    @Getter
    @Setter
    private boolean deleted = false;

    public PageUpdate() {
    }

    public PageUpdate(PageType type, String url) {
        this.type = type;
        setUrl(url);
    }


    public void setUrl(String url) {
        this.url = url == null ? null : Urls.normalize(url);
    }

    public List<Crid> getCrids() {
        if (crids == null) {
            crids = new ArrayList<>();
        }
        return crids;
    }

    public List<String> getAlternativeUrls() {
        if (alternativeUrls == null) {
            alternativeUrls = new ArrayList<>();
        }
        return alternativeUrls;
    }

    public List<String> getBroadcasters() {
        if (broadcasters == null) {
            broadcasters = new ArrayList<>();
        }
        return broadcasters;
    }



    public List<String> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<>();
        }
        return keywords;
    }



    public List<ParagraphUpdate> getParagraphs() {
        if (paragraphs == null) {
            paragraphs = new ArrayList<>();
        }
        return paragraphs;
    }



    public List<String> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }


    public List<LinkUpdate> getLinks() {
        if (links == null) {
            links = new ArrayList<>();
        }
        return links;
    }

    public List<EmbedUpdate> getEmbeds() {
        if (embeds == null) {
            embeds = new ArrayList<>();
        }
        return embeds;
    }


    public void embed(EmbedUpdate embedUpdate) {
        getEmbeds().add(embedUpdate);
    }

    public List<ImageUpdate> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }

        return images;
    }


    public List<RelationUpdate> getRelations() {
        if (relations == null) {
            relations = new ArrayList<>();
        }
        return relations;
    }


    @JsonProperty("_rev")
    @JsonView(Views.Publisher.class)
    public String getRevision() {
        return rev;
    }

    public void setRevision(String rev) {
        this.rev = rev;
    }

    public List<String> getGenres() {
        if (genres == null) {
            genres = new ArrayList<>();
        }

        return genres;
    }


    @Override
    public String toString() {
        return url + (crids != null ? (" " + crids) : "") + " " + getTitle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PageUpdate that = (PageUpdate) o;

        if (type != that.type) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (publishStart != null ? !publishStart.equals(that.publishStart) : that.publishStart != null) return false;
        if (crids != null ? !crids.equals(that.crids) : that.crids != null) return false;
        if (alternativeUrls != null ? !alternativeUrls.equals(that.alternativeUrls) : that.alternativeUrls != null)
            return false;
        if (broadcasters != null ? !broadcasters.equals(that.broadcasters) : that.broadcasters != null) return false;
        if (portal != null ? !portal.equals(that.portal) : that.portal != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (subtitle != null ? !subtitle.equals(that.subtitle) : that.subtitle != null) return false;
        if (keywords != null ? !keywords.equals(that.keywords) : that.keywords != null) return false;
        if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
        if (paragraphs != null ? !paragraphs.equals(that.paragraphs) : that.paragraphs != null) return false;
        if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;
        if (genres != null ? !genres.equals(that.genres) : that.genres != null) return false;
        if (links != null ? !links.equals(that.links) : that.links != null) return false;
        if (embeds != null ? !embeds.equals(that.embeds) : that.embeds != null) return false;
        if (statRefs != null ? !statRefs.equals(that.statRefs) : that.statRefs != null) return false;
        if (images != null ? !images.equals(that.images) : that.images != null) return false;
        if (relations != null ? !relations.equals(that.relations) : that.relations != null) return false;
        if (lastPublished != null ? !lastPublished.equals(that.lastPublished) : that.lastPublished != null)
            return false;
        if (creationDate != null ? !creationDate.equals(that.creationDate) : that.creationDate != null) return false;
        return lastModified != null ? lastModified.equals(that.lastModified) : that.lastModified == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (publishStart != null ? publishStart.hashCode() : 0);
        result = 31 * result + (crids != null ? crids.hashCode() : 0);
        result = 31 * result + (alternativeUrls != null ? alternativeUrls.hashCode() : 0);
        result = 31 * result + (broadcasters != null ? broadcasters.hashCode() : 0);
        result = 31 * result + (portal != null ? portal.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (subtitle != null ? subtitle.hashCode() : 0);
        result = 31 * result + (keywords != null ? keywords.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (paragraphs != null ? paragraphs.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (genres != null ? genres.hashCode() : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        result = 31 * result + (embeds != null ? embeds.hashCode() : 0);
        result = 31 * result + (statRefs != null ? statRefs.hashCode() : 0);
        result = 31 * result + (images != null ? images.hashCode() : 0);
        result = 31 * result + (relations != null ? relations.hashCode() : 0);
        result = 31 * result + (lastPublished != null ? lastPublished.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        return result;
    }
}
