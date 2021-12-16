package ru.i_novus.configuration.system_application.loader;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.TestApp;
import ru.i_novus.configuration.system_application.entity.ApplicationEntity;
import ru.i_novus.configuration.system_application.loader.builders.LoaderApplicationBuilder;
import ru.i_novus.configuration.system_application.repository.ApplicationRepository;
import ru.i_novus.system_application.api.model.SimpleApplicationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Тесты лоадера систем
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableEmbeddedPg
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ApplicationServerLoaderTest {

    @Autowired
    private ApplicationServerLoader applicationServerLoader;

    @Autowired
    private ApplicationRepository repository;

    @LocalServerPort
    private int port;


    /**
     * Тест {@link ApplicationServerLoader}
     */
    @Test
    public void simpleLoader() {
        BiConsumer<List<SimpleApplicationResponse>, String> loader = applicationServerLoader::load;
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
    private void case1(BiConsumer<List<SimpleApplicationResponse>, String> loader) {
        SimpleApplicationResponse application1 = LoaderApplicationBuilder.buildApplication1();
        SimpleApplicationResponse application2 = LoaderApplicationBuilder.buildApplication2();
        List<SimpleApplicationResponse> data = Arrays.asList(application1, application2);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(2));
        applicationAssertEquals(application1, repository.findByCode("app1"));
        applicationAssertEquals(application2, repository.findByCode("app2"));
    }

    /**
     * Вставка двух записей, обе есть в БД, но одна будет обновлена
     */
    private void case2(BiConsumer<List<SimpleApplicationResponse>, String> loader) {
        SimpleApplicationResponse application1 = LoaderApplicationBuilder.buildApplication1();
        SimpleApplicationResponse application2 = LoaderApplicationBuilder.buildApplication2Updated();
        List<SimpleApplicationResponse> data = Arrays.asList(application1, application2);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(2));
        applicationAssertEquals(application1, repository.findByCode("app1"));
        applicationAssertEquals(application2, repository.findByCode("app2"));
    }

    /**
     * Вставка трех записей, две есть в БД, третьей нет
     */
    private void case3(BiConsumer<List<SimpleApplicationResponse>, String> loader) {
        SimpleApplicationResponse application1 = LoaderApplicationBuilder.buildApplication1();
        SimpleApplicationResponse application2 = LoaderApplicationBuilder.buildApplication2Updated();
        SimpleApplicationResponse application3 = LoaderApplicationBuilder.buildApplication3();
        List<SimpleApplicationResponse> data = Arrays.asList(application1, application2, application3);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(3));
        applicationAssertEquals(application1, repository.findByCode("app1"));
        applicationAssertEquals(application2, repository.findByCode("app2"));
        applicationAssertEquals(application3, repository.findByCode("app3"));
    }

    /**
     * Вставка двух записей, в БД три записи, вторая будет обновлена, третья не будет удалена
     */
    private void case4(BiConsumer<List<SimpleApplicationResponse>, String> loader) {
        SimpleApplicationResponse application1 = LoaderApplicationBuilder.buildApplication1();
        SimpleApplicationResponse application2 = LoaderApplicationBuilder.buildApplication2();
        List<SimpleApplicationResponse> data = Arrays.asList(application1, application2);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(3));
        applicationAssertEquals(application1, repository.findByCode("app1"));
        applicationAssertEquals(application2, repository.findByCode("app2"));
        applicationAssertEquals(LoaderApplicationBuilder.buildApplication3(), repository.findByCode("app3"));
    }

    /**
     * Обновление двух записей, в БД три записи, вторая будет обновлена новыми значениями, третья - старыми значениями второй
     */
    private void case5(BiConsumer<List<SimpleApplicationResponse>, String> loader) {
        SimpleApplicationResponse application2 = LoaderApplicationBuilder.buildApplication2Updated();
        SimpleApplicationResponse application3 = LoaderApplicationBuilder.buildApplication3();
        SimpleApplicationResponse tmp = LoaderApplicationBuilder.buildApplication2();
        application3.setName(tmp.getName());
        List<SimpleApplicationResponse> data = Arrays.asList(application2, application3);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(3));
        applicationAssertEquals(LoaderApplicationBuilder.buildApplication1(), repository.findByCode("app1"));
        applicationAssertEquals(application2, repository.findByCode("app2"));
        applicationAssertEquals(application3, repository.findByCode("app3"));
    }

    private void applicationAssertEquals(SimpleApplicationResponse expected, ApplicationEntity actual) {
        assertEquals(expected.getCode(), actual.getCode());
        assertEquals(expected.getName(), actual.getName());
    }
}
