package ru.i_novus.system_application.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@ApiModel("Прикладная система")
public class SystemResponse {

    @NotBlank(message = "Отсутствует код системы")
    @ApiModelProperty("Код системы")
    private String code;

    @ApiModelProperty("Наименование системы")
    private String name;

    @ApiModelProperty("Описание системы")
    private String description;

    @ApiModelProperty("Приложения системы")
    private List<ApplicationRequest> applications;
}
