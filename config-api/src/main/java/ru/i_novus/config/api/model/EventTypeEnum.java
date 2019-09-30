package ru.i_novus.config.api.model;

import lombok.Getter;

/**
 * Тип события
 */
@Getter
public enum EventTypeEnum {
    CONFIG_CREATE("Создание настройки"),
    CONFIG_UPDATE("Изменение параметров настройки"),
    CONFIG_DELETE("Удаление настройки"),
    CONFIG_GROUP_CREATE("Создание группы настроек"),
    CONFIG_GROUP_UPDATE("Изменение группы настроек"),
    CONFIG_GROUP_DELETE("Удаление группы настроек"),
    APPLICATION_CONFIG_CREATE("Создание настроек приложения"),
    APPLICATION_CONFIG_UPDATE("Изменение настроек приложения"),
    APPLICATION_CONFIG_DELETE("Удаление настроек приложения");

    private String title;

    EventTypeEnum(String title) {
        this.title = title;
    }
}
