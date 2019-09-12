package ru.i_novus.config.api.service;

import java.util.Map;

public interface ConfigValueService {

    String getValue(String appName, String code);

    Map<String, String> getKeyValueListByApplicationCode(String appName);

    void saveValue(String appName, String code, String value);

    void saveAllValues(String appName, Map<String, String> data);

    void deleteValue(String appName, String code);
}
