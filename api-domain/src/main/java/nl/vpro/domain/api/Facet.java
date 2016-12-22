/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public interface Facet<T extends AbstractSearch> {

    T getFilter();

    void setFilter(T search);
}
