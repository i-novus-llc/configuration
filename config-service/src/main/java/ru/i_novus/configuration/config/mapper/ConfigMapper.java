package ru.i_novus.configuration.config.mapper;

import ru.i_novus.config.api.model.*;
import ru.i_novus.configuration.config.entity.ConfigEntity;

public class ConfigMapper {

    public static ConfigEntity toConfigEntity(ConfigForm configForm) {
        ConfigEntity configEntity = new ConfigEntity();
        configEntity.setCode(configForm.getCode());
        return toConfigEntity(configEntity, configForm);
    }

    public static ConfigEntity toConfigEntity(ConfigEntity configEntity, ConfigForm configForm) {
        configEntity.setName(configForm.getName());
        configEntity.setDescription(configForm.getDescription());
        configEntity.setValueType(ValueTypeEnum.valueOf(configForm.getValueType()));
        configEntity.setDefaultValue(configForm.getDefaultValue());
        configEntity.setApplicationCode(configForm.getApplicationCode());
        configEntity.setGroupId(configEntity.getGroupId());
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
        configForm.setValueType(configEntity.getValueType().name());
        configForm.setValue(value);
        configForm.setDefaultValue(configEntity.getDefaultValue());
        configForm.setApplicationCode(configEntity.getApplicationCode());
        return configForm;
    }
}

