package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("Приложение")
public class ApplicationResponse {

    @ApiModelProperty("Код приложения")
    private String code;

    @ApiModelProperty("Наименование приложения")
    private String name;
}
