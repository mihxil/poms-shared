package nl.vpro.domain.api.media;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.api.ApiScheduleEvent;
import nl.vpro.domain.api.GenericScheduleSearchResult;
import nl.vpro.domain.api.SearchResult;
import nl.vpro.domain.api.SearchResultItem;


/**
 * Exists only because of https://jira.vpro.nl/browse/API-118
 *
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "scheduleSearchResultType", propOrder = {})
@XmlRootElement(name = "scheduleSearchResult")
public class ScheduleSearchResult extends GenericScheduleSearchResult<ApiScheduleEvent> {

    public ScheduleSearchResult() {
    }

    public static ScheduleSearchResult emptyResult(Long offset, Integer max) {
        return new ScheduleSearchResult(Collections.emptyList(), offset, max, 0L);
    }

    public ScheduleSearchResult(List<SearchResultItem<? extends ApiScheduleEvent>> list, Long offset, Integer max, long total) {
        super(list, offset, max, total);
    }

    public ScheduleSearchResult(SearchResult<? extends ApiScheduleEvent> sr) {
        super(sr);
    }

    @Override
    public ScheduleResult asResult() {
        return new ScheduleResult(super.asResult());
    }


}
