package ru.i_novus.configuration_api.service;

import org.springframework.stereotype.Service;

@Service
public interface ConfigurationValueService {

    String getValue(String serviceCode, String code);

    void saveValue(String serviceCode, String code, String value);

    void deleteValue(String serviceCode, String code);
}
