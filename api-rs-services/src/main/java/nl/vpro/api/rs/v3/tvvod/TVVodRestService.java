/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.api.rs.v3.tvvod;

import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.media.MediaTable;

/**
 * @author Michiel Meeuwissen
 * @since 3.5
 */
@Path(TVVodRestService.PATH)
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface TVVodRestService {
    String PATH = "/tvvod";

    @GET
    @Path("/{mid}")
    MediaTable get(
        @PathParam("mid") @Size(min = 1) String mid
    );

}
