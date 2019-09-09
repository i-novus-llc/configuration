package ru.i_novus.system_application.api.criteria;

import java.util.List;


/**
 * Критерии поиска систем
 */
public class SystemCriteria extends AbstractCriteria {

    /**
     * Коды систем
     */
    private List<String> codes;

    /**
     * Код приложения
     */
    private String appCode;
}
