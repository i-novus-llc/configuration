package ru.i_novus.config.api.service;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.config.api.model.ConfigRequest;
import ru.i_novus.config.api.model.ConfigResponse;
import ru.i_novus.config.api.model.GroupedConfigForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

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
    ConfigResponse getConfig(@PathParam("code") @ApiParam(name = "Код настройки") String code);

    @GET
    @Path("/")
    @ApiOperation(value = "Получение всех настроек", response = ConfigResponse.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение списка настроек")
    Page<ConfigResponse> getAllConfig(@ApiParam(name = "Критерии поиска настроек")
                                      @BeanParam ConfigCriteria criteria);

    @GET
    @Path("/byAppCode/{code}")
    @ApiOperation(value = "Получение сгруппированных настроек приложения", response = GroupedConfigForm.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение сгрупированных настроек приложения"),
            @ApiResponse(code = 404, message = "Приложение не найдено")
    })
    List<GroupedConfigForm> getGroupedConfigByAppCode(@PathParam("code") String code);

    @POST
    @Path("/byAppCode/{code}")
    @ApiOperation(value = "Изменение значений настроек приложения")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Изменение значений настроек приложения успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос"),
            @ApiResponse(code = 404, message = "Приложение не найдено")
    })
    void saveApplicationConfig(@Valid @NotNull @ApiParam(name = "Пары значений (код настройки / значение)", required = true)
                                       Map<String, Object> data);

    @POST
    @Path("/")
    @ApiOperation(value = "Добавление настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Сохранение настройки успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос")
    })
    void saveConfig(@Valid @NotNull @ApiParam(name = "Новая настройка", required = true)
                            ConfigRequest configRequest);

    @PUT
    @Path("/{code}")
    @ApiOperation(value = "Изменение настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Изменение настройки успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос"),
            @ApiResponse(code = 404, message = "Настройка не найдена")
    })
    void updateConfig(@PathParam("code") @ApiParam(name = "Код настройки") String code,
                      @Valid @NotNull @ApiParam(name = "Обновленная настройка", required = true)
                              ConfigRequest configRequest);

    @DELETE
    @Path("/{code}")
    @ApiOperation(value = "Удаление настройки")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Настройка успешно удалена"),
            @ApiResponse(code = 404, message = "Настройка не найдена")
    })
    void deleteConfig(@PathParam("code") @ApiParam(name = "Код настройки") String code);
}
