package ru.i_novus.config.service.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.GroupForm;

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
     * Код приложения
     */
    @Column(name = "application_code", nullable = false)
    private String applicationCode;

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
        this.applicationCode = configForm.getApplicationCode();
        this.name = configForm.getName();
        this.description = configForm.getDescription();
        setValueType(configForm.getValueType());
    }

    public void setValueType(String valueType) {
        this.valueType = Objects.requireNonNullElse(ValueTypeEnum.getValueType(valueType), ValueTypeEnum.STRING);
    }

    public ConfigForm toConfigForm(String value, String systemName, GroupForm group) {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode(this.code);
        configForm.setName(this.name);
        configForm.setDescription(this.description);
        configForm.setValueType(Objects.requireNonNullElse(this.valueType, ValueTypeEnum.STRING).getTitle());
        configForm.setValue(value);
        configForm.setApplicationCode(this.applicationCode);
        configForm.setSystemName(systemName);
        configForm.setGroup(group);
        return configForm;
    }
}
