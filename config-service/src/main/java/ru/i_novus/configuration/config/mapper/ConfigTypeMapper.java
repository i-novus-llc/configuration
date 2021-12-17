package ru.i_novus.configuration.config.mapper;

import ru.i_novus.config.api.model.ConfigTypeResponse;
import ru.i_novus.config.api.model.ValueTypeEnum;

public class ConfigTypeMapper {

    public static ConfigTypeResponse toConfigResponse(ValueTypeEnum typeEnum) {
        ConfigTypeResponse configTypeResponse = new ConfigTypeResponse();
        configTypeResponse.setId(typeEnum.name());
        configTypeResponse.setName(typeEnum.getName());
        return configTypeResponse;
    }
}

