package ru.i_novus.system_application.service.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Сущность Приложение
 */
@Getter
@Setter
@Entity
@Table(name = "application", schema = "rdm")
public class ApplicationEntity implements Serializable {

    /**
     * Идентификатор приложения
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
     * Код приложения
     */
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Наименование приложения
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Прикладная система
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "system_code", referencedColumnName = "code")
    private SystemEntity system;
}
