/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.api.rs.v3.page;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.api.Constants;
import nl.vpro.domain.api.IdList;
import nl.vpro.domain.api.MultiplePageResult;
import nl.vpro.domain.api.SuggestResult;
import nl.vpro.domain.api.page.PageForm;
import nl.vpro.domain.api.page.PageResult;
import nl.vpro.domain.api.page.PageSearchResult;

import static nl.vpro.domain.api.Constants.*;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@Path(PageRestService.PATH)
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public interface PageRestService {
    String TAG = "pages";
    String PATH = "/" + TAG;

    @GET
    @Path("/suggest")
    SuggestResult suggest(
        @QueryParam("input") @Size(min = 1) String input,
        @QueryParam(PROFILE) String profile,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max
    );

    @GET
    PageResult list(
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max
    );

    @POST
    PageSearchResult find(
        @Valid PageForm form,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max);

    @GET
    @Path("/multiple")
    MultiplePageResult loadMultiple(
        @QueryParam("ids") String ids,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(PROFILE) String profileName
    );

    @POST
    @Path("/multiple")
    MultiplePageResult loadMultiple(
        IdList ids,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(PROFILE) String profileName
    );

    @GET
    @Path("/related")
    PageResult listRelated(
        @QueryParam("id") String id,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max
    );

    @POST
    @Path("/related")
    PageSearchResult findRelated(
        @Valid PageForm form,
        @QueryParam("id") String id,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max
    );


    @POST
    @Path("/iterate/")
    @Deprecated
        //"This targets sitemaps, we'll make a sitemap feature on the page rest service"
    InputStream iterate(
        @Valid PageForm form,
        @QueryParam(PROFILE) String profile,
        @QueryParam(PROPERTIES) String properties,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response
    );
}
