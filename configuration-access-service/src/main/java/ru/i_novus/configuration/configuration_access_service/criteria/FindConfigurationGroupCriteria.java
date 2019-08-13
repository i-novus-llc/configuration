package ru.i_novus.configuration.configuration_access_service.criteria;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.QueryParam;

@Getter
@Setter
@ApiModel("Критерий поиска групп настроек")
public class FindConfigurationGroupCriteria extends AbstractCriteria {

    @QueryParam("groupName")
    @ApiParam("Имя группы")
    private String name;

    @QueryParam("groupCode")
    @ApiParam("Код группы")
    private String code;
}
