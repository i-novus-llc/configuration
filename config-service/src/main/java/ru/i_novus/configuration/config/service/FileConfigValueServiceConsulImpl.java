package ru.i_novus.configuration.config.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
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
        //todo rest
    }

    @Override
    protected ObjectNode loadYaml(String appCode) {
        try {
            String rawValue = restTemplate.getForObject(url + appCode + keySuffix + "?raw=1", String.class);

            if (StringUtils.hasText(rawValue)) {
                JsonNode node = yamlMapper.readTree(rawValue);
                return (ObjectNode) node;
            }
            return null;
        } catch (HttpClientErrorException.NotFound | JsonProcessingException e) {
            log.info(e.getMessage());
            return null;
        }
    }

    @Override
    protected void saveYaml(String appCode, JsonNode node) {
        //todo rest
    }
}