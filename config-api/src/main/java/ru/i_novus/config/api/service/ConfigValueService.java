package ru.i_novus.config.api.service;

import org.springframework.stereotype.Service;

@Service
public interface ConfigValueService {

    String getValue(String serviceCode, String code);

    void saveValue(String serviceCode, String code, String value);

    void deleteValue(String serviceCode, String code);
}
