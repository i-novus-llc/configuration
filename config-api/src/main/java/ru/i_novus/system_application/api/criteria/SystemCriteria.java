package ru.i_novus.system_application.api.criteria;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import ru.i_novus.config.api.criteria.AbstractCriteria;

import javax.ws.rs.QueryParam;
import java.util.List;


@ApiModel("Критерии поиска прикладных систем")
@Getter
@Setter
public class SystemCriteria extends AbstractCriteria {

    @QueryParam("code")
    @ApiParam("Коды систем")
    private List<String> codes;

    @QueryParam("appCode")
    @ApiParam("Код приложения")
    private String appCode;
}