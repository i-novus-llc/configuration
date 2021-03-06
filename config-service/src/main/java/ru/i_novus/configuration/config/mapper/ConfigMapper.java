package ru.i_novus.configuration.config.mapper;

import ru.i_novus.config.api.model.*;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;
import ru.i_novus.configuration.config.entity.ConfigEntity;

public class ConfigMapper {

    public static ConfigEntity toConfigEntity(ConfigForm configForm) {
        return toConfigEntity(new ConfigEntity(), configForm);
    }

    public static ConfigEntity toConfigEntity(ConfigEntity entity, ConfigForm configForm) {
        entity.setCode(configForm.getCode());
        entity.setName(configForm.getName());
        entity.setDescription(configForm.getDescription());
        entity.setValueType(ValueTypeEnum.valueOf(configForm.getValueType()));
        entity.setDefaultValue(configForm.getDefaultValue());
        return entity;
    }

    public static ConfigResponse toConfigResponse(ConfigEntity configEntity, GroupForm group) {
        ConfigResponse configResponse = new ConfigResponse();
        configResponse.setCode(configEntity.getCode());
        configResponse.setName(configEntity.getName());
        configResponse.setDescription(configEntity.getDescription());
        configResponse.setValueType(configEntity.getValueType());
        configResponse.setDefaultValue(configEntity.getDefaultValue());
        configResponse.setApplication(ApplicationMapper
                .toApplicationResponse(configEntity.getApplication()));
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
        if (configEntity.getApplication() != null) {
            configForm.setApplicationCode(configEntity.getApplication().getCode());
        }
        return configForm;
    }
}

