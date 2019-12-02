package ru.i_novus.config.api.service;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.ConfigResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Интерфейс REST API для работы с настройками
 */
@Valid
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("Сервис для работы с настройками")
public interface ConfigRestService {

    @GET
    @Path("/{code}")
    @ApiOperation(value = "Получение настройки", response = ConfigResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение настройки"),
            @ApiResponse(code = 404, message = "Настройка не найдена")
    })
    ConfigResponse getConfig(@PathParam("code") @ApiParam(value = "Код настройки") String code);

    @GET
    @Path("/")
    @ApiOperation(value = "Получение всех настроек", response = ConfigResponse.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение списка настроек")
    Page<ConfigResponse> getAllConfig(@ApiParam(name = "Критерии поиска настроек")
                                      @BeanParam ConfigCriteria criteria);

    @POST
    @Path("/")
    @ApiOperation(value = "Добавление настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Сохранение настройки успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос")
    })
    void saveConfig(@Valid @NotNull @ApiParam(name = "Новая настройка", required = true)
                            ConfigForm configForm);

    @PUT
    @Path("/{code}")
    @ApiOperation(value = "Изменение настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Изменение настройки успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос"),
            @ApiResponse(code = 404, message = "Настройка не найдена")
    })
    void updateConfig(@PathParam("code") @ApiParam(value = "Код настройки") String code,
                      @Valid @NotNull @ApiParam(name = "Обновленная настройка", required = true)
                              ConfigForm configForm);

    @DELETE
    @Path("/{code}")
    @ApiOperation(value = "Удаление настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Настройка успешно удалена"),
            @ApiResponse(code = 404, message = "Настройка не найдена")
    })
    void deleteConfig(@PathParam("code") @ApiParam(value = "Код настройки") String code);
}
