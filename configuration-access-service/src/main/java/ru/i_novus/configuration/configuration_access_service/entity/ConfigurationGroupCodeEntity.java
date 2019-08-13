package ru.i_novus.configuration.configuration_access_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 * Сущность Код группы настроек
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_code", schema = "configuration")
public class ConfigurationGroupCodeEntity {

    /**
     * Код группы
     */
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Идентификатор группы
     */
    @Column(name = "group_id", nullable = false)
    private Integer groupId;
}
