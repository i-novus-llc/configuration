package ru.i_novus.configuration.configuration_access_service.service.metadata;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.configuration.configuration_access_service.criteria.FindConfigurationCriteria;
import ru.i_novus.configuration.configuration_access_service.items.ConfigurationResponseItem;

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
public interface ConfigurationAccessRestService {

    @GET
    @Path("/{configurationCode}")
    @ApiOperation(value = "Получение настройки", response = ConfigurationResponseItem.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение настройки"),
            @ApiResponse(code = 404, message = "Настройка не найдена")
    })
    ConfigurationResponseItem getConfiguration(@PathParam("configurationCode") @ApiParam(name = "Код настройки") String code);

    @GET
    @Path("/")
    @ApiOperation(value = "Получение всех настроек", response = ConfigurationResponseItem.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение списка настроек")
    Page<ConfigurationResponseItem> getAllConfigurations(@ApiParam(name = "Критерии поиска настроек")
                                                         @BeanParam FindConfigurationCriteria criteria);

    @POST
    @Path("/")
    @ApiOperation(value = "Добавление настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Сохранение настройки успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос")
    })
    void saveConfiguration(@Valid @NotNull @ApiParam(name = "Новая настройки", required = true)
                                           ConfigurationResponseItem configurationResponseItem);

    @PUT
    @Path("/{configurationCode}")
    @ApiOperation(value = "Изменение настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Изменение настройки успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос"),
            @ApiResponse(code = 404, message = "Настройка не найдена")
    })
    @Transactional
    void updateConfiguration(@PathParam("configurationCode") @ApiParam(name = "Код настройки") String code,
                             @Valid @NotNull @ApiParam(name = "Обновленная настройка", required = true)
                                             ConfigurationResponseItem configurationResponseItem);

    @DELETE
    @Path("/{configurationCode}")
    @ApiOperation(value = "Удаление настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Настройка успешно удалена"),
            @ApiResponse(code = 404, message = "Настройка не найдена")
    })
    void deleteConfiguration(@PathParam("configurationCode") @ApiParam(name = "Код настройки") String code);
}
