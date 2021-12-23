package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("Значение настройки")
public class ConfigValue {
    private String value;
}
