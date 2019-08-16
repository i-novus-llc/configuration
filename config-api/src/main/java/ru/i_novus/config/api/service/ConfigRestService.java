package ru.i_novus.config.api.service;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.config.api.criteria.FindConfigCriteria;
import ru.i_novus.config.api.items.ConfigForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Интерфейс REST API для работы с настройками
 */
@Valid
@Path("/configurations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис для работы с настройками")
public interface ConfigRestService {

    @GET
    @Path("/{configCode}")
    @ApiOperation(value = "Получение настройки", response = ConfigForm.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение настройки"),
            @ApiResponse(code = 404, message = "Настройка не найдена")
    })
    ConfigForm getConfig(@PathParam("configCode") @ApiParam(name = "Код настройки") String code);

    @GET
    @Path("/")
    @ApiOperation(value = "Получение всех настроек", response = ConfigForm.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение списка настроек")
    Page<ConfigForm> getAllConfig(@ApiParam(name = "Критерии поиска настроек")
                                                         @BeanParam FindConfigCriteria criteria);

    @POST
    @Path("/")
    @ApiOperation(value = "Добавление настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Сохранение настройки успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос")
    })
    void saveConfig(@Valid @NotNull @ApiParam(name = "Новая настройки", required = true)
                                   ConfigForm configForm);

    @PUT
    @Path("/{configCode}")
    @ApiOperation(value = "Изменение настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Изменение настройки успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос"),
            @ApiResponse(code = 404, message = "Настройка не найдена")
    })
    void updateConfig(@PathParam("configCode") @ApiParam(name = "Код настройки") String code,
                             @Valid @NotNull @ApiParam(name = "Обновленная настройка", required = true)
                                     ConfigForm configForm);

    @DELETE
    @Path("/{configCode}")
    @ApiOperation(value = "Удаление настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Настройка успешно удалена"),
            @ApiResponse(code = 404, message = "Настройка не найдена")
    })
    void deleteConfig(@PathParam("configCode") @ApiParam(name = "Код настройки") String code);
}
