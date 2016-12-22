/**
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import nl.vpro.domain.api.*;
import nl.vpro.domain.media.AVType;
import nl.vpro.domain.media.AgeRating;
import nl.vpro.domain.media.ContentRating;
import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.RelationDefinition;
import nl.vpro.domain.media.support.Tag;
import nl.vpro.util.DateUtils;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class MediaFormBuilder extends AbstractFormBuilder {

    private final MediaForm form = new MediaForm();

    private MediaFormBuilder() {
    }

    public static MediaFormBuilder form() {
        return new MediaFormBuilder();
    }

    public static MediaForm emptyForm() {
        return form().build();
    }

    public MediaForm build() {
        return form;
    }

    // if proprerty can have more than one value, the default Match value is MUST, otherwise it is SHOULD

    public MediaFormBuilder text(String text) {
        search().setText(simpleTextMatcher(text, Match.SHOULD));
        return this;
    }

    public MediaFormBuilder mediaIds(String... mediaIds) {
        search().setMediaIds(textMatchers(Match.SHOULD, mediaIds));
        return this;
    }

    public MediaFormBuilder mediaIds(Match match, String... mediaIds) {
        search().setMediaIds(textMatchers(match, mediaIds));
        return this;
    }

    public MediaFormBuilder sortDate(Date begin, Date end) {
        return sortDate(begin, end, false);
    }

    public MediaFormBuilder sortDate(Instant begin, Instant end) {
        return sortDate(begin, end, false);
    }



    public MediaFormBuilder sortDate(Date begin, Date end, boolean inclusiveEnd) {
        return sortDate(DateUtils.toInstant(begin), DateUtils.toInstant(end), inclusiveEnd);
    }


    public MediaFormBuilder sortDate(Instant begin, Instant end, boolean inclusiveEnd) {
        DateRangeMatcherList list = search().getSortDates();
        if (list == null) {
            list = new DateRangeMatcherList();
            search().setSortDates(list);
        }
        list.asList().add(new DateRangeMatcher(begin, end, inclusiveEnd));
        return this;
    }


    public MediaFormBuilder publishDate(Instant begin, Instant end) {
        return publishDate(begin, end, false);
    }


    public MediaFormBuilder publishDate(Instant begin, Instant end, boolean inclusiveEnd) {
        DateRangeMatcherList list = search().getPublishDates();
        if (list == null) {
            list = new DateRangeMatcherList();
            search().setSortDates(list);
        }
        list.asList().add(new DateRangeMatcher(begin, end, inclusiveEnd));
        return this;
    }

    public MediaFormBuilder broadcasters(String... broadcasters) {
        broadcasters(Match.SHOULD, broadcasters);
        return this;
    }

    public MediaFormBuilder broadcasters(Match match, String... broadcasters) {
        search().setBroadcasters(textMatchers(match, broadcasters));
        return this;
    }

    public MediaFormBuilder broadcasters(Match match, TextMatcher... matchers) {
        search().setBroadcasters(textMatchers(match, matchers));
        return this;
    }

    public MediaFormBuilder locations(String... locations) {
        search().setLocations(textMatchers(Match.SHOULD, locations));
        return this;
    }

    public MediaFormBuilder tags(String... tags) {
        search().setTags(extendedTextMatchers(Match.SHOULD, tags));
        return this;
    }

    public MediaFormBuilder tags(boolean caseSensitive, String... tags) {
        search().setTags(extendedTextMatchers(Match.SHOULD, tags));
        return this;
    }


    public MediaFormBuilder tags(Tag... tags) {
        search().setTags(extendedTextMatchers(Match.SHOULD, tags));
        return this;
    }

    public MediaFormBuilder tags(Match match, Tag... tags) {
        search().setTags(extendedTextMatchers(match, tags));
        return this;
    }

    public MediaFormBuilder tags(Match match, ExtendedTextMatcher... matchers) {
        search().setTags(extendedTextMatchers(match, matchers));
        return this;
    }


    public MediaFormBuilder tags(ExtendedTextMatcher... matchers) {
        return tags(Match.SHOULD, matchers);
    }


    public MediaFormBuilder genres(String... terms) {
        search().setGenres(textMatchers(Match.SHOULD, terms));
        return this;
    }

    public MediaFormBuilder genres(Match match, String... terms) {
        search().setGenres(textMatchers(match, terms));
        return this;
    }

    public MediaFormBuilder genres(Match match, TextMatcher... matchers) {
        search().setGenres(textMatchers(match, matchers));
        return this;
    }

    public MediaFormBuilder types(MediaType... types) {
        search().setTypes(textMatchers(Match.SHOULD, types));
        return this;
    }

    public MediaFormBuilder types(Match match, MediaType... types) {
        search().setTypes(textMatchers(match, types));
        return this;
    }

    public MediaFormBuilder types(Match match, TextMatcher... matchers) {
        search().setTypes(textMatchers(match, matchers));
        return this;
    }

    public MediaFormBuilder avTypes(AVType... avTypes) {
        search().setAvTypes(textMatchers(Match.SHOULD, avTypes));
        return this;
    }


    public MediaFormBuilder avTypes(Match match, AVType... avTypes) {
        search().setAvTypes(textMatchers(match, avTypes));
        return this;
    }

    public MediaFormBuilder avTypes(Match match, TextMatcher... matchers) {
        search().setAvTypes(textMatchers(match, matchers));
        return this;
    }

    public MediaFormBuilder duration(Date begin, Date end) {
        return duration(begin, end, false);
    }

    public MediaFormBuilder duration(Date begin, Date end, boolean inclusiveEnd) {
        DateRangeMatcherList list  = search().getDurations();
        if (list == null) {
            list = new DateRangeMatcherList();
            search().setDurations(list);
        }
        list.asList().add(new DateRangeMatcher(begin, end, inclusiveEnd));
        return this;
    }


    public MediaFormBuilder duration(Duration begin, Duration end) {
        return duration(begin, end, false);
    }

    public MediaFormBuilder duration(Duration begin, Duration end, boolean inclusiveEnd) {
        DateRangeMatcherList list = search().getDurations();
        if (list == null) {
            list = new DateRangeMatcherList();
            search().setDurations(list);
        }
        list.asList().add(new DateRangeMatcher(begin, end, inclusiveEnd));
        return this;
    }


    public MediaFormBuilder episodeOfs(String... mids) {
        search().setEpisodeOf(textMatchers(Match.SHOULD, mids));
        return this;
    }

    public MediaFormBuilder episodeOfs(Match match, String... mids) {
        search().setEpisodeOf(textMatchers(match, mids));
        return this;
    }

    public MediaFormBuilder episodeOfs(Match match, TextMatcher... matchers) {
        search().setEpisodeOf(textMatchers(match, matchers));
        return this;
    }

    public MediaFormBuilder descendantOfs(String... mids) {
        search().setDescendantOf(textMatchers(Match.SHOULD, mids));
        return this;
    }

    public MediaFormBuilder descendantOfs(Match match, String... mids) {
        search().setDescendantOf(textMatchers(match, mids));
        return this;
    }

    public MediaFormBuilder descendantOfs(Match match, TextMatcher... matchers) {
        search().setDescendantOf(textMatchers(match, matchers));
        return this;
    }

    public MediaFormBuilder memberOfs(String... mids) {
        search().setMemberOf(textMatchers(Match.SHOULD, mids));
        return this;
    }

    public MediaFormBuilder memberOfs(Match match, String... mids) {
        search().setMemberOf(textMatchers(match, mids));
        return this;
    }

    public MediaFormBuilder ageRating(AgeRating... ageRatings) {
        search().setAgeRatings(textMatchers(Match.SHOULD, ageRatings));
        return this;
    }

    public MediaFormBuilder contentRatings(ContentRating... contentRatings) {
        search().setContentRatings(textMatchers(Match.SHOULD, contentRatings));
        return this;
    }

    public MediaFormBuilder memberOfs(Match match, TextMatcher... matchers) {
        search().setMemberOf(textMatchers(match, matchers));
        return this;
    }

    public MediaFormBuilder scheduleEvents(ScheduleEventSearch scheduleEventSearch) {
        search().setScheduleEvents(scheduleEventSearch);
        return this;
    }

    public MediaFormBuilder highlight(boolean b) {
        form.setHighlight(b);
        return this;
    }

    public MediaFormBuilder broadcasterFacet() {
        facets().setBroadcasters(new MediaFacet());
        return this;
    }

    public MediaFormBuilder broadcasterFacet(MediaFacet facet) {
        facets().setBroadcasters(facet);
        return this;
    }

    public MediaFormBuilder genreFacet() {
        facets().setGenres(new MediaSearchableTermFacet());
        return this;
    }

    public MediaFormBuilder tagFacet() {
        facets().setTags(new ExtendedMediaFacet());
        return this;
    }

    public MediaFormBuilder tagFacet(boolean caseSensitive) {
        ExtendedMediaFacet emf = new ExtendedMediaFacet();
        emf.setCaseSensitive(caseSensitive);
        facets().setTags(emf);
        return this;
    }


    public MediaFormBuilder typeFacet() {
        facets().setTypes(new MediaFacet());
        return this;
    }

    public MediaFormBuilder avTypeFacet() {
        facets().setAvTypes(new MediaFacet());
        return this;
    }

    public MediaFormBuilder sortDateFacet(RangeFacet<Date>... ranges) {
        DateRangeFacets dateRangeFacets = new DateRangeFacets();
        dateRangeFacets.setRanges(Arrays.asList(ranges));
        facets().setSortDates(dateRangeFacets);
        return this;
    }

    public MediaFormBuilder durationFacet(RangeFacet<Date>... ranges) {
        DateRangeFacets dateRangeFacets = new DateRangeFacets();
        dateRangeFacets.setRanges(Arrays.asList(ranges));
        facets().setDurations(dateRangeFacets);
        return this;
    }

    public MediaFormBuilder episodeOfFacet() {
        facets().setEpisodeOf(new MemberRefFacet());
        return this;
    }

    public MediaFormBuilder episodeOfFacet(MemberRefFacet facet) {
        facets().setEpisodeOf(facet);
        return this;
    }

    public MediaFormBuilder memberOfFacet() {
        facets().setMemberOf(new MemberRefFacet());
        return this;
    }

    public MediaFormBuilder memberOfFacet(MemberRefFacet facet) {
        facets().setMemberOf(facet);
        return this;
    }

    public MediaFormBuilder descendantOfFacet() {
        facets().setDescendantOf(new MemberRefFacet());
        return this;
    }

    public MediaFormBuilder descendantOfFacet(MemberRefFacet facet) {
        facets().setDescendantOf(facet);
        return this;
    }

    public MediaFormBuilder relationText(RelationDefinition definition, ExtendedTextMatcher text) {
        return relation(definition, text, null);
    }

    public MediaFormBuilder relationUri(RelationDefinition definition, String uri) {
        return relation(definition, null, uri);
    }

    public MediaFormBuilder relationText(RelationDefinition definition, String text) {
        return relation(definition, text, null);
    }


    public MediaFormBuilder relation(RelationDefinition definition, String text, String uri) {
        return relation(definition, ExtendedTextMatcher.must(text), TextMatcher.must(uri));
    }
    public MediaFormBuilder relation(RelationDefinition definition, ExtendedTextMatcher text, TextMatcher uri) {
        RelationSearch relationSearch = new RelationSearch();
        RelationSearchList search = search().getRelations();
        if (text != null) {
            relationSearch.setValues(ExtendedTextMatcherList.must(text));
        }
        if (uri != null) {
            relationSearch.setUriRefs(TextMatcherList.must(uri));
        }
        relationSearch.setBroadcasters(TextMatcherList.must(TextMatcher.must(definition.getBroadcaster())));
        relationSearch.setTypes(TextMatcherList.must(TextMatcher.must(definition.getType())));
        if (search == null) {
            search = new RelationSearchList();
            search().setRelations(search);
        }
        search.asList().add(relationSearch);

        return this;
    }

    public MediaFormBuilder relationsFacet() {
        return relationsFacet(new RelationFacet());
    }

    public MediaFormBuilder relationsFacet(RelationFacet relationFacet) {
        facets().setRelations(new RelationFacetList(Collections.singletonList(relationFacet)));
        return this;
    }

    public MediaFormBuilder relationsFacet(String relationFacet) {
        RelationFacet rf = new RelationFacet();
        rf.setName(relationFacet);
        return relationsFacet(rf);
    }

    public MediaFormBuilder relationsFacet(RelationFacetList facets) {
        facets().setRelations(facets);
        return this;
    }

    public MediaFormBuilder sortOrder(MediaSortOrder... orders) {
        for (MediaSortOrder order : orders) {
            form.addSortField(order);
        }
        return this;
    }

    public MediaFormBuilder asc(MediaSortField... orders) {
        for (MediaSortField order : orders) {
            form.addSortField(MediaSortOrder.asc(order));
        }
        return this;
    }

    public MediaFormBuilder ageRatingFacet() {
        return ageRatingFacet(null);
    }


    public MediaFormBuilder ageRatingFacet(Integer threshold) {
        MediaFacet facet = new MediaFacet();
        facet.setThreshold(threshold);
        facets().setAgeRatings(facet);
        return this;
    }

    public MediaFormBuilder contentRatingsFacet() {
        facets().setContentRatings(new MediaFacet());
        return this;
    }

    private MediaSearch search() {
        if(form.getSearches() == null) {
            form.setSearches(new MediaSearch());
        }
        return form.getSearches();
    }

    private MediaFacets facets() {
        if(form.getFacets() == null) {
            form.setFacets(new MediaFacets());
        }
        return form.getFacets();
    }

}
