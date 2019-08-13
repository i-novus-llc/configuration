package ru.i_novus.configuration.configuration_access_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Тип значения настройки
 */
@AllArgsConstructor
@Getter
public enum ConfigurationValueTypeEnum {
    NUMBER("Число"),
    STRING("Строка"),
    BOOLEAN("Чекбокс");

    /**
     * Конкретное наименование для каждого типа
     */
    private String title;

    /**
     * Словарь для поиска ConfigurationValueTypeEnum по его наименованию
     */
    private static final Map<String, ConfigurationValueTypeEnum> titles = new HashMap<>();

    static {
        for (ConfigurationValueTypeEnum t : ConfigurationValueTypeEnum.values()) {
            titles.put(t.getTitle(), t);
        }
    }

    public static ConfigurationValueTypeEnum getConfigurationValueType(String title) {
        return titles.get(title);
    }
}
