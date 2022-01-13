package ru.i_novus.config.api.criteria;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.n2oapp.platform.jaxrs.RestCriteria;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.util.List;

public abstract class AbstractCriteria extends RestCriteria {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_SIZE = Integer.MAX_VALUE;


    public AbstractCriteria() {
        super(DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
    }

    public void noPagination() {
        setPageSize(MAX_SIZE);
        setPageNumber(DEFAULT_PAGE);
    }

    @Override
    public Sort getSort() {
        Sort sort = super.getSort();
        if (!CollectionUtils.isEmpty(getDefaultOrders()) &&
                sort.stream().noneMatch(s -> s.equals(getDefaultOrders().get(0)))) {
            List<Sort.Order> result = sort.and(getDefaultOrders()).toList();
            setOrders(result);
            return Sort.by(result);
        }
        return sort;
    }

    @JsonIgnore
    public Boolean isDefaultSort() {
        return getSort().toList().equals(getDefaultOrders());
    }

}
