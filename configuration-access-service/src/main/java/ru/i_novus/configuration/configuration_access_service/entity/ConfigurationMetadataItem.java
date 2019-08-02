package ru.i_novus.configuration.configuration_access_service.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Data
@ApiModel("Метаданные настройки, используемые при получении/отправки")
public class ConfigurationMetadataItem {

    @NotBlank
    @ApiModelProperty("Код настройки")
    private String code;

    @NotBlank
    @ApiModelProperty("Имя настройки")
    private String name;

    @ApiModelProperty("Описание настройки")
    private String description;

    @ApiModelProperty("Тип значения настройки")
    private String valueType;

    @ApiModelProperty("Код группы, к которой принадлежит настройка")
    private String groupCode;

    @ApiModelProperty("Код прикладной системы, к которой относится настройка")
    private String systemCode;


    public ConfigurationMetadataItem(ConfigurationMetadataEntity configurationMetadataEntity) {
        this.setCode(configurationMetadataEntity.getCode());
        this.setName(configurationMetadataEntity.getName());
        this.setDescription(configurationMetadataEntity.getDescription());
        this.setValueType(configurationMetadataEntity.getValueType().getTitle());

        ConfigurationGroupEntity configurationGroup = configurationMetadataEntity.getGroup();
        if (configurationGroup != null) {
            this.setGroupCode(configurationMetadataEntity.getGroup().getCode());
        }
        ConfigurationSystemEntity configurationSystem = configurationMetadataEntity.getSystem();
        if (configurationSystem != null) {
            this.setSystemCode(configurationMetadataEntity.getSystem().getCode());
        }
    }
}
