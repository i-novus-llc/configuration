package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel("Группа настроек")
public class GroupForm {

    @ApiModelProperty("Идентификатор группы")
    private Integer id;

    @NotBlank(message = "Имя не должно быть пустым")
    @ApiModelProperty("Наименование группы")
    private String name;

    @ApiModelProperty("Описание группы")
    private String description;

    @ApiModelProperty("Приоритет группы")
    private Integer priority;

    @NotEmpty(message = "Группа настройки должна иметь один или более кодов")
    @ApiModelProperty("Коды группы")
    private List<String> codes;
}
