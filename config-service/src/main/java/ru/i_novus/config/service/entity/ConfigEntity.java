package ru.i_novus.config.service.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.config.api.items.ConfigForm;

import javax.persistence.*;
import java.util.Objects;

/**
 * Сущность Метаданные настройки
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "config", schema = "configuration")
public class ConfigEntity {

    /**
     * Код настройки
     */
    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = "code", nullable = false)
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


    public ConfigEntity(ConfigForm configForm) {
        this.code = configForm.getCode();
        this.serviceCode = configForm.getServiceCode();
        this.name = configForm.getName();
        this.description = configForm.getDescription();

        String valueType = configForm.getValueType();
        ValueTypeEnum valueTypeEnum = ValueTypeEnum.getValueType(valueType);
        this.valueType = Objects.requireNonNullElse(valueTypeEnum, ValueTypeEnum.STRING);
    }

    public ConfigForm toConfigForm(String value, String systemName, String groupName) {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode(this.code);
        configForm.setName(this.name);
        configForm.setDescription(this.description);
        configForm.setValueType(Objects.requireNonNullElse(this.valueType, ValueTypeEnum.STRING).getTitle());
        configForm.setValue(value);
        configForm.setServiceCode(this.serviceCode);
        configForm.setSystemName(systemName);
        configForm.setGroupName(groupName);
        return configForm;
    }
}
