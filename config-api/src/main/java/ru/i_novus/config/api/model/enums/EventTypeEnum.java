package ru.i_novus.config.api.model.enums;

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
    COMMON_SYSTEM_CONFIG_UPDATE("Изменение общесистемной настройки"),
    COMMON_SYSTEM_CONFIG_DELETE("Удаление общесистемной настройки"),
    APPLICATION_CONFIG_UPDATE("Изменение настройки приложения"),
    APPLICATION_CONFIG_DELETE("Удаление настройки приложения");

    private String title;

    EventTypeEnum(String title) {
        this.title = title;
    }
}
