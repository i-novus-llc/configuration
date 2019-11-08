package ru.i_novus.config.service.mapper;

import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.ConfigResponse;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.service.entity.ConfigEntity;
import ru.i_novus.system_application.api.model.ApplicationResponse;

public class ConfigMapper {
    
    public static ConfigEntity toConfigEntity(ConfigForm configForm) {
        ConfigEntity configEntity = new ConfigEntity();
        configEntity.setCode(configForm.getCode());
        configEntity.setName(configForm.getName());
        configEntity.setDescription(configForm.getDescription());
        configEntity.setValueType(configForm.getValueType());
        configEntity.setDefaultValue(configForm.getDefaultValue());
        configEntity.setApplicationCode(configForm.getApplicationCode());
        return configEntity;
    }

    public static ConfigResponse toConfigResponse(ConfigEntity configEntity, ApplicationResponse application, GroupForm group) {
        ConfigResponse configResponse = new ConfigResponse();
        configResponse.setCode(configEntity.getCode());
        configResponse.setName(configEntity.getName());
        configResponse.setDescription(configEntity.getDescription());
        configResponse.setValueType(configEntity.getValueType());
        configResponse.setDefaultValue(configEntity.getDefaultValue());
        configResponse.setApplication(application);
        configResponse.setGroup(group);
        return configResponse;
    }

    public static ConfigForm toConfigForm(ConfigEntity configEntity, String value) {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode(configEntity.getCode());
        configForm.setName(configEntity.getName());
        configForm.setDescription(configEntity.getDescription());
        configForm.setValueType(configEntity.getValueType());
        configForm.setValue(value);
        configForm.setDefaultValue(configEntity.getDefaultValue());
        configForm.setApplicationCode(configEntity.getApplicationCode());
        return configForm;
    }
}

