package ru.i_novus.system_application.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("Приложение")
public class ApplicationResponse {

    @ApiModelProperty("Код приложения")
    private String code;

    @ApiModelProperty("Наименование приложения")
    private String name;

    @ApiModelProperty("Прикладная система")
    private System system;
}
