package ru.i_novus.configuration.config.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.config.api.service.ConfigValueService;

import java.util.HashMap;
import java.util.Map;

/**
 * Реализация сервиса для работы со значениями настроек, хранящихся в Consul в формате FILES
 */
public class FileConfigValueServiceConsulImpl extends YamlConfigValueServiceConsulImpl implements ConfigValueService {

    @Value("${config.consul.url}")
    private String url;

    @Value("${config.consul.key-suffix}")
    private String keySuffix;

    public FileConfigValueServiceConsulImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public Map<String, String> getKeyValueList(String appCode) {
        Map<String, String> keyValues = new HashMap<>();

        //Property values from default key "{spring.application.name}.yaml"
        ObjectNode defaultConsulNode = getFromConsul(url + appCode + ".yaml");
        if (defaultConsulNode != null) floatNode(keyValues, "", defaultConsulNode);

        //Property values from profile key "{spring.application.name}-{spring.profiles.active}.yaml" overwrite value if exists
        ObjectNode profileConsulNode = loadYaml(appCode);
        if (profileConsulNode != null) floatNode(keyValues, "", profileConsulNode);

        return keyValues;
    }

    @Override
    public void deleteAllValues(String appCode) {
        deleteFromConsul(url + appCode + keySuffix);
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