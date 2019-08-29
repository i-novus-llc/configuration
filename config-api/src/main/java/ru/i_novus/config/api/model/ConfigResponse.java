package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Data
@ApiModel("Настройка")
public class ConfigResponse {

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

    @ApiModelProperty("Прикладная система, к которой относится настройка")
    private SystemForm system;

    @ApiModelProperty("Группа, к которой принадлежит настройка")
    private GroupForm group;
}
