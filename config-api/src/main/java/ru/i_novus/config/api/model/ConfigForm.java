package ru.i_novus.config.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@ApiModel("Настройка")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigForm {

    @NotBlank(message = "Отсутствует код настройки")
    @ApiModelProperty("Код настройки")
    private String code;

    @ApiModelProperty("Наименование настройки")
    private String name;

    @ApiModelProperty("Описание настройки")
    private String description;

    @ApiModelProperty("Тип значения настройки")
    private String valueType;

    @ApiModelProperty("Значение настройки")
    private String value;

    @ApiModelProperty("Значение по умолчанию")
    private String defaultValue;

    @ApiModelProperty("Код приложения")
    private String applicationCode;

    @ApiModelProperty("Идентификатор группы настроек")
    private Integer groupId;
}
