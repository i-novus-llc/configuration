package ru.i_novus.configuration.configuration_access_service.items;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationGroupEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

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


    public ConfigurationGroupResponseItem(ConfigurationGroupEntity entity, List<String> codes) {
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.codes = codes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationGroupResponseItem that = (ConfigurationGroupResponseItem) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                codes.size() == that.codes.size() &&
                new HashSet(codes).equals(new HashSet(that.codes));
    }
}
