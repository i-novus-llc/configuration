package ru.i_novus.config.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Тип значения настройки
 */
@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ValueTypeEnum {
    NUMBER("Число"),
    STRING("Строка"),
    BOOLEAN("Чекбокс");

    private String name;

    public String getId() {
        return name();
    }
}
