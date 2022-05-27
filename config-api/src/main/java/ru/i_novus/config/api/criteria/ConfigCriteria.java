package ru.i_novus.config.api.criteria;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel("Критерии поиска настроек")
public class ConfigCriteria extends AbstractCriteria {

    @QueryParam("code")
    @ApiParam("Код настройки")
    private String code;

    @QueryParam("name")
    @ApiParam("Наименование настройки")
    private String name;

    @QueryParam("groupId")
    @ApiParam("Идентификаторы групп")
    private List<Integer> groupIds;

    @QueryParam("applicationCode")
    @ApiParam("Компоненты систем")
    private List<String> applicationCodes;

    @QueryParam("isCommonSystemConfig")
    @ApiParam("Признак того, что настройка является общесистемной")
    private Boolean isCommonSystemConfig;

    @Override
    protected List<Sort.Order> getDefaultOrders() {
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "application.code", Sort.NullHandling.NULLS_FIRST));
        orders.add(new Sort.Order(Sort.Direction.ASC, "code", Sort.NullHandling.NATIVE));
        return orders;
    }
}
