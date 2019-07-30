package ru.i_novus.configuration.configuration_access_service.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность Метаданные настройки, используемые при получении/отправки json
 */
@NoArgsConstructor
@Getter
@Setter
public class ConfigurationMetadataJsonItem {
    /**
     * Код настройки
     */
    private String code;

    /**
     * Имя настройки
     */
    private String name;

    /**
     * Описание настройки
     */
    private String description;

    /**
     * Тип значения настройки
     */
    private String valueType;

    /**
     * Код группы, к которой принадлежит настройка
     */
    private String groupCode;

    /**
     * Код прикладной системы, к которой относится настройка
     */
    private String systemCode;


    public ConfigurationMetadataJsonItem(ConfigurationMetadataEntity configurationMetadataEntity) {
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
