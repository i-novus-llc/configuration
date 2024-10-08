package ru.i_novus.config.api.service;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.config.api.criteria.ApplicationCriteria;
import ru.i_novus.config.api.model.ApplicationResponse;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;


/**
 * Интерфейс API для работы с приложениями
 */
@Valid
@Path("/applications/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("Сервис для получения приложений")
public interface ApplicationRestService {

    @GET
    @Path("/")
    @ApiOperation(value = "Получение всех приложений", response = ApplicationResponse.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение списка приложений")
    Page<ApplicationResponse> getAllApplications(@BeanParam ApplicationCriteria criteria);

    @GET
    @Path("/{code}")
    @ApiOperation(value = "Получение приложения", response = ApplicationResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение приложения"),
            @ApiResponse(code = 404, message = "Приложение не найдено")
    })
    ApplicationResponse getApplication(@PathParam("code") @ApiParam(value = "Код приложения") String code);
}
