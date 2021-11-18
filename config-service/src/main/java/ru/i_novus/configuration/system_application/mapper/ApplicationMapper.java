package ru.i_novus.configuration.system_application.mapper;

import ru.i_novus.configuration.system_application.CommonSystemResponse;
import ru.i_novus.configuration.system_application.entity.ApplicationEntity;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.model.SimpleApplicationResponse;
import ru.i_novus.system_application.api.model.SimpleSystemResponse;

public class ApplicationMapper {

    public static ApplicationResponse toApplicationResponse(ApplicationEntity applicationEntity) {
        ApplicationResponse applicationResponse = new ApplicationResponse();
        applicationResponse.setCode(applicationEntity.getCode());
        applicationResponse.setName(applicationEntity.getName());
        applicationResponse.setSystem(
                applicationEntity.getSystem() == null ? null : SystemMapper.toSimpleSystemResponse(applicationEntity.getSystem())
        );
        return applicationResponse;
    }

    public static SimpleApplicationResponse toSimpleApplicationResponse(ApplicationEntity applicationEntity) {
        SimpleApplicationResponse simpleApplicationResponse = new SimpleApplicationResponse();
        simpleApplicationResponse.setCode(applicationEntity.getCode());
        simpleApplicationResponse.setName(applicationEntity.getName());
        simpleApplicationResponse.setSystemCode(applicationEntity.getSystem().getCode());
        return simpleApplicationResponse;
    }

    public static ApplicationResponse getCommonSystemApplication() {
        ApplicationResponse applicationResponse = new ApplicationResponse();
        CommonSystemResponse commonSystemResponse = new CommonSystemResponse();
        SimpleSystemResponse simpleSystemResponse = new SimpleSystemResponse();
        simpleSystemResponse.setCode(commonSystemResponse.getCode());
        simpleSystemResponse.setName(commonSystemResponse.getName());
        applicationResponse.setSystem(simpleSystemResponse);
        return applicationResponse;
    }
}
