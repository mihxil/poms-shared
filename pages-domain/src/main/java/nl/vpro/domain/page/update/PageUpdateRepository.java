/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import java.time.Instant;
import java.util.List;

import nl.vpro.domain.Changes;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public interface PageUpdateRepository {

    List<String> namesForSectionPath(String path, String portalId);

    PageUpdate loadByUrl(String url);

    PageUpdate loadByCrid(String crid);

    void save(PageUpdate oldPage, PageUpdate update);

    boolean delete(String pageRef);

    boolean deleteAll(String url, int max);

    Changes<PageUpdate> getChanges(Instant since);

    class ToManyUpdatesException extends RuntimeException {

        public ToManyUpdatesException(String message) {
            super(message);
        }
    }

}
