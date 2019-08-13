package ru.i_novus.configuration.configuration_access_service.service.value_receiving;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

@Controller
public class ConfigurationValueServiceConsulImpl implements ConfigurationValueService {

    @Value("${consul.url}")
    private String url;

    private RestTemplate restTemplate = new RestTemplate();


    @Override
    public String getConfigurationValue(String serviceCode, String code) {
        return restTemplate.getForObject(getFullUrl(serviceCode, code) + "?raw=1", String.class);
    }

    @Override
    public void saveConfigurationValue(String serviceCode, String code, String value) {
        restTemplate.put(getFullUrl(serviceCode, code), value);
    }

    @Override
    public void deleteConfigurationValue(String serviceCode, String code) {
        restTemplate.delete(getFullUrl(serviceCode, code));
    }

    private String getFullUrl(String serviceCode, String code) {
        return url + serviceCode + "/" + code.replace(".", "/");
    }
}