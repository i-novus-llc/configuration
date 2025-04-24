package ru.i_novus.config.api.service;

import ru.i_novus.config.api.model.enums.ValueTypeEnum;

/**
 * Сервис валидации значений настроек
 */
public interface ConfigValidationService {

    /**
     * Проверяет значение настройки
     *
     * @param value     Значение настройки
     * @param valueType Тип настройки
     */
    void validateConfigValue(String value, ValueTypeEnum valueType);

}
