package ru.i_novus.configuration.system_application.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Сущность Система
 */
@Getter
@Setter
@Entity
@Table(name = "system", schema = "rdm")
public class SystemEntity implements Serializable {

    /**
     * Идентификатор системы
     */
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Признак удаления
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    /**
     * Код системы
     */
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

    /**
     * Список приложений, принадлежащих системе
     */
    @OneToMany(mappedBy = "system", fetch = FetchType.EAGER)
    private List<ApplicationEntity> applications;
}
