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
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
public interface SearchableFacet<T extends AbstractSearch> { //},  F extends AbstractSearch>  extends Facet<F> {
    boolean hasSubSearch();

    T getSubSearch();

    void setSubSearch(T filter);
}
