package ru.i_novus.system_application.service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.i_novus.system_application.api.model.SystemRequest;
import ru.i_novus.system_application.api.model.SystemResponse;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сущность Система
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "system", schema = "configuration")
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


    public SystemEntity(SystemRequest systemRequest) {
        this.code = systemRequest.getCode();
        this.name = systemRequest.getName();
        this.description = systemRequest.getDescription();
    }

    public SystemResponse toSystemResponse() {
        return new SystemResponse(code, name, description,
                applications.stream().map(ApplicationEntity::toApplicationRequest).collect(Collectors.toList()));
    }

    public SystemRequest toSystemRequest() {
        return new SystemRequest(code, name, description);
    }
}
