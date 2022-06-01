package ru.i_novus.configuration.config.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность Приложение
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "application", schema = "rdm")
public class ApplicationEntity implements Serializable {

    /**
     * Код приложения
     */
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Признак удаления
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    /**
     * Наименование приложения
     */
    @Column(name = "name", nullable = false)
    private String name;

    public ApplicationEntity(String code) {
        this.code = code;
    }

    public ApplicationEntity(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
