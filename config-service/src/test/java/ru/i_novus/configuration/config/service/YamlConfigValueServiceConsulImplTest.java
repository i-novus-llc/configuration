package ru.i_novus.configuration.config.service;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.TestApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableEmbeddedPg
public class YamlConfigValueServiceConsulImplTest {

    @MockBean(name = "restTemplate")
    private RestTemplate restTemplate;

    @Test
    public void getKeyValueEmptyMapListTest() {
        YamlConfigValueServiceConsulImpl yamlConfigValueServiceConsul = new YamlConfigValueServiceConsulImpl(restTemplate);
        Mockito.when(restTemplate.getForObject(getPath(), String.class)).thenReturn(null);
        Map<String, String> keyValueMap = yamlConfigValueServiceConsul.getKeyValueList("myApplication");
        assertTrue(keyValueMap.isEmpty());
    }

    @Test
    public void getKeyValueMapListTest() throws IOException {
        YamlConfigValueServiceConsulImpl yamlConfigValueServiceConsul = new YamlConfigValueServiceConsulImpl(restTemplate);
        Mockito.when(restTemplate.getForObject(getPath(), String.class)).thenReturn(readFile("/test_file.yml"));
        Map<String, String> keyValueMap = yamlConfigValueServiceConsul.getKeyValueList("myApplication");

        assertTrue(keyValueMap.containsKey("server.port"));
        assertEquals(keyValueMap.get("server.port"), "8080");

        assertTrue(keyValueMap.containsKey("spring.datasource.url"));
        assertTrue(keyValueMap.containsKey("spring.datasource.driver-class-name"));
        assertTrue(keyValueMap.containsKey("spring.datasource.username"));
        assertTrue(keyValueMap.containsKey("spring.datasource.password"));
        assertEquals(keyValueMap.get("spring.datasource.url"), "jdbc:postgresql://localhost:5432/audit");
        assertEquals(keyValueMap.get("spring.datasource.driver-class-name"), "org.postgresql.Driver");
        assertEquals(keyValueMap.get("spring.datasource.username"), "postgres");
        assertEquals(keyValueMap.get("spring.datasource.password"), "postgres");

        assertTrue(keyValueMap.containsKey("spring.jpa.database-platform"));
        assertTrue(keyValueMap.containsKey("spring.liquibase.change-log"));
        assertEquals(keyValueMap.get("spring.jpa.database-platform"), "org.hibernate.dialect.PostgreSQL9Dialect");
        assertEquals(keyValueMap.get("spring.liquibase.change-log"), "classpath:/db/changelog-master.xml");

        assertTrue(keyValueMap.containsKey("cxf.path"));
        assertTrue(keyValueMap.containsKey("cxf.jaxrs.component-scan"));
        assertTrue(keyValueMap.containsKey("cxf.servlet.init.service-list-path"));
        assertEquals(keyValueMap.get("cxf.path"), "/api");
        assertEquals(keyValueMap.get("cxf.jaxrs.component-scan"), "true");
        assertEquals(keyValueMap.get("cxf.servlet.init.service-list-path"), "/info");
    }

    @Test
    public void getValueTest() throws IOException {
        YamlConfigValueServiceConsulImpl yamlConfigValueServiceConsul = new YamlConfigValueServiceConsulImpl(restTemplate);
        Mockito.when(restTemplate.getForObject(getPath(), String.class)).thenReturn(readFile("/test_file.yml"));
        String serverPortValue = yamlConfigValueServiceConsul.getValue("myApplication", "server.port");

        assertNotNull(serverPortValue);
        assertEquals(serverPortValue, "8080");
    }

    @Test
    public void getNotExistValueTest() throws IOException {
        YamlConfigValueServiceConsulImpl yamlConfigValueServiceConsul = new YamlConfigValueServiceConsulImpl(restTemplate);
        Mockito.when(restTemplate.getForObject(getPath(), String.class)).thenReturn(readFile("/test_file.yml"));
        String swaggerResourcePackageValue = yamlConfigValueServiceConsul.getValue("myApplication", "jaxrs.swagger.resource-package");

        assertNull(swaggerResourcePackageValue);
    }

    private String getPath() {
        return any() + "myApplication" + "/" + any() + "?raw=1";
    }

    private String readFile(String fileName) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(fileName)) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines()
                        .collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

}
