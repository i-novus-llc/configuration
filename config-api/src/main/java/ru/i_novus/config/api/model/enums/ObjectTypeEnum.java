package ru.i_novus.config.api.model.enums;

import lombok.Getter;

/**
 * Тип объекта
 */
@Getter
public enum ObjectTypeEnum {
    CONFIG("Настройка"),
    CONFIG_GROUP("Группа настроек"),
    APPLICATION_CONFIG("Настройка приложения"),
    COMMON_SYSTEM_CONFIG("Общесистемная настройка");

    private String title;

    ObjectTypeEnum(String title) {
        this.title = title;
    }
}
