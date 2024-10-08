package ru.i_novus.config.api.service;

import io.swagger.annotations.*;
import ru.i_novus.config.api.criteria.ApplicationConfigCriteria;
import ru.i_novus.config.api.model.ApplicationConfigResponse;
import ru.i_novus.config.api.model.ConfigValue;
import ru.i_novus.config.api.model.ConfigsApplicationResponse;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;


/**
 * Интерфейс API для работы с настройками приложений
 */
@Valid
@Path("/application_configs/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("Сервис для получения настроек приложений")
public interface ApplicationConfigRestService {

    @GET
    @Path("/")
    @ApiOperation(value = "Получение всех настроек приложений", response = ConfigsApplicationResponse.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение списка настроек приложений")
    List<ConfigsApplicationResponse> getAllConfigs(@BeanParam ApplicationConfigCriteria criteria);

    @GET
    @Path("/{code}")
    @ApiOperation(value = "Получение настройки приложения", response = ApplicationConfigResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение настройки приложения"),
            @ApiResponse(code = 404, message = "Настройка приложения не найдена")
    })
    ApplicationConfigResponse getConfig(@PathParam("code") @ApiParam(value = "Код настройки") String code);

    @PUT
    @Path("/{code}")
    @ApiOperation(value = "Изменение значения настройки приложения")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Изменение значения настройки приложения успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос"),
            @ApiResponse(code = 404, message = "Настройка приложения не найдена")
    })
    void saveConfigValue(@PathParam("code") @ApiParam(value = "Код настройки") String code,
                         @ApiParam(name = "Значение настройки", required = true) ConfigValue configValue);

    @DELETE
    @Path("/{code}")
    @ApiOperation(value = "Удаление значения настройки приложения")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Удаление значения настройки приложения успешно выполнено"),
            @ApiResponse(code = 404, message = "Настройка приложения не найдена")
    })
    void deleteConfigValue(@PathParam("code") @ApiParam(value = "Код настройки") String code);
}
