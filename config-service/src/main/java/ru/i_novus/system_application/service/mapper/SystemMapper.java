package ru.i_novus.system_application.service.mapper;

import ru.i_novus.system_application.api.model.SystemRequest;
import ru.i_novus.system_application.api.model.SystemResponse;
import ru.i_novus.system_application.service.entity.SystemEntity;

import java.util.stream.Collectors;

public class SystemMapper {

    public static SystemRequest toSystemRequest(SystemEntity systemEntity) {
        SystemRequest systemRequest = new SystemRequest();
        systemRequest.setCode(systemEntity.getCode());
        systemRequest.setName(systemEntity.getName());
        systemRequest.setDescription(systemEntity.getDescription());
        return systemRequest;
    }

    public static SystemResponse toSystemResponse(SystemEntity systemEntity) {
        SystemResponse systemResponse = new SystemResponse();
        systemResponse.setCode(systemEntity.getCode());
        systemResponse.setName(systemEntity.getName());
        systemResponse.setDescription(systemEntity.getDescription());
        systemResponse.setApplications(
                systemEntity.getApplications().stream()
                        .map(ApplicationMapper::toApplicationRequest)
                        .collect(Collectors.toList())
        );
        return systemResponse;
    }
}
