package ru.i_novus.config.api.criteria;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ApiModel("Критерии поиска настроек приложений")
public class ApplicationConfigCriteria extends AbstractCriteria {

    @QueryParam("applicationCode")
    @ApiParam("Компоненты систем")
    private List<String> applicationCodes;

    @QueryParam("groupId")
    @ApiParam("Идентификаторы групп")
    private List<Integer> groupIds;

    @QueryParam("configName")
    @ApiParam("Наименование настройки")
    private String configName;

    @QueryParam("withValue")
    @ApiParam("С заданным значением")
    private Boolean withValue;

    @QueryParam("isCommonSystem")
    @ApiParam("Общесистемные настройки")
    private Boolean isCommonSystem;

    @Override
    protected List<Sort.Order> getDefaultOrders() {
        return Arrays.asList(new Sort.Order(Sort.Direction.ASC, "code"));
    }
}
