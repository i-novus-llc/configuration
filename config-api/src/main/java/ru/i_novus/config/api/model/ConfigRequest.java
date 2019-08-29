package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("Входные данные настройки")
public class ConfigRequest {

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

    @ApiModelProperty("Код приложения")
    private String applicationCode;
}
