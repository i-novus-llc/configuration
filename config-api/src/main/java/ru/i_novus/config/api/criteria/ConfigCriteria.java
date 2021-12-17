package ru.i_novus.config.api.criteria;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import javax.ws.rs.QueryParam;
import java.util.Collections;
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

    @Override
    protected List<Sort.Order> getDefaultOrders() {
        return Collections.emptyList();
    }
}
