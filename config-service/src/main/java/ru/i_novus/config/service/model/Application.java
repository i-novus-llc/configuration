package ru.i_novus.config.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.i_novus.config.api.model.ApplicationForm;

/**
 * Приложение прикладной системы
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    /**
     * Код приложения
     */
    private String code;

    /**
     * Наименование приложения
     */
    private String name;

    /**
     * Прикладная система, к которой относится приложение
     */
    private SimpleSystem system;


    public ApplicationForm toApplicationForm() {
        return new ApplicationForm(code, name, system.toSystemForm());
    }
}
