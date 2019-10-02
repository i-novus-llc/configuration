package ru.i_novus.system_application.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("Простая версия приложения")
public class SimpleApplicationResponse {

    @ApiModelProperty("Код приложения")
    private String code;

    @ApiModelProperty("Наименование приложения")
    private String name;

    @ApiModelProperty("Код прикладной системы")
    private String systemCode;
}
