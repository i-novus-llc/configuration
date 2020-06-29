package ru.i_novus.system_application.api.criteria;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import ru.i_novus.config.api.criteria.AbstractCriteria;

import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ApiModel("Критерии поиска приложений")
public class ApplicationCriteria extends AbstractCriteria {

    @QueryParam("systemCode")
    @ApiParam("Код системы")
    private String systemCode;

    @Override
    protected List<Sort.Order> getDefaultOrders() {
        return Arrays.asList(new Sort.Order(Sort.Direction.ASC, "code"));
    }
}
