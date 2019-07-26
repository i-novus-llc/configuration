package ru.i_novus.configuration.configuration_access_service.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


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
    @Column(name="description")
    private String description;

    /**
     * Тип значения настройки
     */
    @Enumerated(EnumType.STRING)
    @Column(name="value_type", nullable = false)
    private ConfigurationValueType valueType;

    /**
     * Группа, к которой принадлежит настройка
     */
    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    private ConfigurationGroupEntity group;

    /**
     * Прикладная система, к которой относится настройка
     */
    @ManyToOne
    @JoinColumn(name = "system_id", referencedColumnName = "id", nullable = false)
    private ConfigurationSystemEntity system;
}
