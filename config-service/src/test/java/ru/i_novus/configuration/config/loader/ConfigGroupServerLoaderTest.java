package ru.i_novus.configuration.config.loader;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.TestApp;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.configuration.config.entity.GroupCodeEntity;
import ru.i_novus.configuration.config.entity.GroupEntity;
import ru.i_novus.configuration.config.loader.builders.LoaderGroupBuilder;
import ru.i_novus.configuration.config.repository.GroupCodeRepository;
import ru.i_novus.configuration.config.repository.GroupRepository;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Тесты лоадера групп настроек
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableEmbeddedPg
public class ConfigGroupServerLoaderTest {

    @Autowired
    private ConfigGroupServerLoader configLoader;

    @Autowired
    private GroupRepository repository;

    @Autowired
    private GroupCodeRepository groupCodeRepository;

    @LocalServerPort
    private int port;


    /**
     * Тест {@link ConfigServerLoader}
     */
    @Test
    public void simpleLoader() {
        BiConsumer<List<GroupForm>, String> loader = configLoader::load;
        repository.deleteAll();
        case1(loader);
        case2(loader);
        case3(loader);
        case4(loader);
        case5(loader);
        case6(loader);
    }

    /**
     * Вставка двух новых записей, в БД нет записей
     */
    private void case1(BiConsumer<List<GroupForm>, String> loader) {
        GroupForm groupForm1 = LoaderGroupBuilder.buildGroup1();
        GroupForm groupForm2 = LoaderGroupBuilder.buildGroup2();
        List<GroupForm> data = Arrays.asList(groupForm1, groupForm2);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(2));
        assertThat(groupCodeRepository.findAll().size(), is(6));
        configGroupAssertEquals(groupForm1, repository.findByName("group1"));
        configGroupAssertEquals(groupForm2, repository.findByName("group2"));
    }

    /**
     * Вставка двух записей, обе есть в БД, но одна будет обновлена
     */
    private void case2(BiConsumer<List<GroupForm>, String> loader) {
        GroupForm groupForm1 = LoaderGroupBuilder.buildGroup1();
        GroupForm groupForm2 = LoaderGroupBuilder.buildGroup2Updated();
        List<GroupForm> data = Arrays.asList(groupForm1, groupForm2);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(2));
        assertThat(groupCodeRepository.findAll().size(), is(5));
        configGroupAssertEquals(groupForm1, repository.findByName("group1"));
        configGroupAssertEquals(groupForm2, repository.findByName("group2"));
    }

    /**
     * Вставка трех записей, две есть в БД, третьей нет
     */
    private void case3(BiConsumer<List<GroupForm>, String> loader) {
        GroupForm groupForm1 = LoaderGroupBuilder.buildGroup1();
        GroupForm groupForm2 = LoaderGroupBuilder.buildGroup2Updated();
        GroupForm groupForm3 = LoaderGroupBuilder.buildGroup3();
        List<GroupForm> data = Arrays.asList(groupForm1, groupForm2, groupForm3);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(3));
        assertThat(groupCodeRepository.findAll().size(), is(8));
        configGroupAssertEquals(groupForm1, repository.findByName("group1"));
        configGroupAssertEquals(groupForm2, repository.findByName("group2"));
        configGroupAssertEquals(groupForm3, repository.findByName("group3"));
    }

    /**
     * Вставка двух записей, в БД три записи, вторая будет обновлена, третья не будет удалена
     */
    private void case4(BiConsumer<List<GroupForm>, String> loader) {
        GroupForm groupForm1 = LoaderGroupBuilder.buildGroup1();
        GroupForm groupForm2 = LoaderGroupBuilder.buildGroup2();
        List<GroupForm> data = Arrays.asList(groupForm1, groupForm2);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(3));
        assertThat(groupCodeRepository.findAll().size(), is(9));
        configGroupAssertEquals(groupForm1, repository.findByName("group1"));
        configGroupAssertEquals(groupForm2, repository.findByName("group2"));
        configGroupAssertEquals(LoaderGroupBuilder.buildGroup3(), repository.findByName("group3"));
    }


    /**
     * Обновление одной из записей неуникальными кодами.
     * Проверка, что обновления не будет и другие записи не будут повреждены
     */
    private void case5(BiConsumer<List<GroupForm>, String> loader) {
        GroupForm groupForm1 = LoaderGroupBuilder.buildGroup1();
        GroupForm groupForm3 = LoaderGroupBuilder.buildGroup2();
        groupForm3.setCodes(groupForm1.getCodes());
        List<GroupForm> data = Arrays.asList(groupForm3);

        try {
            loader.accept(data, "test");
            fail("Method should throw exception, but he didn't!");
        } catch (UserException ignored) {}

        assertThat(repository.findAll().size(), is(3));
        assertThat(groupCodeRepository.findAll().size(), is(9));
        configGroupAssertEquals(groupForm1, repository.findByName("group1"));
        configGroupAssertEquals(LoaderGroupBuilder.buildGroup2(), repository.findByName("group2"));
        configGroupAssertEquals(LoaderGroupBuilder.buildGroup3(), repository.findByName("group3"));
    }

    /**
     * Обновление двух записей, в БД три записи, вторая будет обновлена новыми значениями, третья - старыми значениями второй
     */
    private void case6(BiConsumer<List<GroupForm>, String> loader) {
        GroupForm groupForm2 = LoaderGroupBuilder.buildGroup2Updated();
        GroupForm groupForm3 = LoaderGroupBuilder.buildGroup3();
        GroupForm tmp = LoaderGroupBuilder.buildGroup2();
        groupForm3.setDescription(tmp.getDescription());
        groupForm3.setPriority(tmp.getPriority());
        groupForm3.setCodes(tmp.getCodes());
        List<GroupForm> data = Arrays.asList(groupForm2, groupForm3);

        loader.accept(data, "test");

        assertThat(repository.findAll().size(), is(3));
        assertThat(groupCodeRepository.findAll().size(), is(8));
        configGroupAssertEquals(LoaderGroupBuilder.buildGroup1(), repository.findByName("group1"));
        configGroupAssertEquals(groupForm2, repository.findByName("group2"));
        configGroupAssertEquals(groupForm3, repository.findByName("group3"));
    }

    private void configGroupAssertEquals(GroupForm expected, GroupEntity actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getPriority(), actual.getPriority());
        assertEquals(expected.getCodes(), actual.getCodes().stream().map(GroupCodeEntity::getCode).collect(Collectors.toSet()));
    }
}
