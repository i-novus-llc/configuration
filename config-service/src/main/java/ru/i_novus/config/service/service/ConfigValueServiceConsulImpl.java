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
    public String getValue(String appCode, String code) {
        return restTemplate.getForObject(getFullUrl(appCode, code) + "?raw=1", String.class);
    }

    @Override
    public Map<String, String> getKeyValueList(String appCode) {
        List<Map> raw = restTemplate.getForObject(getFullUrl(appCode, "") + "?recurse=true", List.class);

        Map<String, String> keyValues = new HashMap<>();
        if (raw != null && !raw.isEmpty()) {
            for (Map rawObject : raw) {
                String code = ((String) rawObject.get("Key"));
                if (code.endsWith("/")) continue;
                code = code.substring(prefix.length() + appCode.length() + 2).replace("/", ".");

                Object rawValue = rawObject.get("Value");
                String value = (rawValue != null) ? new String(Base64.getDecoder().decode((String) rawValue)) : null;

                keyValues.put(code, value);
            }
        }

        return keyValues;
    }

    @Override
    public void saveValue(String appCode, String code, String value) {
        restTemplate.put(getFullUrl(appCode, code), value);
    }

    @Override
    public void saveAllValues(String appCode, Map<String, String> updatedData, Map<String, String> deletedData) {
        List<Map<String, Map<String, String>>> list = new ArrayList<>();

        updatedData.entrySet().forEach(entry -> fillList(appCode, list, entry, "set"));
        deletedData.entrySet().forEach(entry -> fillList(appCode, list, entry, "delete"));

        String fullUrl = url.substring(0, url.indexOf("kv")) + "txn";

        restTemplate.put(fullUrl, list);
    }

    @Override
    public void deleteValue(String appCode, String code) {
        restTemplate.delete(getFullUrl(appCode, code));
    }

    @Override
    public void deleteAllValues(String appCode) {
        restTemplate.delete(getFullUrl(appCode, "") + "?recurse=true");
    }

    private String getFullUrl(String appCode, String code) {
        return url + appCode + "/" + code.replace(".", "/");
    }

    private void fillList(String appCode, List<Map<String, Map<String, String>>> list,
                          Map.Entry<String, String> entry, String verb) {
        String appConfigPrefixUrl = prefix + "/" + appCode + "/";
        Map<String, String> keyValueMap = new HashMap<>();
        keyValueMap.put("Key", appConfigPrefixUrl + entry.getKey().replace(".", "/"));
        keyValueMap.put("Value", new String(Base64.getEncoder().encode(entry.getValue().getBytes())));
        keyValueMap.put("Verb", verb);

        Map<String, Map<String, String>> map = new HashMap<>();
        map.put("KV", keyValueMap);

        list.add(map);
    }
}