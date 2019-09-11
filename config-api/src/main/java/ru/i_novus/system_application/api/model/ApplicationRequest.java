package ru.i_novus.system_application.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("Простая версия приложения")
public class ApplicationRequest {

    @ApiModelProperty("Код приложения")
    private String code;

    @ApiModelProperty("Наименование приложения")
    private String name;

    @ApiModelProperty("Код прикладной системы")
    private String systemCode;
}
