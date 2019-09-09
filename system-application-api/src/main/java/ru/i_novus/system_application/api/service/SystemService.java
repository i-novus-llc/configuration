package ru.i_novus.system_application.api.service;

import org.springframework.data.domain.Page;
import ru.i_novus.system_application.api.model.SystemResponse;
import ru.i_novus.system_application.api.criteria.SystemCriteria;


/**
 * Интерфейс API для получения прикладных систем
 */
public interface SystemService {

    public Page<SystemResponse> getAllSystem(SystemCriteria criteria);

    public SystemResponse getSystem(String code);
}
