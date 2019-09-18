package ru.i_novus.config.service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.config.api.service.ConfigValueService;

import java.util.*;

/**
 * Реализация сервиса для работы со значениями настроек, хранящихся в Consul
 */
@Service
public class ConfigValueServiceConsulImpl implements ConfigValueService {

    @Value("${config.consul.url}")
    private String url;

    @Value("${spring.cloud.consul.config.prefix}")
    private String prefix;

    private RestTemplate restTemplate = new RestTemplate();


    @Override
    public String getValue(String appName, String code) {
        return restTemplate.getForObject(getFullUrl(appName, code) + "?raw=1", String.class);
    }

    @Override
    public Map<String, String> getKeyValueListByApplicationName(String appName) {
        List<Map> raw = restTemplate.getForObject(getFullUrl(appName, "") + "?recurse=true", List.class);

        Map<String, String> keyValues = new HashMap<>();
        if (raw != null && !raw.isEmpty()) {
            for (Map rawObject : raw) {
                String code = ((String) rawObject.get("Key"));
                if (code.endsWith("/")) continue;
                code = code.substring(prefix.length() + appName.length() + 2).replace("/", ".");

                Object rawValue = rawObject.get("Value");
                String value = (rawValue != null) ? new String(Base64.getDecoder().decode((String) rawValue)) : null;

                keyValues.put(code, value);
            }
        }

        return keyValues;
    }

    @Override
    public void saveValue(String appName, String code, String value) {
        restTemplate.put(getFullUrl(appName, code), value);
    }

    @Override
    public void saveAllValues(String appName, Map<String, String> data) {
        List<Map<String, Map<String, String>>> list = new ArrayList<>();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            Map<String, String> keyValueMap = new HashMap<>();
            keyValueMap.put("Key", prefix + "/" + appName + "/" + entry.getKey().replace(".", "/"));
            keyValueMap.put("Value", new String(Base64.getEncoder().encode(entry.getValue().getBytes())));
            keyValueMap.put("Verb", "set");

            Map<String, Map<String, String>> map = new HashMap<>();
            map.put("KV", keyValueMap);

            list.add(map);
        }

        String fullUrl = url.substring(0, url.indexOf("kv")) + "txn";

        restTemplate.put(fullUrl, list);
    }

    @Override
    public void deleteValue(String appName, String code) {
        restTemplate.delete(getFullUrl(appName, code));
    }

    @Override
    public void deleteAllValues(String appName) {
        restTemplate.delete(getFullUrl(appName, "") + "?recurse=true");
    }

    private String getFullUrl(String appName, String code) {
        return url + appName + "/" + code.replace(".", "/");
    }
}