package ru.i_novus.system_application.service.mapper;

import ru.i_novus.system_application.api.model.ApplicationRequest;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.model.SystemRequest;
import ru.i_novus.system_application.service.CommonSystemResponse;
import ru.i_novus.system_application.service.entity.ApplicationEntity;

public class ApplicationMapper {

    public static ApplicationResponse toApplicationResponse(ApplicationEntity applicationEntity) {
        ApplicationResponse applicationResponse = new ApplicationResponse();
        applicationResponse.setCode(applicationEntity.getCode());
        applicationResponse.setName(applicationEntity.getName());
        applicationResponse.setSystem(
                applicationEntity.getSystem() == null ? null : SystemMapper.toSystemRequest(applicationEntity.getSystem())
        );
        return applicationResponse;
    }

    public static ApplicationRequest toApplicationRequest(ApplicationEntity applicationEntity) {
        ApplicationRequest applicationRequest = new ApplicationRequest();
        applicationRequest.setCode(applicationEntity.getCode());
        applicationRequest.setName(applicationEntity.getName());
        applicationRequest.setSystemCode(applicationEntity.getSystem().getCode());
        return applicationRequest;
    }

    public static ApplicationResponse getCommonSystemApplication() {
        ApplicationResponse applicationResponse = new ApplicationResponse();
        CommonSystemResponse commonSystemResponse = new CommonSystemResponse();
        SystemRequest systemRequest = new SystemRequest();
        systemRequest.setCode(commonSystemResponse.getCode());
        systemRequest.setName(commonSystemResponse.getName());
        applicationResponse.setSystem(systemRequest);
        return applicationResponse;
    }
}
