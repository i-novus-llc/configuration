package ru.i_novus.config.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Простая версия приложения прикладной системы
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleApplication {

    /**
     * Код приложения
     */
    private String code;

    /**
     * Наименование приложения
     */
    private String name;

    /**
     * Код прикладной системы, к которой относится приложение
     */
    private String systemCode;
}