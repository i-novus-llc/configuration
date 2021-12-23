package ru.i_novus.config.api.service;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.config.api.criteria.ApplicationConfigCriteria;
import ru.i_novus.config.api.model.ApplicationConfigResponse;
import ru.i_novus.config.api.model.ConfigsApplicationResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;


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
    Page<ConfigsApplicationResponse> getAllConfigs(@BeanParam ApplicationConfigCriteria criteria);

    @GET
    @Path("/{code}")
    @ApiOperation(value = "Получение настройки приложения", response = ApplicationConfigResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение настройки приложения"),
            @ApiResponse(code = 404, message = "Настройка приложения не найдена")
    })
    ApplicationConfigResponse getConfig(@PathParam("code") @ApiParam(value = "Код настройки") String code);



    // TODO - переделать методы ниже

    @PUT
    @Path("/{code}")
    @ApiOperation(value = "Изменение значений настроек приложения")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Изменение значений настроек приложения успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос"),
            @ApiResponse(code = 404, message = "Приложение не найдено")
    })
    void saveApplicationConfig(@PathParam("code") @ApiParam(value = "Код приложения") String code,
                               @Valid @NotNull @ApiParam(name = "Пары значений (код настройки / значение)", required = true)
                                       Map<String, Object> data);

    @DELETE
    @Path("/{code}/configs")
    @ApiOperation(value = "Удаление значений настроек приложения")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Удаление значений настроек приложения успешно выполнено"),
            @ApiResponse(code = 404, message = "Приложение не найдено")
    })
    void deleteApplicationConfigValue(@PathParam("code") @ApiParam(value = "Код приложения") String code);
}
