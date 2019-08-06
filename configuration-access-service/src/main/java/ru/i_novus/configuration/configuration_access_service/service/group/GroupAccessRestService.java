package ru.i_novus.configuration.configuration_access_service.service.group;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.configuration.configuration_access_service.criteria.FindConfigurationGroupsCriteria;
import ru.i_novus.configuration.configuration_access_service.entity.group.ConfigurationGroupEntity;
import ru.i_novus.configuration.configuration_access_service.entity.group.ConfigurationGroupResponseItem;

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
public interface GroupAccessRestService {
    @GET
    @Path("/")
    @ApiOperation(value = "Получение групп настроек", response = ConfigurationGroupResponseItem.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение групп настроек")
    Page<ConfigurationGroupEntity> getConfigurationsGroup(@BeanParam FindConfigurationGroupsCriteria criteria);

    @POST
    @Path("/")
    @ApiOperation(value = "Добавление группы настроек")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Сохранение группы успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос")
    })
    Integer saveConfigurationGroup(@Valid @NotNull @ApiParam(name = "Новая группа настроек", required = true)
                                           ConfigurationGroupResponseItem configurationGroupResponseItem);

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
                                             ConfigurationGroupResponseItem configurationGroupResponseItem);

    @DELETE
    @Path("/{groupId}")
    @ApiOperation(value = "Удаление группы настроек")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Группа успешно удалена"),
            @ApiResponse(code = 404, message = "Группа не найдена")
    })
    void deleteConfigurationGroup(@PathParam("groupId") @ApiParam(name = "Идентификатор группы") Integer groupId);
}
