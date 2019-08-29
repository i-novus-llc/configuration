package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("Группа со всеми ее настройками")
public class GroupedConfigForm {

    @ApiModelProperty("Идентификатор группы")
    private Integer id;

    @NotBlank(message = "Имя не должно быть пустым")
    @ApiModelProperty("Наименование группы")
    private String name;

    @ApiModelProperty("Описание группы")
    private String description;

    @ApiModelProperty("Приоритет группы")
    private Integer priority;

    @ApiModelProperty("Настройки, принадлежащие группе")
    private List<ConfigRequest> configs;
}
