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
public enum ConfigurationValueType {
    NUMBER("Числовой"),
    STRING("Строковый"),
    DATE("Дата");

    /**
     * Конкретное обозначение для каждого типа
     */
    private String title;

    /**
     * Словарь для поиска ConfigurationValueType по его обозначению
     */
    private static final Map<String, ConfigurationValueType> titles = new HashMap<>();

    static {
        for (ConfigurationValueType t : ConfigurationValueType.values()) {
            titles.put(t.getTitle(), t);
        }
    }

    public static ConfigurationValueType getConfigurationValueType(String title) {
        return titles.get(title);
    }
}
