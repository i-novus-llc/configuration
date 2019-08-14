package ru.i_novus.configuration_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.configuration_api.service.ConfigurationValueService;

@Controller
public class ConfigurationValueServiceConsulImpl implements ConfigurationValueService {

    @Value("${consul.url}")
    private String url;

    private RestTemplate restTemplate = new RestTemplate();


    @Override
    public String getValue(String serviceCode, String code) {
        return restTemplate.getForObject(getFullUrl(serviceCode, code) + "?raw=1", String.class);
    }

    @Override
    public void saveValue(String serviceCode, String code, String value) {
        restTemplate.put(getFullUrl(serviceCode, code), value);
    }

    @Override
    public void deleteValue(String serviceCode, String code) {
        restTemplate.delete(getFullUrl(serviceCode, code));
    }

    private String getFullUrl(String serviceCode, String code) {
        return url + serviceCode + "/" + code.replace(".", "/");
    }
}