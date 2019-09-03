package ru.i_novus.config.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.i_novus.config.api.model.SystemForm;

import java.util.List;

/**
 * Прикладная система
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class System {

    /**
     * Код системы
     */
    private String code;

    /**
     * Наименование системы
     */
    private String name;

    /**
     * Описание системы
     */
    private String description;

    /**
     * Приложения системы
     */
    private List<SimpleApplication> applications;


    public SystemForm toSystemForm() {
        return new SystemForm(code, name, description);
    }
}
