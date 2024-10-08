package ru.i_novus.configuration.config.service;

import jakarta.ws.rs.NotFoundException;
import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.platform.test.autoconfigure.pg.EnableTestcontainersPg;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.i_novus.TestApp;
import ru.i_novus.config.api.criteria.GroupCriteria;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.api.service.ConfigGroupRestService;
import ru.i_novus.configuration.config.service.builders.GroupFormBuilder;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableTestcontainersPg
public class ConfigGroupRestServiceImplTest {

    @Autowired
    private ConfigGroupRestService groupRestService;

    /**
     * Проверка, что список групп настроек возвращается корректно
     */
    @Test
    public void getAllGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        GroupForm groupForm3 = GroupFormBuilder.buildGroupForm3();

        List<GroupForm> groupForms = groupRestService.getAllGroup(new GroupCriteria()).getContent();

        assertEquals(4, groupForms.size());
        groupAssertEquals(groupForm, groupForms.get(1));
        groupAssertEquals(groupForm2, groupForms.get(2));
        groupAssertEquals(groupForm3, groupForms.get(3));
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
        criteria.setPageSize(3);
        List<GroupForm> groupForms = groupRestService.getAllGroup(criteria).getContent();

        assertEquals(3, groupForms.size());
        groupAssertEquals(groupForm, groupForms.get(1));
        groupAssertEquals(groupForm2, groupForms.get(2));

        criteria.setPageNumber(1);
        groupForms = groupRestService.getAllGroup(criteria).getContent();

        assertEquals(1, groupForms.size());
        groupAssertEquals(groupForm3, groupForms.get(0));
    }

    /**
     * Проверка, что группа настроек по некоторому заданному идентификатору возвращается корректно
     */
    @Test
    public void getGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();

        groupAssertEquals(groupForm, groupRestService.getGroup(101));
    }

    /**
     * Проверка, что получение группы настроек по несуществующему идентификатору приводит к NotFoundException
     */
    @Test
    public void getGroupByNotExistsCodeTest() {
        assertThrows(NotFoundException.class, () -> groupRestService.getGroup(999));
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
     * Проверка, что сохранение группы настроек с неуникальным именем приводит к UserException
     */
    @Test
    public void saveGroupWithAlreadyExistsNameTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        GroupForm testGroupForm = GroupFormBuilder.buildTestGroupForm();
        testGroupForm.setName(groupForm.getName());

        assertThrows(UserException.class, () -> groupRestService.saveGroup(testGroupForm));
    }

    /**
     * Проверка, что сохранение группы настроек с неуникальными кодами приводит к UserException
     */
    @Test
    public void saveGroupWithNotUniqueCodesTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        GroupForm testGroupForm = GroupFormBuilder.buildTestGroupForm();
        testGroupForm.setCodes(groupForm.getCodes());

        assertThrows(UserException.class, () -> groupRestService.saveGroup(testGroupForm));
    }

    /**
     * Проверка, что группа настроек с корректными данными успешно обновляется
     */
    @Test
    public void updateGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildTestGroupForm();
        Integer groupId = groupRestService.saveGroup(groupForm);

        groupForm.setName("test-test");
        groupForm.setCodes(Set.of("test"));

        groupRestService.updateGroup(groupId, groupForm);

        GroupForm savedGroupForm = groupRestService.getGroup(groupId);
        groupAssertEquals(groupForm, savedGroupForm);

        groupRestService.deleteGroup(groupId);
    }

    /**
     * Проверка, что обновление группы настроек с несуществующим идентификатором приводит к NotFoundException
     */
    @Test
    public void updateNotExistsGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildTestGroupForm();
        groupForm.setId(999);
        assertThrows(NotFoundException.class, () -> groupRestService.updateGroup(groupForm.getId(), groupForm));
    }

    /**
     * Проверка, что обновление группы настроек с уже существующем именем приводит к UserException
     */
    @Test
    public void updateGroupWithNotUniqueNameTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        GroupForm testGroupForm = GroupFormBuilder.buildTestGroupForm();
        Integer groupId = groupRestService.saveGroup(testGroupForm);

        testGroupForm.setName(groupForm.getName());

        try {
            assertThrows(UserException.class, () -> groupRestService.updateGroup(groupId, testGroupForm));
        } finally {
            groupRestService.deleteGroup(groupId);
        }
    }

    /**
     * Проверка, что обновление группы настроек с уже существующими кодами приводит к UserException
     */
    @Test
    public void updateGroupWithNotUniqueCodesTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        GroupForm testGroupForm = GroupFormBuilder.buildTestGroupForm();
        Integer groupId = groupRestService.saveGroup(testGroupForm);

        testGroupForm.setCodes(groupForm.getCodes());

        try {
            assertThrows(UserException.class, () -> groupRestService.updateGroup(groupId, testGroupForm));
        } finally {
            groupRestService.deleteGroup(groupId);
        }
    }

    /**
     * Проверка, что удаление группы настроек по идентификатору происходит корректно
     */
    @Test
    public void deleteGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildTestGroupForm();
        Integer groupId = groupRestService.saveGroup(groupForm);

        groupRestService.deleteGroup(groupId);

        assertThrows(NotFoundException.class, () -> groupRestService.getGroup(groupId));
    }

    /**
     * Проверка, что удаление группы настроек по несуществующему идентификатору приводит к NotFoundException
     */
    @Test
    public void deleteGroupByNotExistsCodeTest() {
        assertThrows(NotFoundException.class, () -> groupRestService.deleteGroup(999));
    }

    private void groupAssertEquals(GroupForm expected, GroupForm actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getPriority(), actual.getPriority());
        assertEquals(expected.getCodes().size(), actual.getCodes().size());
        assertTrue(expected.getCodes().containsAll(actual.getCodes()));
    }
}