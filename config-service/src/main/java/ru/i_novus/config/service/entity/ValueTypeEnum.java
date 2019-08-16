package ru.i_novus.config.service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Тип значения настройки
 */
@AllArgsConstructor
@Getter
public enum ValueTypeEnum {
    NUMBER("Число"),
    STRING("Строка"),
    BOOLEAN("Чекбокс");

    /**
     * Конкретное наименование для каждого типа
     */
    private String title;

    /**
     * Словарь для поиска ValueTypeEnum по его наименованию
     */
    private static final Map<String, ValueTypeEnum> titles = new HashMap<>();

    static {
        for (ValueTypeEnum t : ValueTypeEnum.values()) {
            titles.put(t.getTitle(), t);
        }
    }

    public static ValueTypeEnum getValueType(String title) {
        return titles.get(title);
    }
}
