package ru.i_novus.system_application.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("Система")
public class SimpleSystemResponse {

    @NotBlank(message = "Отсутствует код системы")
    @ApiModelProperty("Код системы")
    private String code;

    @ApiModelProperty("Наименование системы")
    private String name;

    @ApiModelProperty("Описание системы")
    private String description;
}
