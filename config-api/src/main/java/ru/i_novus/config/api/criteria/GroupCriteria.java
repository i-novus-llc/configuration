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
@ApiModel("Критерий поиска групп настроек")
public class GroupCriteria extends AbstractCriteria {

    @QueryParam("name")
    @ApiParam("Имя группы")
    private String name;

    @QueryParam("code")
    @ApiParam("Код группы")
    private String code;

    @Override
    protected List<Sort.Order> getDefaultOrders() {
        return Arrays.asList(new Sort.Order(Sort.Direction.ASC, "id"));
    }
}
