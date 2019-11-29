package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@ApiModel("Группа со всеми ее настройками, принадлежащими некоторому приложению")
public class GroupedApplicationConfig {

    @ApiModelProperty("Идентификатор группы")
    private Integer id;

    @NotBlank(message = "Имя не должно быть пустым")
    @ApiModelProperty("Наименование группы")
    private String name;

    @ApiModelProperty("Настройки, принадлежащие группе")
    private List<ConfigForm> configs;
}
