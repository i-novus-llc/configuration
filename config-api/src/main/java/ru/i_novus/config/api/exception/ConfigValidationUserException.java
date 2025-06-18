package ru.i_novus.config.api.exception;

import net.n2oapp.platform.i18n.Message;
import net.n2oapp.platform.i18n.UserException;

import java.io.Serial;

/**
 * Пользовательские исключения при работе сервиса валидации значений настроек
 */
public class ConfigValidationUserException extends UserException {

    @Serial
    private static final long serialVersionUID = 505137611869691492L;

    /**
     * Конструктор
     *
     * @param code Код сообщения
     */
    public ConfigValidationUserException(String code) {
        super(new Message(code));
    }

    /**
     * Конструктор
     *
     * @param code Код сообщения
     * @param args Аргументы
     */
    public ConfigValidationUserException(String code, Object... args) {
        super(new Message(code, args));
    }

}
