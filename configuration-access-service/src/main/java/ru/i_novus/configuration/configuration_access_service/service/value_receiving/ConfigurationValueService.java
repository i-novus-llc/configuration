package ru.i_novus.configuration.configuration_access_service.service.value_receiving;

import org.springframework.stereotype.Service;

@Service
public interface ConfigurationValueService {

    String getConfigurationValue(String serviceCode, String code);

    void saveConfigurationValue(String serviceCode, String code, String value);

    void deleteConfigurationValue(String serviceCode, String code);
}
