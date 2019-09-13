package ru.i_novus.config.service.mapper;

import ru.i_novus.config.api.model.ConfigRequest;
import ru.i_novus.config.api.model.ConfigResponse;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.service.entity.ConfigEntity;
import ru.i_novus.system_application.api.model.ApplicationResponse;

public class ConfigMapper {
    
    public static ConfigEntity toConfigEntity(ConfigRequest configRequest) {
        ConfigEntity configEntity = new ConfigEntity();
        configEntity.setCode(configRequest.getCode());
        configEntity.setName(configRequest.getName());
        configEntity.setDescription(configRequest.getDescription());
        configEntity.setValueType(configRequest.getValueType());
        configEntity.setApplicationCode(configRequest.getApplicationCode());
        return configEntity;
    }

    public static ConfigResponse toConfigResponse(ConfigEntity configEntity, String value,
                                                  ApplicationResponse application, GroupForm group) {
        ConfigResponse configResponse = new ConfigResponse();
        configResponse.setCode(configEntity.getCode());
        configResponse.setName(configEntity.getName());
        configResponse.setDescription(configEntity.getDescription());
        configResponse.setValueType(configEntity.getValueType());
        configResponse.setValue(value);
        configResponse.setApplication(application);
        configResponse.setGroup(group);
        return configResponse;
    }

    public static ConfigRequest toConfigRequest(ConfigEntity configEntity, String value) {
        ConfigRequest configRequest = new ConfigRequest();
        configRequest.setCode(configEntity.getCode());
        configRequest.setName(configEntity.getName());
        configRequest.setDescription(configEntity.getDescription());
        configRequest.setValueType(configEntity.getValueType());
        configRequest.setValue(value);
        configRequest.setApplicationCode(configEntity.getApplicationCode());
        return configRequest;
    }
}

