package ru.i_novus.system_application.service.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Сущность Система
 */
@Data
@Entity
@Table(name = "system", schema = "rdm")
public class SystemEntity {

    /**
     * Код системы
     */
    @Id
    @Column(name = "code", nullable = false)
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

    @OneToMany(mappedBy = "system", fetch = FetchType.EAGER)
    private List<ApplicationEntity> applications;
}
