package ru.i_novus.system_application.service.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Сущность Приложение
 */
@Data
@Entity
@Table(name = "application", schema = "configuration")
public class ApplicationEntity {

    /**
     * Код приложения
     */
    @Id
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
    @JoinColumn(name = "system_code")
    @ManyToOne(fetch = FetchType.EAGER)
    private SystemEntity system;
}
