package ru.i_novus.configuration.configuration_access_service.entity.system;

import lombok.Data;

import javax.persistence.*;

/**
 * Сущность Прикладная система настроек
 */
@Data
@Entity
@Table(name = "system", schema = "configuration")
public class ConfigurationSystemEntity {

    /**
     * Идентификатор системы
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Код системы
     */
    @Column(name = "code", unique = true, nullable = false)
    private String code;

    /**
     * Наименование системы
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Описание системы
     */
    @Column(name = "description")
    private String description;
}
