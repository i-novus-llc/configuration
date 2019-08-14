package ru.i_novus.configuration_service.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.configuration_api.items.ConfigurationResponseItem;

import javax.persistence.*;
import java.util.Objects;

/**
 * Сущность Метаданные настройки
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "metadata", schema = "configuration")
public class MetadataEntity {

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
    private ValueTypeEnum valueType;


    public MetadataEntity(ConfigurationResponseItem responseItem) {
        this.code = responseItem.getCode();
        this.serviceCode = responseItem.getServiceCode();
        this.name = responseItem.getName();
        this.description = responseItem.getDescription();

        String valueType = responseItem.getValueType();
        ValueTypeEnum valueTypeEnum = ValueTypeEnum.getValueType(valueType);
        this.valueType = Objects.requireNonNullElse(valueTypeEnum, ValueTypeEnum.STRING);
    }

    public ConfigurationResponseItem toItem(String value, String systemName, String groupName) {
        ConfigurationResponseItem item = new ConfigurationResponseItem();
        item.setCode(this.code);
        item.setName(this.name);
        item.setDescription(this.description);
        item.setValueType(Objects.requireNonNullElse(this.valueType, ValueTypeEnum.STRING).getTitle());
        item.setValue(value);
        item.setServiceCode(this.serviceCode);
        item.setSystemName(systemName);
        item.setGroupName(groupName);
        return item;
    }
}
