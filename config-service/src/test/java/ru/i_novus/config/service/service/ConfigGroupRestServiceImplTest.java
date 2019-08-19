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
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.config.api.criteria.GroupCriteria;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.api.service.ConfigGroupRestService;
import ru.i_novus.config.service.ConfigServiceApplication;
import ru.i_novus.config.service.service.builders.GroupFormBuilder;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
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
public class ConfigGroupRestServiceImplTest {

    /** @noinspection SpringJavaInjectionPointsAutowiringInspection*/
    @Autowired
    @Qualifier("configGroupRestServiceJaxRsProxyClient")
    private ConfigGroupRestService groupRestService;

    /**
     * Проверка, что список групп настроек возвращается корректно
     */
    @Test
    public void getAllGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);
        GroupForm groupForm3 = GroupFormBuilder.buildGroupForm3();
        Integer groupId3 = groupRestService.saveGroup(groupForm3);

        List<GroupForm> groupForms = groupRestService.getAllGroup(new GroupCriteria()).getContent();

        assertEquals(3, groupForms.size());
        assertTrue(groupForms.containsAll(Arrays.asList(groupForm, groupForm2, groupForm3)));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        groupRestService.deleteGroup(groupId3);
    }

    /**
     * Проверка, что группа настроек по некоторому заданному имени возвращается корректно
     */
    @Test
    public void getGroupsByNameTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);
        GroupForm groupForm3 = GroupFormBuilder.buildGroupForm3();
        Integer groupId3 = groupRestService.saveGroup(groupForm3);

        GroupCriteria criteria = new GroupCriteria();
        criteria.setName("security");
        List<GroupForm> groupForms = groupRestService.getAllGroup(criteria).getContent();

        assertEquals(2, groupForms.size());
        assertTrue(groupForms.containsAll(Arrays.asList(groupForm, groupForm2)));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        groupRestService.deleteGroup(groupId3);
    }

    /**
     * Проверка, что группа настроек по некоторому заданному коду возвращается корректно
     */
    @Test
    public void getGroupsByCodeTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);
        GroupForm groupForm3 = GroupFormBuilder.buildGroupForm3();
        Integer groupId3 = groupRestService.saveGroup(groupForm3);

        GroupCriteria criteria = new GroupCriteria();
        criteria.setCode("sec");
        List<GroupForm> groupForms = groupRestService.getAllGroup(criteria).getContent();

        assertEquals(2, groupForms.size());
        assertTrue(groupForms.containsAll(Arrays.asList(groupForm, groupForm2)));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        groupRestService.deleteGroup(groupId3);
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    public void groupPaginationTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);
        GroupForm groupForm3 = GroupFormBuilder.buildGroupForm3();
        Integer groupId3 = groupRestService.saveGroup(groupForm3);

        GroupCriteria criteria = new GroupCriteria();
        criteria.setPageSize(2);
        List<GroupForm> groupForms = groupRestService.getAllGroup(criteria).getContent();

        assertEquals(2, groupForms.size());

        criteria.setPageNumber(1);
        groupForms = groupRestService.getAllGroup(criteria).getContent();

        assertEquals(1, groupForms.size());

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        groupRestService.deleteGroup(groupId3);
    }

    /**
     * Проверка, что корректно заданная группа настроек успешно сохраняется
     */
    @Test
    public void saveGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);

        GroupForm savedGroupForm = groupRestService.getAllGroup(new GroupCriteria()).getContent().get(0);

        assertEquals(groupForm, savedGroupForm);

        groupRestService.deleteGroup(groupId);
    }

    /**
     * Проверка, что сохранение группы настроек с неуникальным именем приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void saveGroupWithAlreadyExistsNameTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);

        try {
            groupRestService.saveGroup(groupForm);
        } finally {
            groupRestService.deleteGroup(groupId);
        }
    }

    /**
     * Проверка, что сохранение группы настроек с отсутствующим значением кодов приводит к RestException
     */
    @Test(expected = RestException.class)
    public void saveGroupWithEmptyCodesTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        groupForm.setCodes(new ArrayList<>());

        groupRestService.saveGroup(groupForm);
    }

    /**
     * Проверка, что сохранение группы настроек с неуникальными кодами приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void saveGroupWithNotUniqueCodesTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        groupForm2.setCodes(groupForm.getCodes());

        try {
            groupRestService.saveGroup(groupForm2);
        } finally {
            groupRestService.deleteGroup(groupId);
        }
    }

    /**
     * Проверка, что группа настроек с корректными данными успешно обновляется
     */
    @Test
    public void updateGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);

        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        groupRestService.updateGroup(groupId, groupForm2);

        GroupForm savedGroupForm = groupRestService.getAllGroup(new GroupCriteria()).getContent().get(0);
        assertEquals(groupForm2, savedGroupForm);

        groupRestService.deleteGroup(groupId);
    }

    /**
     * Проверка, что обновление несуществующей группы настроек приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void updateNotExistsGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        groupRestService.updateGroup(0, groupForm);
    }

    /**
     * Проверка, что обновление группы настроек с уже существующем именем приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void updateGroupWithNotUniqueNameTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);

        groupForm2.setName(groupForm.getName());

        try {
            groupRestService.updateGroup(groupId2, groupForm2);
        } finally {
            groupRestService.deleteGroup(groupId);
            groupRestService.deleteGroup(groupId2);
        }
    }

    /**
     * Проверка, что обновление группы настроек с уже существующими кодами приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void updateGroupWithNotUniqueCodesTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);

        groupForm2.setCodes(groupForm.getCodes());

        try {
            groupRestService.updateGroup(groupId2, groupForm2);
        } finally {
            groupRestService.deleteGroup(groupId);
            groupRestService.deleteGroup(groupId2);
        }
    }

    /**
     * Проверка, что удаление группы настроек по идентификатору происходит корректно
     */
    @Test
    public void deleteGroupTest() {
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();

        Integer groupId = groupRestService.saveGroup(groupForm);
        groupRestService.deleteGroup(groupId);

        assertTrue(groupRestService.getAllGroup(new GroupCriteria()).isEmpty());
    }

    /**
     * Проверка, что удаление группы настроек по несуществующему идентификатору приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void deleteNotExistsGroupTest() {
        groupRestService.deleteGroup(0);
    }
}