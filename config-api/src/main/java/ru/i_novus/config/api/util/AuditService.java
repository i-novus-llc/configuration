package ru.i_novus.config.api.util;

/**
 * API сервиса аудита
 */
public interface AuditService {

    /**
     * Логирование информации о действии, произведенном над объектом
     * @param action     Наименование действия
     * @param object     Объект
     * @param objectId   Уникальный идентификатор объекта
     * @param objectName Наименование объекта
     */
    void audit(String action, Object object, String objectId, String objectName);
}
