package ru.i_novus.configuration.configuration_access_service.entity.metadata;


import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.configuration.configuration_access_service.entity.system.ConfigurationSystemEntity;

import javax.persistence.*;
import java.util.Objects;

/**
 * Сущность Метаданные настройки
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "metadata", schema = "configuration")
public class ConfigurationMetadataEntity {

    /**
     * Идентификатор настройки
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Код настройки
     */
    @Column(name = "code", unique = true, nullable = false)
    private String code;

    /**
     * Имя настройки
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Описание настройки
     */
    @Column(name = "description")
    private String description;

    /**
     * Тип значения настройки
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", nullable = false)
    private ConfigurationValueTypeEnum valueType;

    /**
     * Прикладная система, к которой относится настройка
     */
    @ManyToOne
    @JoinColumn(name = "system_id", referencedColumnName = "id")
    private ConfigurationSystemEntity system;


    /**
     * Установка атрибутов текущего экземпляра метаданных
     * @param configurationMetadataResponseItem Полученные метаданные
     * @param configurationSystemEntity Прикладная система настройки
     */
    public void setAttributes(ConfigurationMetadataResponseItem configurationMetadataResponseItem,
                              ConfigurationSystemEntity configurationSystemEntity) {
        this.code = configurationMetadataResponseItem.getCode();
        this.name = configurationMetadataResponseItem.getName();
        this.description = configurationMetadataResponseItem.getDescription();

        String valueType = configurationMetadataResponseItem.getValueType();
        ConfigurationValueTypeEnum configurationValueType = ConfigurationValueTypeEnum.getConfigurationValueType(valueType);
        this.valueType = Objects.requireNonNullElse(configurationValueType, ConfigurationValueTypeEnum.STRING);

        this.system = configurationSystemEntity;
    }
}
