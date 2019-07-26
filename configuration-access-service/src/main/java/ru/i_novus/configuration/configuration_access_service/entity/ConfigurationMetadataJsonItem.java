package ru.i_novus.configuration.configuration_access_service.entity;


import lombok.Data;

import java.util.Objects;

/**
 * Сущность Метаданные настройки, используемые при получении/отправки json
 */
@Data
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

    /**
     * Преобразование объекта типа ConfigurationMetadataEntity к типу ConfigurationMetadataJsonItem
     * @param configurationMetadataEntity Экземпляр ConfigurationMetadataEntity
     * @return Экземпляр ConfigurationMetadataJsonItem
     */
    public static ConfigurationMetadataJsonItem configurationEntityToConfigurationItem(ConfigurationMetadataEntity configurationMetadataEntity) {
        ConfigurationMetadataJsonItem configurationMetadataJsonItem = new ConfigurationMetadataJsonItem();
        configurationMetadataJsonItem.setCode(configurationMetadataEntity.getCode());
        configurationMetadataJsonItem.setName(configurationMetadataEntity.getName());
        configurationMetadataJsonItem.setDescription(configurationMetadataEntity.getDescription());
        configurationMetadataJsonItem.setValueType(configurationMetadataEntity.getValueType().getTitle());
        configurationMetadataJsonItem.setGroupCode(configurationMetadataEntity.getGroup().getCode());
        configurationMetadataJsonItem.setSystemCode(configurationMetadataEntity.getSystem().getCode());
        return configurationMetadataJsonItem;
    }

    /**
     * Преобразование объекта типа ConfigurationMetadataJsonItem к типу ConfigurationMetadataEntity
     * @param configurationMetadataJsonItem Экземпляр ConfigurationMetadataJsonItem
     * @param configurationGroupEntity Группа, к которой принадлежит настройка
     * @param configurationSystemEntity Прикладная система, к которой относится настрока
     * @return Экземпляр ConfigurationMetadataEntity
     */
    public static ConfigurationMetadataEntity configurationItemToConfigurationEntity(ConfigurationMetadataJsonItem configurationMetadataJsonItem,
                                                                                     ConfigurationGroupEntity configurationGroupEntity,
                                                                                     ConfigurationSystemEntity configurationSystemEntity) {
        ConfigurationMetadataEntity configurationMetadataEntity = new ConfigurationMetadataEntity();
        configurationMetadataEntity.setCode(configurationMetadataJsonItem.getCode());
        configurationMetadataEntity.setName(configurationMetadataJsonItem.getName());
        configurationMetadataEntity.setDescription(configurationMetadataJsonItem.getDescription());

        String valueType = configurationMetadataJsonItem.getValueType();
        ConfigurationValueType configurationValueType = ConfigurationValueType.getConfigurationValueType(valueType);
        configurationMetadataEntity.setValueType(Objects.requireNonNullElse(configurationValueType, ConfigurationValueType.STRING));

        configurationMetadataEntity.setGroup(configurationGroupEntity);
        configurationMetadataEntity.setSystem(configurationSystemEntity);
        return configurationMetadataEntity;
    }
}
