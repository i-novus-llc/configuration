package ru.i_novus.configuration_api.criteria;

import net.n2oapp.platform.jaxrs.RestCriteria;

public class AbstractCriteria extends RestCriteria {
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
}
