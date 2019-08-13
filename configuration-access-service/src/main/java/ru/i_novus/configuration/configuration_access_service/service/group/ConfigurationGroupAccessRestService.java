package ru.i_novus.configuration.configuration_access_service.service.group;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.configuration.configuration_access_service.criteria.FindConfigurationGroupCriteria;
import ru.i_novus.configuration.configuration_access_service.items.ConfigurationGroupResponseItem;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Интерфейс REST API для работы с группами настроек
 */
@Valid
@Path("/configurations/group")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис для работы с группами настроек")
public interface ConfigurationGroupAccessRestService {

    @GET
    @Path("/{groupId}")
    @ApiOperation(value = "Получение группы настроек", response = ConfigurationGroupResponseItem.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение группы настроек"),
            @ApiResponse(code = 404, message = "Группа настроек не найдена")
    })
    ConfigurationGroupResponseItem getConfigurationGroup(@PathParam("groupId") @ApiParam(name = "Идентификатор группы") Integer groupId);

    @GET
    @Path("/")
    @ApiOperation(value = "Получение групп настроек", response = ConfigurationGroupResponseItem.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение групп настроек")
    Page<ConfigurationGroupResponseItem> getAllConfigurationsGroup(@BeanParam FindConfigurationGroupCriteria criteria);

    @POST
    @Path("/")
    @ApiOperation(value = "Добавление группы настроек")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Сохранение группы успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос")
    })
    Integer saveConfigurationGroup(@Valid @NotNull @ApiParam(name = "Новая группа настроек", required = true)
                                           ConfigurationGroupResponseItem configurationGroupItem);

    @PUT
    @Path("/{groupId}")
    @ApiOperation(value = "Изменение группы настроек")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Изменение группы успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос"),
            @ApiResponse(code = 404, message = "Группа не найдена")
    })
    void updateConfigurationGroup(@PathParam("groupId") @ApiParam(name = "Идентификатор группы") Integer groupId,
                                     @Valid @NotNull @ApiParam(name = "Обновленная группа настроек", required = true)
                                             ConfigurationGroupResponseItem configurationGroupItem);

    @DELETE
    @Path("/{groupId}")
    @ApiOperation(value = "Удаление группы настроек")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Группа успешно удалена"),
            @ApiResponse(code = 404, message = "Группа не найдена")
    })
    void deleteConfigurationGroup(@PathParam("groupId") @ApiParam(name = "Идентификатор группы") Integer groupId);
}
