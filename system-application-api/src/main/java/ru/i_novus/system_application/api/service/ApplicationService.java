package ru.i_novus.system_application.api.service;

import org.springframework.data.domain.Page;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.criteria.ApplicationCriteria;

/**
 * Интерфейс API для получения приложений
 */
public interface ApplicationService {

    public Page<ApplicationResponse> getAllApplication(ApplicationCriteria criteria);

    public ApplicationResponse getApplication(String code);
}
