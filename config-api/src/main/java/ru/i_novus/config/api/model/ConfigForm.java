package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Data
@ApiModel("Настройка")
public class ConfigForm {

    @NotBlank(message = "Отсутствует код настройки")
    @ApiModelProperty("Код настройки")
    private String code;

    @ApiModelProperty("Наименование настройки")
    private String name;

    @ApiModelProperty("Описание настройки")
    private String description;

    @NotBlank(message = "Отсутствует тип значения настройки")
    @ApiModelProperty("Тип значения настройки")
    private String valueType;

    @NotBlank(message = "Отсутствует значение настройки")
    @ApiModelProperty("Значение настройки")
    private String value;

    @ApiModelProperty("Код приложения")
    private String applicationCode;

    @NotBlank(message = "Отсутствует наименование прикладной системы настройки")
    @ApiModelProperty("Наименование прикладной системы, к которой относится настройка")
    private String systemName;

    @ApiModelProperty("Группа, к которой принадлежит настройка")
    private GroupForm group;
}
