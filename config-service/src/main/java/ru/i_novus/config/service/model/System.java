package ru.i_novus.config.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
