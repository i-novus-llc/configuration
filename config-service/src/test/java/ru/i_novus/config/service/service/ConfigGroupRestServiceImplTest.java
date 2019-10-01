package ru.i_novus.config.service.service;

import net.n2oapp.platform.jaxrs.RestException;
import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.platform.test.autoconfigure.DefinePort;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.ConfigServiceApplication;
import ru.i_novus.config.api.criteria.GroupCriteria;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.api.service.ConfigGroupRestService;
import ru.i_novus.config.service.service.builders.GroupFormBuilder;
import ru.i_novus.ms.audit.client.AuditClient;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ConfigServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableJaxRsProxyClient(
        classes = ConfigGroupRestService.class,
        address = "http://localhost:${server.port}/api"
)
@DefinePort
@EnableEmbeddedPg
@TestPropertySource(properties = "spring.liquibase.change-log=classpath:/db/db.changelog-master-test.yaml")
public class ConfigGroupRestServiceImplTest {

    /**
     * @noinspection SpringJavaInjectionPointsAutowiringInspection
     */
    @Autowired
    @Qualifier("configGroupRestServiceJaxRsProxyClient")
    private ConfigGroupRestService groupRestService;

    @MockBean
    private AuditClient auditClient;


    /**
     * Проверка, что список групп настроек возвращается корректно
     */
    @Test
    public void getAllGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        GroupForm groupForm3 = GroupFormBuilder.buildGroupForm3();

        List<GroupForm> groupForms = groupRestService.getAllGroup(new GroupCriteria()).getContent();

