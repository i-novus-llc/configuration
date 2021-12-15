package ru.i_novus.configuration.system_application.loader;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.TestApp;
import ru.i_novus.configuration.system_application.entity.SystemEntity;
import ru.i_novus.configuration.system_application.loader.builders.LoaderSystemBuilder;
import ru.i_novus.configuration.system_application.repository.SystemRepository;
import ru.i_novus.system_application.api.model.SimpleSystemResponse;

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
public class SystemServerLoaderTest {

    @Autowired
    private SystemServerLoader systemServerLoader;

    @Autowired
    private SystemRepository repository;

    @LocalServerPort
    private int port;


    /**
     * Тест {@link ApplicationServerLoader}
     */
    @Test
    public void simpleLoader() {
        BiConsumer<List<SimpleSystemResponse>, String> loader = systemServerLoader::load;
        repository.deleteAll();
        case1(loader);
        case2(loader);
        case3(loader);
        case4(loader);
        case5(loader);
        repository.deleteAll();
    }

    /**
     * Вставка двух новых записей, в БД нет записей
     */
    private void case1(BiConsumer<List<SimpleSystemResponse>, String> loader) {
        SimpleSystemResponse system1 = LoaderSystemBuilder.buildSystem1();
        SimpleSystemResponse system2 = LoaderSystemBuilder.buildSystem2();
        List<SimpleSystemResponse> data = Arrays.asList(system1, system2);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(2));
        systemAssertEquals(system1, repository.findByCode("sys1"));
        systemAssertEquals(system2, repository.findByCode("sys2"));
    }

    /**
     * Вставка двух записей, обе есть в БД, но одна будет обновлена
     */
    private void case2(BiConsumer<List<SimpleSystemResponse>, String> loader) {
        SimpleSystemResponse system1 = LoaderSystemBuilder.buildSystem1();
        SimpleSystemResponse system2 = LoaderSystemBuilder.buildSystem2Updated();
        List<SimpleSystemResponse> data = Arrays.asList(system1, system2);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(2));
        systemAssertEquals(system1, repository.findByCode("sys1"));
        systemAssertEquals(system2, repository.findByCode("sys2"));
    }

    /**
     * Вставка трех записей, две есть в БД, третьей нет
     */
    private void case3(BiConsumer<List<SimpleSystemResponse>, String> loader) {
        SimpleSystemResponse system1 = LoaderSystemBuilder.buildSystem1();
        SimpleSystemResponse system2 = LoaderSystemBuilder.buildSystem2Updated();
        SimpleSystemResponse system3 = LoaderSystemBuilder.buildSystem3();
        List<SimpleSystemResponse> data = Arrays.asList(system1, system2, system3);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(3));
        systemAssertEquals(system1, repository.findByCode("sys1"));
        systemAssertEquals(system2, repository.findByCode("sys2"));
        systemAssertEquals(system3, repository.findByCode("sys3"));
    }

    /**
     * Вставка двух записей, в БД три записи, вторая будет обновлена, третья не будет удалена
     */
    private void case4(BiConsumer<List<SimpleSystemResponse>, String> loader) {
        SimpleSystemResponse system1 = LoaderSystemBuilder.buildSystem1();
        SimpleSystemResponse system2 = LoaderSystemBuilder.buildSystem2();
        List<SimpleSystemResponse> data = Arrays.asList(system1, system2);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(3));
        systemAssertEquals(system1, repository.findByCode("sys1"));
        systemAssertEquals(system2, repository.findByCode("sys2"));
        systemAssertEquals(LoaderSystemBuilder.buildSystem3(), repository.findByCode("sys3"));
    }

    /**
     * Обновление двух записей, в БД три записи, вторая будет обновлена новыми значениями, третья - старыми значениями второй
     */
    private void case5(BiConsumer<List<SimpleSystemResponse>, String> loader) {
        SimpleSystemResponse system2 = LoaderSystemBuilder.buildSystem2Updated();
        SimpleSystemResponse system3 = LoaderSystemBuilder.buildSystem3();
        SimpleSystemResponse tmp = LoaderSystemBuilder.buildSystem2();
        system3.setDescription(tmp.getDescription());
        system3.setName(tmp.getName());
        List<SimpleSystemResponse> data = Arrays.asList(system2, system3);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(3));
        systemAssertEquals(LoaderSystemBuilder.buildSystem1(), repository.findByCode("sys1"));
        systemAssertEquals(system2, repository.findByCode("sys2"));
        systemAssertEquals(system3, repository.findByCode("sys3"));
    }

    private void systemAssertEquals(SimpleSystemResponse expected, SystemEntity actual) {
        assertEquals(expected.getCode(), actual.getCode());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
    }
}
