/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.extern.slf4j.Slf4j;
import nl.vpro.domain.Change;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.util.DateUtils;

import javax.xml.bind.annotation.*;
import java.time.Instant;

/**
 * @author Roelof Jan Koekoek
 */
@XmlType(name = "changeType")
@JsonPropertyOrder({
    "sequence",
    "publishDate",
    "revision",
    "id",
    "mid",
    "deleted",
    "mergedTo",
    "media",
})
@XmlAccessorType(XmlAccessType.NONE)
@Slf4j
@XmlRootElement(name = "change")
public class MediaChange extends Change<MediaObject> {

    @XmlAttribute
    private Long sequence;

    @XmlAttribute
    private Long revision;

    @XmlAttribute
    private String mergedTo;

    public MediaChange() {
    }

    @lombok.Builder
    private MediaChange(Instant publishDate, Long revision, String mid, MediaObject media, Boolean deleted, MediaSince since) {
        this(DateUtils.toLong(MediaSince.instant(publishDate, since)), revision, MediaSince.mid(mid, since), media, deleted);
        setPublishDate(MediaSince.instant(publishDate, since));
    }

    private MediaChange(Long sequence, Long revision, String mid, MediaObject media, Boolean deleted) {
        super(mid, media, deleted);
        this.sequence = sequence;
        this.revision = revision;
    }

    public static MediaChange update(long sequence, Long revision, MediaObject media) {
        MediaChange change = new MediaChange(sequence, revision, media.getMid(), media, false);
        change.setPublishDate(media.getLastPublishedInstant());
        return change;
    }

    public static MediaChange delete(long sequence, Long revision, String mid) {
        return new MediaChange(sequence, revision, mid, null, true);
    }

    public static MediaChange delete(long sequence, Long revision, MediaObject media) {
        MediaChange change = new MediaChange(sequence, revision, media.getMid(), media, true);
        change.setPublishDate(media.getLastPublishedInstant());
        return change;
    }

    public static MediaChange merged(long sequence, Long revision, MediaObject media, String mergedTo) {
        MediaChange change = new MediaChange(sequence, revision, media.getMid(), media, true);
        change.setPublishDate(media.getLastPublishedInstant());
        change.setMergedTo(mergedTo);
        return change;
    }

    public static MediaChange tail(long sequence) {
        return tail(null, sequence);
    }

    public static MediaChange tail(Instant publishDate) {
        return tail(publishDate, null);
    }


    public static MediaChange tail(Instant publishDate, Long sequence) {
        MediaChange tail = new MediaChange(publishDate, sequence, null, null, null, null);
        tail.setTail(true);
        return tail;
    }

    public static MediaChange of(MediaObject media) {
        return of(media, null);
    }

    public static MediaChange of(MediaObject media, Long revision) {

        MediaChange change;
        final Instant lastPublished = media.getLastPublishedInstant();
        if (media.getWorkflow() == null) {
            log.warn("Invalid workflow for {} : {}", media.getMid(), media.getWorkflow());
            return null;
        }
        switch (media.getWorkflow()) {
            case DELETED:
            case REVOKED:
                change = new MediaChange(lastPublished, revision, media.getMid(), media, true, null);
                break;

            case PUBLISHED:
                change = new MediaChange(lastPublished, revision, media.getMid(), media, false, null);
                break;

            case MERGED:
                change = new MediaChange(lastPublished, revision, media.getMid(), media, true, null);
                change.setMergedTo(media.getMergedToRef());
                break;

            default:
                log.warn("Invalid workflow for {} : {}", media.getMid(), media.getWorkflow());
                change = null;
                break;

        }
        return change;

    }


    @XmlElement(name = "media")
    public MediaObject getMedia() {
        return getObject();
    }
    public void setMedia(MediaObject media) {
        setObject(media);
    }

    @Deprecated
    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    @Deprecated
    public Long getRevision() {
        return revision;
    }

    public void setRevision(Long revision) {
        this.revision = revision;
    }


    @XmlAttribute
    public String getMid() {
        return getId();
    }

    public void setMid(String mid) {
        if (getId() == null) {
            setId(mid);
        }
    }
    public String getMergedTo() {
        return mergedTo;
    }

    public void setMergedTo(String mergedTo) {
        this.mergedTo = mergedTo;
    }


    @Override
    public String toString() {
        return super.toString() + ":" + revision + (mergedTo != null  ? (":merged to " + mergedTo) : "");
    }



}
