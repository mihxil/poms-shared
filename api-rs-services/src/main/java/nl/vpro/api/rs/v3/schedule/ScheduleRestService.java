/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.api.rs.v3.schedule;

import java.time.Instant;
import java.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import nl.vpro.api.rs.v3.media.MediaRestService;
import nl.vpro.domain.api.ApiScheduleEvent;
import nl.vpro.domain.api.media.ScheduleForm;
import nl.vpro.domain.api.media.ScheduleResult;
import nl.vpro.domain.api.media.ScheduleSearchResult;

import static nl.vpro.domain.api.Constants.*;

/**
 * Endpoint which facilitates RPC like requests on scheduled content. This API intents to capture meaningful and frequent
 * queries on scheduled media used when building a site or apps containing POMS media. This not a real REST API. It has no update
 * statements and it is mainly document oriented. Most calls will return a full media document and there are no separate
 * calls for sub-resources.
 * <p/>
 * The API returns three media instance pageTypes: Programs, Groups and Segments.
 * <p/>
 * Media id's must be mid's. Retrieval by crid is not implemented at this moment.
 *
 * @author Rico Jansen
 * @since 3.0
 */
@Path(ScheduleRestService.PATH)
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public interface ScheduleRestService {
    String TAG = MediaRestService.TAG; // documented with media, so also in that tag!
    String PATH = "/schedule";

    String CHANNEL = "channel";
    String NET = "net";
    String BROADCASTER = "broadcaster";
    String ANCESTOR = "ancestor";

    String GUIDE_DAY = "guideDay";
    String START = "start";
    String STOP = "stop";
    String SORT = "sort";


    @GET
    ScheduleResult list(
        @QueryParam(GUIDE_DAY) LocalDate  guideDay,
        @QueryParam(START) Instant start,
        @QueryParam(STOP) Instant stop,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SORT) @DefaultValue(ASC) String sort,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max
    );

    @GET
    @Path("/ancestor/{ancestor}")
    ScheduleResult listForAncestor(
        @Encoded @PathParam(ANCESTOR) String mediaId,
        @QueryParam(GUIDE_DAY) LocalDate guideDay,
        @QueryParam(START) Instant start,
        @QueryParam(STOP) Instant stop,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SORT) @DefaultValue(ASC) String sort,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max
    );


    @GET
    @Path("/ancestor/{ancestor}/now")
    ApiScheduleEvent nowForAncestor(
        @Encoded @PathParam(ANCESTOR) String mediaId,
        @QueryParam(PROPERTIES) String properties
    );

    @GET
    @Path("/ancestor/{ancestor}/next")
    ApiScheduleEvent nextForAncestor(
        @Encoded @PathParam(ANCESTOR) String mediaId,
        @QueryParam(PROPERTIES) String properties
    );

    @GET
    @Path("/broadcaster/{broadcaster}")
    ScheduleResult listBroadcaster(
        @PathParam(BROADCASTER) String broadcaster,
        @QueryParam(GUIDE_DAY) LocalDate guideDay,
        @QueryParam(START) Instant start,
        @QueryParam(STOP) Instant stop,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SORT) @DefaultValue(ASC) String sort,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max
    );


    @GET
    @Path("/broadcaster/{broadcaster}/now")
    ApiScheduleEvent nowForBroadcaster(
        @PathParam(BROADCASTER) String broadcaster,
        @QueryParam(PROPERTIES) String properties
    );

    @GET
    @Path("/broadcaster/{broadcaster}/next")
    ApiScheduleEvent nextForBroadcaster(
        @PathParam(BROADCASTER) String broadcaster,
        @QueryParam(PROPERTIES) String properties
    );

    @GET
    @Path("/channel/{channel}")
    ScheduleResult listChannel(
        @PathParam(CHANNEL) String channel,
        @QueryParam(GUIDE_DAY) LocalDate guideDay,
        @QueryParam(START) Instant start,
        @QueryParam(STOP) Instant stop,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SORT) @DefaultValue(ASC) String sort,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max
    );


    @GET
    @Path("/channel/{channel}/now")
    ApiScheduleEvent nowForChannel(
        @PathParam(CHANNEL) String channel,
        @QueryParam(PROPERTIES) String properties
    );

    @GET
    @Path("/channel/{channel}/next")
    ApiScheduleEvent nextForChannel(
        @PathParam(CHANNEL) String channel,
        @QueryParam(PROPERTIES) String properties
    );

    @GET
    @Path("/net/{net}")
    ScheduleResult listNet(
        @Encoded @PathParam(NET) String net,
        @QueryParam(GUIDE_DAY) LocalDate guideDay,
        @QueryParam(START) Instant start,
        @QueryParam(STOP) Instant stop,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(SORT) @DefaultValue(ASC) String sort,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max
    );


    @GET
    @Path("/net/{net}/now")
    ApiScheduleEvent nowForNet(
        @Encoded @PathParam(NET) String net,
        @QueryParam(PROPERTIES) String properties
    );

    @GET
    @Path("/net/{net}/next")
    ApiScheduleEvent nextForNet(
        @Encoded @PathParam(NET) String net,
        @QueryParam(PROPERTIES) String properties
    );

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    ScheduleSearchResult find(
        @Valid ScheduleForm form,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max
    );


}
