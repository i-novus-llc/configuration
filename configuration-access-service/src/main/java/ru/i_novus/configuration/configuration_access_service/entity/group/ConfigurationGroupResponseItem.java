package ru.i_novus.configuration.configuration_access_service.entity.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("Группа настройки")
public class ConfigurationGroupResponseItem {

    @NotBlank(message = "Имя не должно быть пустым")
    @ApiModelProperty("Наименование группы")
    private String name;

    @ApiModelProperty("Описание группы")
    private String description;

    @NotEmpty(message = "Группа настройки должна иметь один или более кодов")
    @ApiModelProperty("Коды группы")
    private List<String> codes;
}
