package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("Тип настройки")
public class ConfigTypeResponse {

    @ApiModelProperty("Идентификатор")
    private String id;

    @ApiModelProperty("Наименование")
    private String name;
}
