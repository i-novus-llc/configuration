package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import ru.i_novus.system_application.api.model.ApplicationResponse;

import javax.validation.constraints.NotBlank;

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
    private ValueTypeEnum valueType;

    @ApiModelProperty("Приложение")
    private ApplicationResponse application;

    @ApiModelProperty("Группа, к которой принадлежит настройка")
    private GroupForm group;
}
