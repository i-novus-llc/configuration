package ru.i_novus.config.api.service;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.config.api.criteria.GroupCriteria;
import ru.i_novus.config.api.model.GroupForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Интерфейс REST API для работы с группами настроек
 */
@Valid
@Path("/groups")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("REST сервис для работы с группами настроек")
public interface ConfigGroupRestService {

    @GET
    @Path("/{groupId}")
    @ApiOperation(value = "Получение группы настроек по идентификатору", response = GroupForm.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение группы настроек"),
            @ApiResponse(code = 404, message = "Группа настроек не найдена")
    })
    GroupForm getGroup(@PathParam("groupId") @ApiParam(value = "Идентификатор группы") Integer groupId);

    @GET
    @Path("/byCode/{code}")
    @ApiOperation(value = "Получение групп настроек по коду настройки", response = GroupForm.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение групп настроек")
    List<GroupForm> getGroupByConfigCode(@PathParam("code") @ApiParam(value = "Код настройки")String code);

    @GET
    @Path("/")
    @ApiOperation(value = "Получение групп настроек", response = GroupForm.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение групп настроек")
    Page<GroupForm> getAllGroup(@BeanParam GroupCriteria criteria);

    @POST
    @Path("/")
    @ApiOperation(value = "Добавление группы настроек")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Сохранение группы успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос")
    })
    Integer saveGroup(@Valid @NotNull @ApiParam(name = "Новая группа настроек", required = true)
                              GroupForm groupForm);

    @PUT
    @Path("/{groupId}")
    @ApiOperation(value = "Изменение группы настроек")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Изменение группы успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос"),
            @ApiResponse(code = 404, message = "Группа не найдена")
    })
    void updateGroup(@PathParam("groupId") @ApiParam(value = "Идентификатор группы") Integer groupId,
                     @Valid @NotNull @ApiParam(name = "Обновленная группа настроек", required = true)
                             GroupForm groupForm);

    @DELETE
    @Path("/{groupId}")
    @ApiOperation(value = "Удаление группы настроек")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Группа успешно удалена"),
            @ApiResponse(code = 404, message = "Группа не найдена")
    })
    void deleteGroup(@PathParam("groupId") @ApiParam(value = "Идентификатор группы") Integer groupId);
}
