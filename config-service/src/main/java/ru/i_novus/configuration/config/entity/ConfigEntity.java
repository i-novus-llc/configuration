package ru.i_novus.configuration.config.entity;


import lombok.Data;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;

import jakarta.persistence.*;

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
     * Приложение
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "application_code")
    private ApplicationEntity application;

    /**
     * Группа
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "group_id")
    private GroupEntity group;

}
