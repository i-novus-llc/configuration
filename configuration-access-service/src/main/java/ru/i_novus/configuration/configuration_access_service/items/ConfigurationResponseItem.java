package ru.i_novus.configuration.configuration_access_service.items;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationMetadataEntity;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationValueTypeEnum;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@NoArgsConstructor
@Data
@ApiModel("Настройка")
public class ConfigurationResponseItem {

    @NotBlank(message = "Отсутствует код настройки")
    @ApiModelProperty("Код настройки")
    private String code;

    @ApiModelProperty("Наименование настройки")
    private String name;

    @ApiModelProperty("Описание настройки")
    private String description;

    @NotBlank(message = "Отсутствует тип значения настройки")
    @ApiModelProperty("Тип значения настройки")
    private String valueType;

    @NotBlank(message = "Отсутствует значение настройки")
    @ApiModelProperty("Значение настройки")
    private String value;

    @ApiModelProperty("Наименование службы")
    private String serviceCode;

    @NotBlank(message = "Отсутствует наименование прикладной системы настройки")
    @ApiModelProperty("Наименование прикладной системы, к которой относится настройка")
    private String systemName;

    @NotBlank(message = "Отсутствует наименование группы настройки")
    @ApiModelProperty("Наименование группы, к которой принадлежит настройка")
    private String groupName;

    public ConfigurationResponseItem(ConfigurationMetadataEntity entity,
                                     String value, String systemName, String groupName) {
        this.code = entity.getCode();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.valueType = Objects.requireNonNullElse(entity.getValueType(), ConfigurationValueTypeEnum.STRING).getTitle();
        this.value = value;
        this.serviceCode = entity.getServiceCode();
        this.systemName = systemName;
        this.groupName = groupName;
    }
}
