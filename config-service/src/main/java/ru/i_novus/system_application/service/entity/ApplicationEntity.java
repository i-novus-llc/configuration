package ru.i_novus.system_application.service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.system_application.api.model.ApplicationRequest;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.model.SystemRequest;

import javax.persistence.*;

/**
 * Сущность Приложение
 */
@NoArgsConstructor
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


    public ApplicationResponse toApplicationResponse() {
        SystemRequest systemRequest = (system == null) ? null : system.toSystemRequest();
        return new ApplicationResponse(code, name, systemRequest);
    }

    public ApplicationRequest toApplicationRequest() {
        return new ApplicationRequest(code, name, system.getCode());
    }
}
