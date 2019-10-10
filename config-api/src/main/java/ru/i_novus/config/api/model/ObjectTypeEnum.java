package ru.i_novus.config.api.model;

import lombok.Getter;

/**
 * Тип объекта
 */
@Getter
public enum ObjectTypeEnum {
    CONFIG("Настройка"),
    CONFIG_GROUP("Группа настроек"),
    APPLICATION_CONFIG("Настройка приложения");

    private String title;

    ObjectTypeEnum(String title) {
        this.title = title;
    }
}
