package ru.i_novus.config.service.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.ConfigServiceApplication;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.service.entity.ConfigEntity;
import ru.i_novus.config.service.loader.builders.ConfigFormBuilder;
import ru.i_novus.config.service.repository.ConfigRepository;
import ru.i_novus.config.service.service.ConfigRestServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Тесты лоадера настроек
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConfigServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableEmbeddedPg
public class ConfigServerLoaderTest {

    @Autowired
    private ConfigServerLoader configLoader;

    @Autowired
    private RepositoryServerLoader<ConfigForm, ConfigEntity, String> repositoryLoader;

    @Autowired
    private ConfigRepository repository;

    @LocalServerPort
    private int port;


    /**
     * Тест {@link ConfigServerLoader}
     */
    @Test
    public void simpleLoader() {
        BiConsumer<List<ConfigForm>, String> loader = (data, subject) -> {
            configLoader.load(data, subject);
        };
        repository.deleteAll();
        case1(loader);
        case2(loader);
        case3(loader);
        case4(loader);
        case5(loader);
    }

    /**
     * Тест {@link RepositoryServerLoader}
     */
    @Test
    public void repositoryLoader() {
        BiConsumer<List<ConfigForm>, String> loader = (data, subject) -> {
            repositoryLoader.load(data, subject);
        };
        repository.deleteAll();
        case1(loader);
        case2(loader);
        case3(loader);
        case4(loader);
        case5(loader);
    }

    /**
     * Тест {@link ConfigRestServiceImpl}
     */
    @Test
    public void restLoader() {
        BiConsumer<List<ConfigForm>, String> loader = (data, subject) -> {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:" + port + "/api/loaders/" + subject + "/configs";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<ConfigForm>> request = new HttpEntity<>(data, headers);
            assertThat(restTemplate.postForEntity(url, request, String.class).getStatusCode().is2xxSuccessful(), is(true));
        };
        repository.deleteAll();
        case1(loader);
        case2(loader);
        case3(loader);
        case4(loader);
        case5(loader);
    }

    /**
     * Вставка двух новых записей, в БД нет записей
     */
    private void case1(BiConsumer<List<ConfigForm>, String> loader) {
        ConfigForm configForm1 = ConfigFormBuilder.buildConfigForm1();
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm2();
        List<ConfigForm> data = Arrays.asList(configForm1, configForm2);

        loader.accept(data, "test-app");

        assertThat(repository.findByApplicationCode("test-app").size(), is(2));
        configAssertEquals(configForm1, repository.findById("code1").get());
        configAssertEquals(configForm2, repository.findById("code2").get());
    }

    /**
     * Вставка двух записей, обе есть в БД, но одна будет обновлена
     */
    private void case2(BiConsumer<List<ConfigForm>, String> loader) {
        ConfigForm configForm1 = ConfigFormBuilder.buildConfigForm1();
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm2Updated();
        List<ConfigForm> data = Arrays.asList(configForm1, configForm2);

        loader.accept(data, "test-app");

        assertThat(repository.findByApplicationCode("test-app").size(), is(2));
        configAssertEquals(configForm1, repository.findById("code1").get());
        configAssertEquals(configForm2, repository.findById("code2").get());
    }

    /**
     * Вставка трех записей, две есть в БД, третьей нет
     */
    private void case3(BiConsumer<List<ConfigForm>, String> loader) {
        ConfigForm configForm1 = ConfigFormBuilder.buildConfigForm1();
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm2Updated();
        ConfigForm configForm3 = ConfigFormBuilder.buildConfigForm3();
        List<ConfigForm> data = Arrays.asList(configForm1, configForm2, configForm3);

        loader.accept(data, "test-app");

        assertThat(repository.findByApplicationCode("test-app").size(), is(3));
        configAssertEquals(configForm1, repository.findById("code1").get());
        configAssertEquals(configForm2, repository.findById("code2").get());
        configAssertEquals(configForm3, repository.findById("code3").get());
    }

    /**
     * Вставка двух записей, в БД три записи, вторая будет обновлена, третья будет удалена
     */
    private void case4(BiConsumer<List<ConfigForm>, String> loader) {
        ConfigForm configForm1 = ConfigFormBuilder.buildConfigForm1();
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm2();
        List<ConfigForm> data = Arrays.asList(configForm1, configForm2);

        loader.accept(data, "test-app");

        assertThat(repository.findByApplicationCode("test-app").size(), is(2));
        configAssertEquals(configForm1, repository.findById("code1").get());
        configAssertEquals(configForm2, repository.findById("code2").get());
    }

    /**
     * Вставка двух новых записей клиента application (applicationCode = null), в БД 2 записи клиента "test-app"
     */
    private void case5(BiConsumer<List<ConfigForm>, String> loader) {
        ConfigForm configForm3 = ConfigFormBuilder.buildConfigForm3();
        ConfigForm configForm4 = ConfigFormBuilder.buildConfigForm4();
        List<ConfigForm> data = Arrays.asList(configForm3, configForm4);

        loader.accept(data, "application");
        List<ConfigEntity> all = repository.findAll();
        assertThat(repository.findByApplicationCode(null).size(), is(2));
        configAssertEquals(configForm3, repository.findById("code3").get());
        configAssertEquals(configForm4, repository.findById("code4").get());
    }


    private void configAssertEquals(ConfigForm expected, ConfigEntity actual) {
        assertEquals(expected.getCode(), actual.getCode());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getValueType(), actual.getValueType());
    }
}