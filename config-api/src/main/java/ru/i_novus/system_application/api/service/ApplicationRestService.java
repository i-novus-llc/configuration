package ru.i_novus.system_application.api.service;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.system_application.api.criteria.ApplicationCriteria;
import ru.i_novus.system_application.api.model.ApplicationResponse;

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
@Api("Сервис для получения приложений")
public interface ApplicationRestService {

    @GET
    @Path("/")
    @ApiOperation(value = "Получение всех приложений", response = ApplicationResponse.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение списка приложений")
    public Page<ApplicationResponse> getAllApplication(@ApiParam(name = "Критерии поиска приложений")
                                                           @BeanParam ApplicationCriteria criteria);

    @GET
    @Path("/{code}")
    @ApiOperation(value = "Получение приложения", response = ApplicationResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение приложения"),
            @ApiResponse(code = 404, message = "Приложение не найдено")
    })
    public ApplicationResponse getApplication(@PathParam("code") @ApiParam(name = "Код приложения") String code);
}
