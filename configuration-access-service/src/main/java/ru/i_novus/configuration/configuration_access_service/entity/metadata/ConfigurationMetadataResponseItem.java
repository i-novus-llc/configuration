package ru.i_novus.configuration.configuration_access_service.entity.metadata;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.configuration.configuration_access_service.entity.system.ConfigurationSystemEntity;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Data
@ApiModel("Метаданные настройки")
public class ConfigurationMetadataResponseItem {

    @NotBlank(message = "Код не должен быть пустым")
    @ApiModelProperty("Код настройки")
    private String code;

    @NotBlank(message = "Имя не должно быть пустым")
    @ApiModelProperty("Имя настройки")
    private String name;

    @ApiModelProperty("Описание настройки")
    private String description;

    @ApiModelProperty("Тип значения настройки")
    private String valueType;

    @ApiModelProperty("Код прикладной системы, к которой относится настройка")
    private String systemCode;


    public ConfigurationMetadataResponseItem(ConfigurationMetadataEntity configurationMetadataEntity) {
        this.setCode(configurationMetadataEntity.getCode());
        this.setName(configurationMetadataEntity.getName());
        this.setDescription(configurationMetadataEntity.getDescription());
        this.setValueType(configurationMetadataEntity.getValueType().getTitle());

        ConfigurationSystemEntity configurationSystem = configurationMetadataEntity.getSystem();
        if (configurationSystem != null) {
            this.setSystemCode(configurationMetadataEntity.getSystem().getCode());
        }
    }
}
