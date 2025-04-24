package ru.i_novus.configuration.config.validators.value;

import ru.i_novus.config.api.model.enums.ValueTypeEnum;

/**
 * Валидатор значений настроек
 */
public interface ConfigValueValidator {

    /**
     * Возвращает тип значения настройки
     *
     * @return Тип значения настройки
     */
    ValueTypeEnum getType();

    /**
     * Проверяет значение настройки
     *
     * @param value Значение настройки
     * @return Если валидация прошла успешно, то {@code true}, иначе {@code false}
     */
    boolean validate(String value);

}
