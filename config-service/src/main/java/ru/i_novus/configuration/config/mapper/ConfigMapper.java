package ru.i_novus.configuration.config.mapper;

import ru.i_novus.config.api.model.*;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.entity.GroupEntity;

import static org.springframework.util.StringUtils.hasText;

public class ConfigMapper {

    public static ConfigEntity toConfigEntity(ConfigForm configForm, GroupEntity group) {
        ConfigEntity configEntity = new ConfigEntity();
        configEntity.setCode(configForm.getCode());
        return toConfigEntity(configEntity, configForm, group);
    }

    public static ConfigEntity toConfigEntity(ConfigEntity configEntity, ConfigForm configForm, GroupEntity group) {
        configEntity.setName(configForm.getName());
        configEntity.setDescription(configForm.getDescription());
        configEntity.setValueType(configForm.getValueType());
        configEntity.setDefaultValue(configForm.getDefaultValue());
        configEntity.setApplicationCode(configForm.getApplicationCode());
        configEntity.setGroup(group);
        return configEntity;
    }

    public static ConfigResponse toConfigResponse(ConfigEntity configEntity, ApplicationResponse application, GroupForm group, ConfigTypeResponse type) {
        ConfigResponse configResponse = new ConfigResponse();
        configResponse.setCode(configEntity.getCode());
        configResponse.setName(configEntity.getName());
        configResponse.setDescription(configEntity.getDescription());
        configResponse.setValueType(type);
        configResponse.setDefaultValue(configEntity.getDefaultValue());
        configResponse.setApplication(application);
        configResponse.setGroup(group);
        configResponse.setIsGeneralSystemSetting(hasText(configEntity.getApplicationCode()) ? Boolean.FALSE : Boolean.TRUE);
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

