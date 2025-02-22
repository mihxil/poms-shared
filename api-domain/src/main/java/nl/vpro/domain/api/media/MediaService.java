/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import nl.vpro.domain.api.Deletes;
import nl.vpro.domain.api.MediaChange;
import nl.vpro.domain.api.Order;
import nl.vpro.domain.api.SuggestResult;
import nl.vpro.domain.api.profile.exception.ProfileNotFoundException;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaProvider;
import nl.vpro.domain.media.MediaType;
import nl.vpro.util.FilteringIterator;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public interface MediaService extends MediaProvider {

    SuggestResult suggest(String input, String profile, Integer max);

    Iterator<MediaChange> changes(String profile, boolean profileCheck, Instant since, String mid, Order order, Integer max, Long keepAlive, boolean withSequences, Deletes deletes) throws ProfileNotFoundException;

    @Override
    <T extends MediaObject> T findByMid(String mid);

    List<MediaObject> loadAll(List<String> ids);

    RedirectList redirects();

    MediaResult list(Order order, Long offset, Integer max);

    Iterator<MediaObject> iterate(String profile, MediaForm form, Long offset, Integer max, FilteringIterator.KeepAlive keepAlive) throws ProfileNotFoundException;

    MediaSearchResult find(String profile, MediaForm form, Long offset, Integer max) throws ProfileNotFoundException;

    MediaResult listMembers(MediaObject media, String profile, Order order, Long offset, Integer max);

    MediaSearchResult findMembers(MediaObject media, String profile, MediaForm form, Long offset, Integer max) throws ProfileNotFoundException;

    ProgramResult listEpisodes(MediaObject media, String profile, Order order, Long offset, Integer max);

    ProgramSearchResult findEpisodes(MediaObject media, String profile, MediaForm form, Long offset, Integer max) throws ProfileNotFoundException;

    MediaResult listDescendants(MediaObject media, String profile, Order order, Long offset, Integer max);

    MediaSearchResult findDescendants(MediaObject media, String profile, MediaForm form, Long offset, Integer max) throws ProfileNotFoundException;

    MediaSearchResult findRelated(MediaObject media, String profile, MediaForm form, Integer max) throws ProfileNotFoundException;

    MediaSearchResult findRelatedInTopspin(MediaObject media, String profile, MediaForm form, Integer max, String partyId, String clazz) throws ProfileNotFoundException;

    MediaType getType(String id);

    Optional<String> redirect(String mid);


}
