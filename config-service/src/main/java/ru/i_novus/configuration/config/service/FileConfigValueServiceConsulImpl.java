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

    @Value("#{'-'.concat('${config.consul.files.key.suffix.profile}').concat('.yaml')}")
    private String profileKeySuffix;

    public FileConfigValueServiceConsulImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public Map<String, String> getKeyValueList(String appCode) {
        Map<String, String> keyValues = new HashMap<>();

        //Property values from default key
        ObjectNode defaultConsulNode = getFromConsul(url + appCode + ".yaml");
        if (defaultConsulNode != null) floatNode(keyValues, "", defaultConsulNode);

        //Property values from profile key overwrite value if exists
        ObjectNode profileConsulNode = loadYaml(appCode);
        if (profileConsulNode != null) floatNode(keyValues, "", profileConsulNode);

        return keyValues;
    }

    @Override
    public void deleteAllValues(String appCode) {
        deleteFromConsul(url + appCode + profileKeySuffix);
    }

    @Override
    protected ObjectNode loadYaml(String appCode) {
        return getFromConsul(url + appCode + profileKeySuffix);
    }

    @Override
    protected void saveYaml(String appCode, JsonNode node) {
        putToConsul(url + appCode + profileKeySuffix, node);
    }
}