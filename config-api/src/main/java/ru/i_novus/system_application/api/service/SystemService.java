package ru.i_novus.system_application.api.service;

import org.springframework.data.domain.Page;
import ru.i_novus.system_application.api.model.SystemResponse;
import ru.i_novus.system_application.api.criteria.SystemCriteria;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


/**
 * Интерфейс API для получения прикладных систем
 */
@Valid
@Path("/auth/systems/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SystemService {

    @GET
    @Path("/")
    public Page<SystemResponse> getAllSystem(@BeanParam SystemCriteria criteria);

    @GET
    @Path("/{code}")
    public SystemResponse getSystem(@PathParam("code") String code);
}
