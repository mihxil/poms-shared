/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

/**
 * A searchable facet can have a 'sub search'. This is needed as the facet is not on precisely one field, but on some
 * selection of it.
 *
 * See e.g. http://wiki.publiekeomroep.nl/display/npoapi/Media-+and+Schedule-API#Media-andSchedule-API-relations
 *
 * Another example are genres, you may be interested in just the genres starting with 3.0.1 or so.
 *
 * In other words this has no influence on the number of documents the faceting is applied, this in contraction to filtering {@link Facet#getFilter()}
 *
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
public interface SearchableFacet<T extends AbstractSearch, S extends AbstractSearch> extends Facet<T> {


    default boolean hasSubSearch() {
        return getSubSearch() != null && getSubSearch().hasSearches();
    }

    S getSubSearch();

    void setSubSearch(S  filter);
}
