package ru.i_novus.configuration.config.validators.value;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;

import static ru.i_novus.configuration.config.validators.value.ConfigValueValidatorConstants.INVALID_TYPE_MSG;

/**
 * Валидатор значений настроек
 */
public interface ConfigValueValidator {

    /**
     * Возвращает тип значения настройки
     *
     * @return Тип значения настройки
     */
    @NotNull
    ValueTypeEnum getType();

    /**
     * Возвращает паттерн валидации, может быть {@code null}
     *
     * @return Паттерн
     */
    @Nullable
    default String getPattern() {
        return null;
    }

    /**
     * Проверяет значение настройки
     *
     * @param value Значение настройки
     * @return Если валидация прошла успешно, то {@code true}, иначе {@code false}
     */
    boolean validate(String value);

    /**
     * Возвращает код сообщения ошибки
     *
     * @return Код сообщения ошибки
     */
    default String getErrorMessageCode() {
        return INVALID_TYPE_MSG;
    }

    /**
     * Возвращает аргументы для сообщения об ошибки
     *
     * @param value Значение параметра
     * @return Аргументы для сообщения об ошибки
     */
    default Object[] getArgs(String value) {
        return new String[]{value, getType().getId()};
    }

}
