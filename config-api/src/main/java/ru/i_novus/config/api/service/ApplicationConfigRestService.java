package ru.i_novus.config.api.service;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.config.api.criteria.ApplicationConfigCriteria;
import ru.i_novus.config.api.model.ApplicationResponse;
import ru.i_novus.config.api.model.GroupedApplicationConfig;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;


/**
 * Интерфейс API для работы с приложениями
 */
@Valid
@Path("/application_configs/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("Сервис для получения настроек приложений")
public interface ApplicationConfigRestService {




    @GET
    @Path("/")
    @ApiOperation(value = "Получение настроек приложений / общесистемных", response = ApplicationResponse.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение списка настроек приложений / общесистемных")
    Page<ApplicationResponse> getAllApplicationConfig(@BeanParam ApplicationConfigCriteria criteria);

    @GET
    @Path("/{code}")
    @ApiOperation(value = "Получение приложения", response = ApplicationResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение приложения"),
            @ApiResponse(code = 404, message = "Приложение не найдено")
    })
    ApplicationResponse getApplication(@PathParam("code") @ApiParam(value = "Код приложения") String code);

    @GET
    @Path("/{code}/configs")
    @ApiOperation(value = "Получение сгруппированных настроек приложения", response = GroupedApplicationConfig.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение сгрупированных настроек приложения"),
            @ApiResponse(code = 404, message = "Приложение не найдено")
    })
    List<GroupedApplicationConfig> getGroupedApplicationConfig(@PathParam("code") @ApiParam(value = "Код приложения") String code);

    @PUT
    @Path("/{code}/configs")
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
    void deleteApplicationConfig(@PathParam("code") @ApiParam(value = "Код приложения") String code);
}
