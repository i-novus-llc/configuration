package ru.i_novus.config.api.criteria;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.QueryParam;

@Getter
@Setter
@ApiModel("Критерий поиска групп настроек")
public class GroupCriteria extends AbstractCriteria {

    @QueryParam("groupName")
    @ApiParam("Имя группы")
    private String name;

    @QueryParam("groupCode")
    @ApiParam("Код группы")
    private String code;
}
