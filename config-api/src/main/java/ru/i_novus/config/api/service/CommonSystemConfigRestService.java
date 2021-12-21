package ru.i_novus.config.api.service;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.config.api.criteria.ApplicationConfigCriteria;
import ru.i_novus.config.api.model.ApplicationResponse;
import ru.i_novus.config.api.model.ConfigGroupResponse;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Интерфейс API для работы с общесистемными настройками
 */
@Valid
@Path("/common_system_configs/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("Сервис для получения общесистемных настроек")
public interface CommonSystemConfigRestService {

    @GET
    @Path("/")
    @ApiOperation(value = "Получение общесистемных настроек", response = ConfigGroupResponse.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение списка общесистемных настроек")
    Page<ConfigGroupResponse> getAllConfig(@BeanParam ApplicationConfigCriteria criteria);

    @GET
    @Path("/{code}")
    @ApiOperation(value = "Получение приложения", response = ApplicationResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение приложения"),
            @ApiResponse(code = 404, message = "Приложение не найдено")
    })
    ApplicationResponse getApplication(@PathParam("code") @ApiParam(value = "Код приложения") String code);

}
