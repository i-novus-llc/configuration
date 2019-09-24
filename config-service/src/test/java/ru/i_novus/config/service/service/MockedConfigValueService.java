package ru.i_novus.config.service.service;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MockedConfigValueService extends ConfigValueServiceConsulImpl {

    @Override
    public void saveAllValues(String appCode, Map<String, String> data) {
        Map<String, String> result = Map.of(
                "k4", "v444", "k5", "v555", "k6", "v666"
        );

        assertEquals("appCode", appCode);
        assertEquals(result, data);
    }
}
