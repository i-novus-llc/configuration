package ru.i_novus.system_application.api.criteria;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import ru.i_novus.config.api.criteria.AbstractCriteria;

import javax.ws.rs.QueryParam;

@Getter
@Setter
@ApiModel("Критерии поиска приложений")
public class ApplicationCriteria extends AbstractCriteria {

    @QueryParam("systemCode")
    @ApiParam("Код системы")
    private String systemCode;
}