        assertEquals(3, groupForms.size());
        groupAssertEquals(groupForm, groupForms.get(0));
        groupAssertEquals(groupForm2, groupForms.get(1));
        groupAssertEquals(groupForm3, groupForms.get(2));
    }

    /**
     * Проверка, что фильтрация групп настроек по имени работает корректно
     */
    @Test
    public void getAllGroupByNameTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm2();
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm3();

        GroupCriteria criteria = new GroupCriteria();
        criteria.setName("security");
        List<GroupForm> groupForms = groupRestService.getAllGroup(criteria).getContent();

        assertEquals(2, groupForms.size());
        groupAssertEquals(groupForm, groupForms.get(0));
        groupAssertEquals(groupForm2, groupForms.get(1));
    }

    /**
     * Проверка, что фильтрация групп настроек по коду работает корректно
     */
    @Test
    public void getAllGroupByCodeTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm2();
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm3();

        GroupCriteria criteria = new GroupCriteria();
        criteria.setCode("sec");
        List<GroupForm> groupForms = groupRestService.getAllGroup(criteria).getContent();

        assertEquals(2, groupForms.size());
        groupAssertEquals(groupForm, groupForms.get(0));
        groupAssertEquals(groupForm2, groupForms.get(1));
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    public void groupPaginationTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        GroupForm groupForm3 = GroupFormBuilder.buildGroupForm3();

        GroupCriteria criteria = new GroupCriteria();
        criteria.setPageSize(2);
        List<GroupForm> groupForms = groupRestService.getAllGroup(criteria).getContent();

        assertEquals(2, groupForms.size());
        groupAssertEquals(groupForm, groupForms.get(0));
        groupAssertEquals(groupForm2, groupForms.get(1));

        criteria.setPageNumber(1);
        groupForms = groupRestService.getAllGroup(criteria).getContent();

        assertEquals(1, groupForms.size());
        groupAssertEquals(groupForm3, groupForms.get(0));
    }

    /**
     * Проверка, что группа настроек по некоторому заданному коду возвращается корректно
     */
    @Test
    public void getGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();

        groupAssertEquals(groupForm, groupRestService.getGroup(101));
    }

    /**
     * Проверка, что корректно заданная группа настроек успешно сохраняется
     */
    @Test
    public void saveGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildTestGroupForm();
        Integer groupId = groupRestService.saveGroup(groupForm);

        GroupForm savedGroupForm = groupRestService.getGroup(groupId);

        groupAssertEquals(groupForm, savedGroupForm);

        groupRestService.deleteGroup(groupId);
    }

    /**
     * Проверка, что сохранение группы настроек с неуникальным именем приводит к RestException
     */
    @Test(expected = RestException.class)
    public void saveGroupWithAlreadyExistsNameTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        GroupForm testGroupForm = GroupFormBuilder.buildTestGroupForm();
        testGroupForm.setName(groupForm.getName());

        groupRestService.saveGroup(testGroupForm);
    }

    /**
     * Проверка, что сохранение группы настроек с отсутствующим значением кодов приводит к RestException
     */
    @Test(expected = RestException.class)
    public void saveGroupWithEmptyCodesTest() {
        GroupForm testGroupForm = GroupFormBuilder.buildTestGroupForm();
        testGroupForm.setCodes(Arrays.asList());

        groupRestService.saveGroup(testGroupForm);
    }

    /**
     * Проверка, что сохранение группы настроек с неуникальными кодами приводит к RestException
     */
    @Test(expected = RestException.class)
    public void saveGroupWithNotUniqueCodesTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        GroupForm testGroupForm = GroupFormBuilder.buildTestGroupForm();
        testGroupForm.setCodes(groupForm.getCodes());

        groupRestService.saveGroup(testGroupForm);
    }

    /**
     * Проверка, что группа настроек с корректными данными успешно обновляется
     */
    @Test
    public void updateGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildTestGroupForm();
        Integer groupId = groupRestService.saveGroup(groupForm);

        groupForm.setName("test-test");
        groupForm.setCodes(Arrays.asList("test"));

        groupRestService.updateGroup(groupId, groupForm);

        GroupForm savedGroupForm = groupRestService.getGroup(groupId);
        groupAssertEquals(groupForm, savedGroupForm);

        groupRestService.deleteGroup(groupId);
    }

    /**
     * Проверка, что обновление несуществующей группы настроек приводит к RestException
     */
    @Test(expected = RestException.class)
    public void updateNotExistsGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildTestGroupForm();
        groupRestService.updateGroup(0, groupForm);
    }

    /**
     * Проверка, что обновление группы настроек с уже существующем именем приводит к RestException
     */
    @Test(expected = RestException.class)
    public void updateGroupWithNotUniqueNameTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        GroupForm testGroupForm = GroupFormBuilder.buildTestGroupForm();
        Integer groupId = groupRestService.saveGroup(testGroupForm);

        testGroupForm.setName(groupForm.getName());

        try {
            groupRestService.updateGroup(groupId, testGroupForm);
        } finally {
            groupRestService.deleteGroup(groupId);
        }
    }

    /**
     * Проверка, что обновление группы настроек с уже существующими кодами приводит к RestException
     */
    @Test(expected = RestException.class)
    public void updateGroupWithNotUniqueCodesTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        GroupForm testGroupForm = GroupFormBuilder.buildTestGroupForm();
        Integer groupId = groupRestService.saveGroup(testGroupForm);

        testGroupForm.setCodes(groupForm.getCodes());

        try {
            groupRestService.updateGroup(groupId, testGroupForm);
        } finally {
            groupRestService.deleteGroup(groupId);
        }
    }

    /**
     * Проверка, что удаление группы настроек по идентификатору происходит корректно
     */
    @Test(expected = RestException.class)
    public void deleteGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildTestGroupForm();
        Integer groupId = groupRestService.saveGroup(groupForm);

        groupRestService.deleteGroup(groupId);

        groupRestService.getGroup(groupId);
    }

    private void groupAssertEquals(GroupForm expected, GroupForm actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getPriority(), actual.getPriority());
        assertEquals(expected.getCodes().size(), actual.getCodes().size());
        assertTrue(expected.getCodes().containsAll(actual.getCodes()));
    }
}