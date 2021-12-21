package ru.i_novus.config.api.criteria;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ApiModel("Критерии поиска приложений")
public class ApplicationConfigCriteria extends AbstractCriteria {

    @Override
    protected List<Sort.Order> getDefaultOrders() {
        return Arrays.asList(new Sort.Order(Sort.Direction.ASC, "code"));
    }
}
