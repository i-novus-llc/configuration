package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("Настройка")
public class ApplicationConfigResponse {
    @ApiModelProperty("Код настройки")
    private String code;

    @ApiModelProperty("Наименование настройки")
    private String name;

    @ApiModelProperty("Значение настройки")
    private String value;

    @ApiModelProperty("Общесистемное значение")
    private String commonSystemValue;
}
