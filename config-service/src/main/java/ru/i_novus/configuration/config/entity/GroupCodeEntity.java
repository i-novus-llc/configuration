package ru.i_novus.configuration.config.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;


/**
 * Сущность Код группы настроек
 */
@Getter
@Setter
@Entity
@Table(name = "config_group_code", schema = "configuration")
public class GroupCodeEntity {

    /**
     * Код группы
     */
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Группа
     */
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;
}
