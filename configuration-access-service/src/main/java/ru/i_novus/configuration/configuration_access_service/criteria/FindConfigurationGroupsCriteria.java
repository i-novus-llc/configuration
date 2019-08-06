package ru.i_novus.configuration.configuration_access_service.criteria;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.QueryParam;

/**
 * Критерий поиска групп настроек
 */
@Getter
@Setter
public class FindConfigurationGroupsCriteria extends AbstractCriteria {

    @QueryParam("groupName")
    @ApiParam(value = "Имя группы")
    private String groupName;

    @QueryParam("groupCode")
    @ApiParam(value = "Код группы")
    private String groupCode;
}
