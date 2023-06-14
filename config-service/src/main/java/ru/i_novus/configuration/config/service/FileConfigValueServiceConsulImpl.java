package ru.i_novus.configuration.config.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.config.api.service.ConfigValueService;

/**
 * Реализация сервиса для работы со значениями настроек, хранящихся в Consul в формате FILES
 */
@Slf4j
public class FileConfigValueServiceConsulImpl extends YamlConfigValueServiceConsulImpl implements ConfigValueService {

    @Value("${config.consul.url}")
    private String url;

    @Value("${config.consul.key-suffix}")
    private String keySuffix;

    public FileConfigValueServiceConsulImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public void deleteAllValues(String appCode) {
        restTemplate.delete(url + appCode + keySuffix);
    }

    @Override
    protected ObjectNode loadYaml(String appCode) {
        return getFromConsul(url + appCode + keySuffix);
    }

    @Override
    protected void saveYaml(String appCode, JsonNode node) {
        putToConsul(url + appCode + keySuffix, node);
    }
}