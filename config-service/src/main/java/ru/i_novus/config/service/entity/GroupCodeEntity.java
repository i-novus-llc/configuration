package ru.i_novus.config.service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


/**
 * Сущность Код группы настроек
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
