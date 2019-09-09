package ru.i_novus.system_application.api.service;

import org.springframework.data.domain.Page;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.criteria.ApplicationCriteria;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Интерфейс API для получения приложений
 */
@Valid
@Path("/auth/applications/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ApplicationService {

    @GET
    @Path("/")
    public Page<ApplicationResponse> getAllApplication(@BeanParam ApplicationCriteria criteria);

    @GET
    @Path("/{code}")
    public ApplicationResponse getApplication(@PathParam("code") String code);
}
