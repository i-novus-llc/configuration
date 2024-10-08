package ru.i_novus.configuration.config.service;

import net.n2oapp.platform.test.autoconfigure.pg.EnableTestcontainersPg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableTestcontainersPg
public class YamlConfigValueServiceConsulImplTest {

    @MockBean(name = "restTemplate")
    private RestTemplate restTemplate;

    private YamlConfigValueServiceConsulImpl yamlConfigValueServiceConsul;

    @BeforeEach
    public void setUp() {
        yamlConfigValueServiceConsul = new YamlConfigValueServiceConsulImpl(restTemplate);
        when(restTemplate.getForObject(any() + "myApplication" + "/" + any() + "?raw=1", String.class))
                .thenReturn(readFile("/test_file.yml"));
    }

    @Test
    public void getKeyValueEmptyMapListTest() {
        when(restTemplate.getForObject(any() + "myApplication" + "/" + any() + "?raw=1", String.class)).thenReturn("");
        Map<String, String> keyValueMap = yamlConfigValueServiceConsul.getKeyValueList("myApplication");
        assertTrue(keyValueMap.isEmpty());
    }

    @Test
    public void getKeyValueMapListTest() {
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
    public void getValueTest() {
        String serverPortValue = yamlConfigValueServiceConsul.getValue("myApplication", "server.port");
        assertNotNull(serverPortValue);
        assertEquals(serverPortValue, "8080");
    }

    @Test
    public void getNotExistValueTest() {
        String swaggerResourcePackageValue = yamlConfigValueServiceConsul.getValue("myApplication", "jaxrs.swagger.resource-package");
        assertNull(swaggerResourcePackageValue);
    }

    @Test
    public void saveRootValueTest() {
        yamlConfigValueServiceConsul.saveValue("myApplication", "egisz.fnsi.url", "https://nsi.rosminzdrav.ru");

        //Проверка
        String resultYaml = getSavedResult(restTemplate);

        assertTrue(resultYaml.contains("egisz.fnsi.url: https://nsi.rosminzdrav.ru"));
    }

    @Test
    public void saveValueTest() {
        yamlConfigValueServiceConsul.saveValue("myApplication", "spring.data.cassandra.contact-points", "127.0.0.1");

        //Проверка
        String resultYaml = getSavedResult(restTemplate);

        assertTrue(resultYaml.contains("  datasource:\n"));
        assertTrue(resultYaml.contains("    url: jdbc:postgresql://localhost:5432/audit\n"));
        assertTrue(resultYaml.contains("    url: jdbc:postgresql://localhost:5432/audit\n"));
        assertTrue(resultYaml.contains("  data.cassandra.contact-points: 127.0.0.1\n"));
    }

    @Test
    public void updateValueTest() {
        String importWorklogsTriggerCronExpressionValue = yamlConfigValueServiceConsul.getValue("myApplication", "cron-expressions.import-worklogs-trigger");
        assertNotNull(importWorklogsTriggerCronExpressionValue);
        assertEquals(importWorklogsTriggerCronExpressionValue, "0 5 * * * ?");

        //Изменение значения имеющейся настройке в yml файле
        yamlConfigValueServiceConsul.saveValue("myApplication", "cron-expressions.import-worklogs-trigger", "0 30 */2 * * ?");

        //Проверка
        String resultYaml = getSavedResult(restTemplate);

        assertTrue(resultYaml.contains("cron-expressions:\n"));
        assertTrue(resultYaml.contains("  import-worklogs-trigger: 0 30 */2 * * ?\n"));
    }

    @Test
    public void deleteValueTest() {
        yamlConfigValueServiceConsul.deleteValue("myApplication", "cron-expressions.employee-data-trigger");

        //Проверка
        String resultYaml = getSavedResult(restTemplate);

        assertTrue(resultYaml.contains("\ncron-expressions:\n"));
        assertFalse(resultYaml.contains("employee-data-trigger:"));
    }

    @SuppressWarnings("unchecked")
    private String getSavedResult(RestTemplate restTemplate) {
        ArgumentCaptor<HttpEntity<String>> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).put(stringArgumentCaptor.capture(), httpEntityArgumentCaptor.capture());
        HttpEntity<String> actualHttpEntityArgumentCaptor = httpEntityArgumentCaptor.getValue();
        assertNotNull(actualHttpEntityArgumentCaptor);
        String result = actualHttpEntityArgumentCaptor.getBody();
        assertNotNull(result);
        return result;
    }

    private String readFile(String fileName) {
        try (InputStream inputStream = getClass().getResourceAsStream(fileName)) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines()
                        .collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (IOException e) {
            return "";
        }
    }

}
