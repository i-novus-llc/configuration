package ru.i_novus.config.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
     * Прикладная система
     */
    private System system;
}
