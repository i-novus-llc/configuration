package ru.i_novus.configuration.system_application.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Сущность Система
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "system", schema = "rdm")
public class SystemEntity implements Serializable {

    /**
     * Код системы
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


    public SystemEntity(String code) {
        this.code = code;
    }
}
