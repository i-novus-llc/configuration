package ru.i_novus.configuration.configuration_access_service.criteria;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.QueryParam;
import java.util.List;

@Getter
@Setter
@ApiModel("Критерии поиска настроек")
public class FindConfigurationCriteria extends AbstractCriteria{

    @QueryParam("code")
    @ApiParam("Код настройки")
    private String code;

    @QueryParam("name")
    @ApiParam("Наименование настройки")
    private String name;

    @QueryParam("group")
    @ApiParam("Наименование групп")
    private List<String> groupNames;

    @QueryParam("system")
    @ApiParam("Наименование систем")
    private List<String> systemNames;
}
