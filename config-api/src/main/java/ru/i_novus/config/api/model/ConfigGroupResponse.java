package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("Группа настроек")
public class ConfigGroupResponse {

    @ApiModelProperty("Идентификатор группы")
    private Integer id;

    @ApiModelProperty("Наименование группы")
    private String name;

    @ApiModelProperty("Настройки")
    private List<ApplicationConfigResponse> configs;
}
