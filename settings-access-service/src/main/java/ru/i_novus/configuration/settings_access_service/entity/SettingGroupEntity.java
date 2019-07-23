package ru.i_novus.configuration.settings_access_service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Сущность Группа настроек
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "setting_group", schema = "scs")
public class SettingGroupEntity {

    /**
     * Идентификатор группы
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

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
