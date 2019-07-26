package ru.i_novus.configuration.configuration_access_service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Сущность Группа настроек
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "group", schema = "configuration")
public class ConfigurationGroupEntity {

    /**
     * Идентификатор группы
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Код группы
     */
    @Column(name = "code", unique = true, nullable = false)
    private String code;

    /**
     * Наименование группы
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Описание группы
     */
    @Column(name = "description")
    private String description;
}
