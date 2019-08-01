package ru.i_novus.configuration.configuration_access_service.service;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationMetadataItem;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Интерфейс REST API для работы с метаданными настроек
 */
@Valid
@Path("/configurations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис для работы с метаданными настроек")
public interface ConfigurationAccessRestService {

    @GET
    @Path("/")
    @ApiOperation(value = "Список метаданных настроек", response = ConfigurationMetadataItem.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение списка настроек")
    Page<ConfigurationMetadataItem> getAllConfigurationsMetadata();

    @GET
    @Path("/{configurationCode}")
    @ApiOperation(value = "Выборка метаданных настройки", response = ConfigurationMetadataItem.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение метаданных конкретной настройки"),
            @ApiResponse(code = 404, message = "Метаданные не были найдены")
    })
    ConfigurationMetadataItem getConfigurationMetadata(@PathParam("configurationCode") @ApiParam(name = "Код настройки") String code);

    @POST
    @Path("/")
    @ApiOperation(value = "Добавление метаданных настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Сохранение метаданных успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос")
    })
    void saveConfigurationMetadata(@Valid @NotNull @ApiParam(name = "Метаданные новой настройки", required = true)
                                           ConfigurationMetadataItem configurationMetadataItem);

    @PUT
    @Path("/{configurationCode}")
    @ApiOperation(value = "Изменение метаданных настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Изменение метаданных успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос"),
            @ApiResponse(code = 404, message = "Метаданные не были найдены")
    })
    @Transactional
    void updateConfigurationMetadata(@PathParam("configurationCode") @ApiParam(name = "Код настройки") String code,
                                     @Valid @NotNull @ApiParam(name = "Обновленные метаданные настройки", required = true)
                                             ConfigurationMetadataItem configurationMetadataItem);

    @DELETE
    @Path("/{configurationCode}")
    @ApiOperation(value = "Удаление метаданных настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Метаданные настройки успешно удалены"),
            @ApiResponse(code = 404, message = "Метаданные не были найдены")
    })
    void deleteConfigurationMetadata(@PathParam("configurationCode") @ApiParam(name = "Код настройки") String code);
}
