package ru.i_novus.config.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Тип значения настройки
 */
@AllArgsConstructor
@Getter
public enum ValueTypeEnum {
    NUMBER,
    STRING,
    BOOLEAN
}
