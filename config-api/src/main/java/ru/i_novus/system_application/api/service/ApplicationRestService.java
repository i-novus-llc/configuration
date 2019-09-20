package ru.i_novus.system_application.api.service;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.config.api.model.GroupedConfigRequest;
import ru.i_novus.system_application.api.criteria.ApplicationCriteria;
import ru.i_novus.system_application.api.model.ApplicationResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;


/**
 * Интерфейс API для получения приложений
 */
@Valid
@Path("/auth/applications/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("Сервис для получения приложений")
public interface ApplicationRestService {

    @GET
    @Path("/")
    @ApiOperation(value = "Получение всех приложений", response = ApplicationResponse.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение списка приложений")
    public Page<ApplicationResponse> getAllApplication(@ApiParam(name = "Критерии поиска приложений")
                                                           @BeanParam ApplicationCriteria criteria);

    @GET
    @Path("/{code}")
    @ApiOperation(value = "Получение приложения", response = ApplicationResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение приложения"),
            @ApiResponse(code = 404, message = "Приложение не найдено")
    })
    public ApplicationResponse getApplication(@PathParam("code") @ApiParam(name = "Код приложения") String code);

    @GET
    @Path("/{code}/config")
    @ApiOperation(value = "Получение сгруппированных настроек приложения", response = GroupedConfigRequest.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение сгрупированных настроек приложения"),
            @ApiResponse(code = 404, message = "Приложение не найдено")
    })
    List<GroupedConfigRequest> getGroupedApplicationConfig(@PathParam("code") @ApiParam(name = "Код приложения") String code);

    @POST
    @Path("/{code}/config")
    @ApiOperation(value = "Изменение значений настроек приложения")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Изменение значений настроек приложения успешно выполнено"),
            @ApiResponse(code = 400, message = "Некорректный запрос"),
            @ApiResponse(code = 404, message = "Приложение не найдено")
    })
    void saveApplicationConfig(@PathParam("code") @ApiParam(name = "Код приложения") String code,
                               @Valid @NotNull @ApiParam(name = "Пары значений (код настройки / значение)", required = true)
                                       Map<String, Object> data);
}
