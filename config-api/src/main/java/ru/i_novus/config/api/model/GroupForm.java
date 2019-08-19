package ru.i_novus.config.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("Группа настройки")
public class GroupForm {

    @NotBlank(message = "Имя не должно быть пустым")
    @ApiModelProperty("Наименование группы")
    private String name;

    @ApiModelProperty("Описание группы")
    private String description;

    @NotEmpty(message = "Группа настройки должна иметь один или более кодов")
    @ApiModelProperty("Коды группы")
    private List<String> codes;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupForm that = (GroupForm) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                codes.size() == that.codes.size() &&
                new HashSet(codes).equals(new HashSet(that.codes));
    }
}
