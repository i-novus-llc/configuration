package ru.i_novus.configuration.configuration_access_service.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 * Сущность Настройка
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "configuration", schema = "scs")
public class ConfigurationEntity {

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
    @Column(name = "code", nullable = false)
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
    @Column(name="value_type", nullable = false)
    private String valueType;

    /**
     * Идентификатор группы, к которой принадлежит настройка
     */
    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private ConfigurationGroupEntity group;

    /**
     * Идентификатор прикладной системы, к которой относится настройка
     */
    @ManyToOne
    @JoinColumn(name = "system_id", referencedColumnName = "id")
    private ConfigurationSystemEntity system;
}
