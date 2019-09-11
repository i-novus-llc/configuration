package ru.i_novus.system_application.api.service;

import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import ru.i_novus.system_application.api.criteria.SystemCriteria;
import ru.i_novus.system_application.api.model.SystemResponse;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


/**
 * Интерфейс API для получения прикладных систем
 */
@Valid
@Path("/auth/systems/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api("Сервис для получения прикладных систем")
public interface SystemRestService {

    @GET
    @Path("/")
    @ApiOperation(value = "Получение всех систем", response = SystemResponse.class, responseContainer = "List")
    @ApiResponse(code = 200, message = "Успешное получение списка систем")
    public Page<SystemResponse> getAllSystem(@ApiParam(name = "Критерии поиска систем")
                                                 @BeanParam SystemCriteria criteria);

    @GET
    @Path("/{code}")
    @ApiOperation(value = "Получение системы", response = SystemResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение системы"),
            @ApiResponse(code = 404, message = "Система не найдена")
    })
    public SystemResponse getSystem(@PathParam("code") @ApiParam(name = "Код системы") String code);
}
