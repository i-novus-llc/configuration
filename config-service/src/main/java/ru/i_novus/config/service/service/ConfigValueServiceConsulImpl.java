package ru.i_novus.config.service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.config.api.service.ConfigValueService;

import java.util.*;

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
    public Map<String, String> getKeyValueListByApplicationCode(String appCode) {
        List raw = restTemplate.getForObject(getFullUrl(appCode, "") + "?recurse=true", List.class);

        Map<String, String> keyValues = new HashMap<>();
        if (raw != null && !raw.isEmpty()) {
            for (int i = 1; i < raw.size(); i++) {
                Map rawObject = (Map) raw.get(i);
                String code = ((String) rawObject.get("Key"))
                        .substring(prefix.length() + appCode.length() + 2)
                        .replace("/", ".");
                String value = new String(Base64.getDecoder().decode((String) rawObject.get("Value")));
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
    public void saveAllValues(String appCode, Map<String, String> data) {
        List<Map<String, Map<String, String>>> list = new ArrayList<>();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            Map<String, String> keyValueMap = new HashMap<>();
            keyValueMap.put("Key", prefix + "/" + entry.getKey().replace(".", "/"));
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
    public void deleteValue(String appCode, String code) {
        restTemplate.delete(getFullUrl(appCode, code));
    }

    private String getFullUrl(String appCode, String code) {
        return url + appCode + "/" + code.replace(".", "/");
    }
}