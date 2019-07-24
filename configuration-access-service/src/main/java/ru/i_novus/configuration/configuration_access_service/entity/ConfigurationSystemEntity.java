package ru.i_novus.configuration.configuration_access_service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Сущность Прикладная система настроек
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "configuration_system", schema = "scs")
public class ConfigurationSystemEntity {

    /**
     * Идентификатор системы
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

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
