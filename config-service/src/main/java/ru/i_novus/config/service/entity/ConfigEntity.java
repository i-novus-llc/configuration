package ru.i_novus.config.service.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.config.api.model.*;

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


    public ConfigEntity(ConfigRequest configRequest) {
        this.code = configRequest.getCode();
        this.applicationCode = configRequest.getApplicationCode();
        this.name = configRequest.getName();
        this.description = configRequest.getDescription();
        setValueType(configRequest.getValueType());
    }

    public void setValueType(String valueType) {
        this.valueType = Objects.requireNonNullElse(ValueTypeEnum.getValueType(valueType), ValueTypeEnum.STRING);
    }

    public ConfigResponse toConfigResponse(String value, ApplicationForm application, GroupForm group) {
        ConfigResponse configResponse = new ConfigResponse();
        configResponse.setCode(this.code);
        configResponse.setName(this.name);
        configResponse.setDescription(this.description);
        configResponse.setValueType(Objects.requireNonNullElse(this.valueType, ValueTypeEnum.STRING).getTitle());
        configResponse.setValue(value);
        configResponse.setApplication(application);
        configResponse.setGroup(group);
        return configResponse;
    }
}
