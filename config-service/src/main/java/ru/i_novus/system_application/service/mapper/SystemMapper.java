package ru.i_novus.system_application.service.mapper;

import ru.i_novus.system_application.api.model.SimpleSystemResponse;
import ru.i_novus.system_application.api.model.SystemResponse;
import ru.i_novus.system_application.service.entity.SystemEntity;

import java.util.stream.Collectors;

public class SystemMapper {

    public static SimpleSystemResponse toSimpleSystemResponse(SystemEntity systemEntity) {
        SimpleSystemResponse simpleSystemResponse = new SimpleSystemResponse();
        simpleSystemResponse.setCode(systemEntity.getCode());
        simpleSystemResponse.setName(systemEntity.getName());
        simpleSystemResponse.setDescription(systemEntity.getDescription());
        return simpleSystemResponse;
    }

    public static SystemResponse toSystemResponse(SystemEntity systemEntity) {
        SystemResponse systemResponse = new SystemResponse();
        systemResponse.setCode(systemEntity.getCode());
        systemResponse.setName(systemEntity.getName());
        systemResponse.setDescription(systemEntity.getDescription());
        systemResponse.setApplications(
                systemEntity.getApplications().stream()
                        .map(ApplicationMapper::toSimpleApplicationResponse)
                        .collect(Collectors.toList())
        );
        return systemResponse;
    }
}
