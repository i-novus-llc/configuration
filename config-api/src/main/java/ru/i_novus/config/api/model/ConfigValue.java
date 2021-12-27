package ru.i_novus.config.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("Значение настройки")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigValue {
    private String value;
}
