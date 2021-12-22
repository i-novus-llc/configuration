package ru.i_novus.config.api.service;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.config.api.criteria.ApplicationConfigCriteria;
import ru.i_novus.config.api.model.ApplicationConfigResponse;
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
    Page<ConfigGroupResponse> getAllConfigs(@BeanParam ApplicationConfigCriteria criteria);

    @GET
    @Path("/{code}")
    @ApiOperation(value = "Получение общесистемной настройки", response = ApplicationConfigResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение общесистемной настройки"),
            @ApiResponse(code = 404, message = "Общесистемная настройка не найдена")
    })
    ApplicationConfigResponse getConfig(@PathParam("code") @ApiParam(value = "Код настройки") String code);

    @POST
    @Path("/{code}")
    @ApiOperation(value = "Изменение значения общесистемной настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Изменение значения общесистемной настройки успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос"),
            @ApiResponse(code = 404, message = "Общесистемная настройка не найдена")
    })
    void saveApplicationConfig(@PathParam("code") @ApiParam(value = "Код настройки") String code,
                               @FormParam("value") @ApiParam(name = "Значение настройки", required = true) String value);

}
