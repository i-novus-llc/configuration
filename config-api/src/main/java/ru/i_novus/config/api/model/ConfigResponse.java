package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("Выходные данные настройки")
public class ConfigResponse {

    @NotBlank(message = "Отсутствует код настройки")
    @ApiModelProperty("Код настройки")
    private String code;

    @ApiModelProperty("Наименование настройки")
    private String name;

    @ApiModelProperty("Описание настройки")
    private String description;

    @ApiModelProperty("Тип значения настройки")
    private ConfigTypeResponse valueType;

    @ApiModelProperty("Значение по умолчанию")
    private String defaultValue;

    @ApiModelProperty("Приложение")
    private ApplicationResponse application;

    @ApiModelProperty("Группа, к которой принадлежит настройка")
    private GroupForm group;

    @ApiModelProperty("Признак того, что эта настройка является общесистемной")
    private Boolean isGeneralSystemSetting;

}
