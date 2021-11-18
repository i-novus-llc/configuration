package ru.i_novus.configuration.system_application.mapper;

import ru.i_novus.configuration.system_application.entity.SystemEntity;
import ru.i_novus.system_application.api.model.SimpleSystemResponse;
import ru.i_novus.system_application.api.model.SystemResponse;

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
