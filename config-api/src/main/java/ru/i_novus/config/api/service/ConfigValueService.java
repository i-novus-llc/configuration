package ru.i_novus.config.api.service;

import java.util.Map;

public interface ConfigValueService {

    String getValue(String appCode, String code);

    Map<String, String> getKeyValueListByApplicationCode(String appCode);

    void saveValue(String appCode, String code, String value);

    void saveAllValues(String appCode, Map<String, String> data);

    void deleteValue(String appCode, String code);
}
