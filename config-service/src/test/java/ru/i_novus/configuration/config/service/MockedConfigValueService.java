package ru.i_novus.configuration.config.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.i_novus.configuration.config.service.ConfigValueServiceConsulImpl;

import java.util.Map;

import static org.junit.Assert.assertEquals;

@Service
@Primary
public class MockedConfigValueService extends ConfigValueServiceConsulImpl {

    @Override
    public void saveAllValues(String appCode, Map<String, String> updatedData, Map<String, String> deletedData) {
        Map<String, String> expectedUpdatedData = Map.of(
                "k0", "v0", "k4", "v444", "k5", "v555", "k6", "v666"
        );
        Map<String, String> expectedDeletedData = Map.of(
                "k7", "v7", "k9", "v9",
                "k11", "v11", "k13", "v13"
        );

        assertEquals("app-auth", appCode);
        assertEquals(expectedUpdatedData, updatedData);
        assertEquals(expectedDeletedData, deletedData);
    }
}
