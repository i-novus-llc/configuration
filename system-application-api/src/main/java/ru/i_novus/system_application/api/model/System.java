package ru.i_novus.system_application.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("Простая версия прикладной системы")
public class System {

    @NotBlank(message = "Отсутствует код системы")
    @ApiModelProperty("Код системы")
    private String code;

    @ApiModelProperty("Наименование системы")
    private String name;

    @ApiModelProperty("Описание системы")
    private String description;
}
