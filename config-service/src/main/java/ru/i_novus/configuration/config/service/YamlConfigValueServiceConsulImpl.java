package ru.i_novus.configuration.config.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.config.api.service.ConfigValueService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Реализация сервиса для работы со значениями настроек, хранящихся в Consul в формате yaml
 */
@Slf4j
public class YamlConfigValueServiceConsulImpl implements ConfigValueService {

    @Value("${config.consul.url}")
    private String url;

    @Value("${spring.cloud.consul.config.data-key:data}")
    private String dataKey;

    protected final RestTemplate restTemplate;

    protected final ObjectMapper yamlMapper;

    public YamlConfigValueServiceConsulImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        YAMLFactory factory = new YAMLFactory();
        factory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        factory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        yamlMapper = new ObjectMapper(factory);
        yamlMapper.findAndRegisterModules();
    }

    @Override
    public String getValue(String appCode, String code) {
        Map<String, String> values = getKeyValueList(appCode);
        return values.get(code);
    }

    @Override
    public Map<String, String> getKeyValueList(String appCode) {
        ObjectNode node = loadYaml(appCode);
        Map<String, String> keyValues = new HashMap<>();
        if (node != null) floatNode(keyValues, "", node);
        return keyValues;
    }

    @Override
    public void saveValue(String appCode, String code, String value) {
        ObjectNode node = loadYaml(appCode);
        if (node == null) node = yamlMapper.createObjectNode();

        setValue(node, code, value);

        saveYaml(appCode, node);
    }

    @Override
    public void saveAllValues(String appCode, Map<String, String> updatedData, Map<String, String> deletedData) {
        ObjectNode node = loadYaml(appCode);
        if (node == null) node = yamlMapper.createObjectNode();

        for (Map.Entry<String, String> entry : updatedData.entrySet()) {
            setValue(node, entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, String> entry : deletedData.entrySet()) {
            deleteNodeValue(node, entry.getKey());
        }
        deleteEmptyNodes(node);

        saveYaml(appCode, node);
    }

    @Override
    public void deleteValue(String appCode, String code) {
        ObjectNode node = loadYaml(appCode);
        if (node == null) return;
        deleteNodeValue(node, code);
        deleteEmptyNodes(node);

        saveYaml(appCode, node);
    }

    @Override
    public void deleteAllValues(String appCode) {
        restTemplate.delete(url + appCode + "/" + dataKey);
    }

    protected ObjectNode loadYaml(String appCode) {
        try {
            String rawValue = restTemplate.getForObject(url + appCode + "/" + dataKey + "?raw=1", String.class);

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

    protected void saveYaml(String appCode, JsonNode node) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            String yaml = yamlMapper.writeValueAsString(node);
            HttpEntity<String> httpEntity = new HttpEntity<>(yaml, headers);
            //todo
            restTemplate.put(url + appCode + "/" + dataKey, httpEntity);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    private void floatNode(Map<String, String> keyValues, String prefix, JsonNode node) {
        if (node.isValueNode()) {
            keyValues.put(prefix, node.asText());
        } else {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                floatNode(keyValues, prefix.isEmpty() ? field.getKey() : prefix + "." + field.getKey(), field.getValue());
            }
        }
    }

    private void setValue(ObjectNode root, String code, String value) {
        Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (field.getKey().equals(code)) {
                fields.remove();
                root.set(code, root.textNode(value));
                return;
            }
        }
        int li = code.lastIndexOf('.');
        while (li > 0) {
            String hd = code.substring(0, li);
            fields = root.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getKey().equals(hd)) {
                    setValue((ObjectNode) field.getValue(), code.substring(li + 1), value);
                    return;
                }
                if (field.getKey().startsWith(hd + '.')) {
                    ObjectNode newRoot = root.objectNode();
                    root.set(hd, newRoot);
                    newRoot.set(field.getKey().substring(hd.length() + 1), field.getValue());
                    newRoot.set(code.substring(li + 1), newRoot.textNode(value));
                    root.remove(field.getKey());
                    return;
                }
            }
            li = code.lastIndexOf('.', li - 1);
        }
        root.set(code, root.textNode(value));
    }

    private void deleteNodeValue(JsonNode root, String code) {
        int li = code.length();
        while (li > 0) {
            String hd = code.substring(0, li);
            Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getKey().equals(hd)) {
                    if (li == code.length()) {
                        fields.remove();
                        return;
                    }
                    deleteNodeValue(field.getValue(), code.substring(li + 1));
                }
            }
            li = code.lastIndexOf('.', li - 1);
        }
    }

    private boolean deleteEmptyNodes(JsonNode root) {
        Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
        boolean result = true;
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (field.getValue().isValueNode()) {
                result = false;
                continue;
            }
            if (deleteEmptyNodes(field.getValue())) fields.remove();
            else result = false;
        }
        return result;
    }
}
