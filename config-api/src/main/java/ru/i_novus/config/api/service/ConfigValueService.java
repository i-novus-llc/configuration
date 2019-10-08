package ru.i_novus.config.api.service;

import java.util.Map;

/**
 * Сервис для работы со значениями настроек по коду приложения
 */
public interface ConfigValueService {

    String getValue(String appCode, String code);

    Map<String, String> getKeyValueList(String appCode);

    void saveValue(String appCode, String code, String value);

    void saveAllValues(String appCode, Map<String, String> updatedData, Map<String, String> deletedData);

    void deleteValue(String appCode, String code);

    void deleteAllValues(String appCode);
}
