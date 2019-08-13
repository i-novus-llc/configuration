package ru.i_novus.configuration.configuration_access_service.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.configuration.configuration_access_service.items.ConfigurationResponseItem;

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
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    /**
     * Код прикладной системы
     */
    @Column(name = "serviceCode", nullable = false)
    private String serviceCode;

    /**
     * Наименование настройки
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


    public ConfigurationMetadataEntity(ConfigurationResponseItem responseItem) {
        this.code = responseItem.getCode();
        this.serviceCode = responseItem.getServiceCode();
        this.name = responseItem.getName();
        this.description = responseItem.getDescription();

        String valueType = responseItem.getValueType();
        ConfigurationValueTypeEnum configurationValueType = ConfigurationValueTypeEnum.getConfigurationValueType(valueType);
        this.valueType = Objects.requireNonNullElse(configurationValueType, ConfigurationValueTypeEnum.STRING);
    }
}
