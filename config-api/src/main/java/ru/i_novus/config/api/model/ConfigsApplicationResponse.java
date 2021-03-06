package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("Настройки приложений")
public class ConfigsApplicationResponse {

    @ApiModelProperty("Код приложения")
    private String code;

    @ApiModelProperty("Наименование приложения")
    private String name;

    @ApiModelProperty("Группы настроек приложения")
    private List<ConfigGroupResponse> groups;
}
