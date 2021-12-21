package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("Общесистемные настройки")
public class CommonSystemConfigResponse extends ApplicationConfig {

    @ApiModelProperty("Группы общесистемных настроек")
    private List<ConfigGroupResponse> groups;
}
