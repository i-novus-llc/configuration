package ru.i_novus.config.service.entity;


import lombok.Data;
import ru.i_novus.config.api.model.ValueTypeEnum;

import javax.persistence.*;

/**
 * Сущность Метаданные настройки
 */
@Data
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

    /**
     * Значение по умолчанию
     */
    @Column(name = "default_value")
    private String defaultValue;

    /**
     * Значения справочника
     */
    @Column(name = "ref_book_value")
    private String refBookValue;
}
