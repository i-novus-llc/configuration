package ru.i_novus.configuration.config.mapper;

import ru.i_novus.configuration.config.entity.ApplicationEntity;
import ru.i_novus.config.api.model.ApplicationResponse;

public class ApplicationMapper {

    public static ApplicationResponse toApplicationResponse(ApplicationEntity applicationEntity) {
        ApplicationResponse applicationResponse = new ApplicationResponse();
        applicationResponse.setCode(applicationEntity.getCode());
        applicationResponse.setName(applicationEntity.getName());
        return applicationResponse;
    }
}
