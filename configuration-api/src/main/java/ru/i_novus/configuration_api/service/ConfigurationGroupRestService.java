package ru.i_novus.configuration_api.service;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.configuration_api.criteria.FindGroupCriteria;
import ru.i_novus.configuration_api.items.GroupResponseItem;

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
public interface ConfigurationGroupRestService {

    @GET
    @Path("/{groupId}")
    @ApiOperation(value = "Получение группы настроек", response = GroupResponseItem.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение группы настроек"),
            @ApiResponse(code = 404, message = "Группа настроек не найдена")
    })
    GroupResponseItem getGroup(@PathParam("groupId") @ApiParam(name = "Идентификатор группы") Integer groupId);

    @GET
    @Path("/")
    @ApiOperation(value = "Получение групп настроек", response = GroupResponseItem.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение групп настроек")
    Page<GroupResponseItem> getAllGroup(@BeanParam FindGroupCriteria criteria);

    @POST
    @Path("/")
    @ApiOperation(value = "Добавление группы настроек")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Сохранение группы успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос")
    })
    Integer saveGroup(@Valid @NotNull @ApiParam(name = "Новая группа настроек", required = true)
                                           GroupResponseItem groupResponseItem);

    @PUT
    @Path("/{groupId}")
    @ApiOperation(value = "Изменение группы настроек")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Изменение группы успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос"),
            @ApiResponse(code = 404, message = "Группа не найдена")
    })
    void updateGroup(@PathParam("groupId") @ApiParam(name = "Идентификатор группы") Integer groupId,
                     @Valid @NotNull @ApiParam(name = "Обновленная группа настроек", required = true)
                                             GroupResponseItem groupResponseItem);

    @DELETE
    @Path("/{groupId}")
    @ApiOperation(value = "Удаление группы настроек")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Группа успешно удалена"),
            @ApiResponse(code = 404, message = "Группа не найдена")
    })
    void deleteGroup(@PathParam("groupId") @ApiParam(name = "Идентификатор группы") Integer groupId);
}
