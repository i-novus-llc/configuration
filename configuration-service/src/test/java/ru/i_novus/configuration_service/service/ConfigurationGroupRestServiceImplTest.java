package ru.i_novus.configuration_service.service;

import net.n2oapp.platform.jaxrs.RestException;
import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.platform.test.autoconfigure.DefinePort;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.configuration_api.criteria.FindGroupCriteria;
import ru.i_novus.configuration_api.items.GroupResponseItem;
import ru.i_novus.configuration_api.service.ConfigurationGroupRestService;
import ru.i_novus.configuration_service.ConfigurationServiceApplication;
import ru.i_novus.configuration_service.service.builders.GroupItemBuilder;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ConfigurationServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableJaxRsProxyClient(
        classes = ConfigurationGroupRestService.class,
        address = "http://localhost:${server.port}/api"
)
@DefinePort
@EnableEmbeddedPg
public class ConfigurationGroupRestServiceImplTest {

    /** @noinspection SpringJavaInjectionPointsAutowiringInspection*/
    @Autowired
    @Qualifier("configurationGroupRestServiceJaxRsProxyClient")
    private ConfigurationGroupRestService groupRestService;

    /**
     * Проверка, что список групп настроек возвращается корректно
     */
    @Test
    public void getAllGroupTest() {
        GroupResponseItem groupResponseItem = GroupItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);
        GroupResponseItem groupResponseItem2 = GroupItemBuilder.buildGroupItem2();
        Integer groupId2 = groupRestService.saveGroup(groupResponseItem2);
        GroupResponseItem groupResponseItem3 = GroupItemBuilder.buildGroupItem3();
        Integer groupId3 = groupRestService.saveGroup(groupResponseItem3);

        List<GroupResponseItem> groupResponseItems = groupRestService.getAllGroup(new FindGroupCriteria()).getContent();

        assertEquals(3, groupResponseItems.size());
        assertEquals(groupResponseItem, groupResponseItems.get(0));
        assertEquals(groupResponseItem2, groupResponseItems.get(1));
        assertEquals(groupResponseItem3, groupResponseItems.get(2));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        groupRestService.deleteGroup(groupId3);
    }

    /**
     * Проверка, что группа настроек по некоторому заданному имени возвращается корректно
     */
    @Test
    public void getGroupsByNameTest() {
        GroupResponseItem groupResponseItem = GroupItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);
        GroupResponseItem groupResponseItem2 = GroupItemBuilder.buildGroupItem2();
        Integer groupId2 = groupRestService.saveGroup(groupResponseItem2);
        GroupResponseItem groupResponseItem3 = GroupItemBuilder.buildGroupItem3();
        Integer groupId3 = groupRestService.saveGroup(groupResponseItem3);

        FindGroupCriteria criteria = new FindGroupCriteria();
        criteria.setName("security");
        List<GroupResponseItem> groupResponseItems = groupRestService.getAllGroup(criteria).getContent();

        assertEquals(2, groupResponseItems.size());
        assertEquals(groupResponseItem, groupResponseItems.get(0));
        assertEquals(groupResponseItem2, groupResponseItems.get(1));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        groupRestService.deleteGroup(groupId3);
    }

    /**
     * Проверка, что группа настроек по некоторому заданному коду возвращается корректно
     */
    @Test
    public void getGroupsByCodeTest() {
        GroupResponseItem groupResponseItem = GroupItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);
        GroupResponseItem groupResponseItem2 = GroupItemBuilder.buildGroupItem2();
        Integer groupId2 = groupRestService.saveGroup(groupResponseItem2);
        GroupResponseItem groupResponseItem3 = GroupItemBuilder.buildGroupItem3();
        Integer groupId3 = groupRestService.saveGroup(groupResponseItem3);

        FindGroupCriteria criteria = new FindGroupCriteria();
        criteria.setCode("sec");
        List<GroupResponseItem> groupResponseItems = groupRestService.getAllGroup(criteria).getContent();

        assertEquals(2, groupResponseItems.size());
        assertEquals(groupResponseItem, groupResponseItems.get(0));
        assertEquals(groupResponseItem2, groupResponseItems.get(1));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        groupRestService.deleteGroup(groupId3);
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    @Ignore
    public void groupPaginationTest() {
        GroupResponseItem groupResponseItem = GroupItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);
        GroupResponseItem groupResponseItem2 = GroupItemBuilder.buildGroupItem2();
        Integer groupId2 = groupRestService.saveGroup(groupResponseItem2);
        GroupResponseItem groupResponseItem3 = GroupItemBuilder.buildGroupItem3();
        Integer groupId3 = groupRestService.saveGroup(groupResponseItem3);

        FindGroupCriteria criteria = new FindGroupCriteria();
        criteria.setPageSize(2);
        List<GroupResponseItem> groupResponseItems = groupRestService.getAllGroup(criteria).getContent();

        assertEquals(2, groupResponseItems.size());
        assertEquals(groupResponseItem, groupResponseItems.get(0));
        assertEquals(groupResponseItem, groupResponseItems.get(1));

        criteria.setPageNumber(1);
        groupResponseItems = groupRestService.getAllGroup(criteria).getContent();

        assertEquals(1, groupResponseItems.size());
        assertEquals(groupResponseItem2, groupResponseItems.get(0));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        groupRestService.deleteGroup(groupId3);
    }

    /**
     * Проверка, что корректно заданная группа настроек успешно сохраняется
     */
    @Test
    public void saveGroupTest() {
        GroupResponseItem groupResponseItem = GroupItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);

        GroupResponseItem responseItem = groupRestService.getAllGroup(new FindGroupCriteria()).getContent().get(0);

        assertEquals(groupResponseItem, responseItem);

        groupRestService.deleteGroup(groupId);
    }

    /**
     * Проверка, что сохранение группы настроек с неуникальным именем приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void saveGroupWithAlreadyExistsNameTest() {
        GroupResponseItem groupResponseItem = GroupItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);

        try {
            groupRestService.saveGroup(groupResponseItem);
        } finally {
            groupRestService.deleteGroup(groupId);
        }
    }

    /**
     * Проверка, что сохранение группы настроек с отсутствующим значением кодов приводит к RestException
     */
    @Test(expected = RestException.class)
    public void saveGroupWithEmptyCodesTest() {
        GroupResponseItem groupResponseItem = GroupItemBuilder.buildGroupItem1();
        groupResponseItem.setCodes(new ArrayList<>());

        groupRestService.saveGroup(groupResponseItem);
    }

    /**
     * Проверка, что сохранение группы настроек с неуникальными кодами приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void saveGroupWithNotUniqueCodesTest() {
        GroupResponseItem groupResponseItem = GroupItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);
        GroupResponseItem groupResponseItem2 = GroupItemBuilder.buildGroupItem2();
        groupResponseItem2.setCodes(groupResponseItem.getCodes());

        try {
            groupRestService.saveGroup(groupResponseItem2);
        } finally {
            groupRestService.deleteGroup(groupId);
        }
    }

    /**
     * Проверка, что группа настроек с корректными данными успешно обновляется
     */
    @Test
    public void updateGroupTest() {
        GroupResponseItem groupResponseItem = GroupItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);

        GroupResponseItem groupResponseItem2 = GroupItemBuilder.buildGroupItem2();
        groupRestService.updateGroup(groupId, groupResponseItem2);

        GroupResponseItem responseItem = groupRestService.getAllGroup(new FindGroupCriteria()).getContent().get(0);
        assertEquals(groupResponseItem2, responseItem);

        groupRestService.deleteGroup(groupId);
    }

    /**
     * Проверка, что обновление несуществующей группы настроек приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void updateNotExistsGroupTest() {
        GroupResponseItem groupResponseItem = GroupItemBuilder.buildGroupItem1();
        groupRestService.updateGroup(0, groupResponseItem);
    }

    /**
     * Проверка, что обновление группы настроек с уже существующем именем приводит к BadRequestException
     */
    @Ignore
    @Test(expected = BadRequestException.class)
    public void updateGroupWithNotUniqueNameTest() {
        GroupResponseItem groupResponseItem = GroupItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);
        GroupResponseItem groupResponseItem2 = GroupItemBuilder.buildGroupItem2();
        Integer groupId2 = groupRestService.saveGroup(groupResponseItem2);

        groupResponseItem2.setName(groupResponseItem.getName());

        try {
            groupRestService.updateGroup(groupId2, groupResponseItem2);
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
        GroupResponseItem groupResponseItem = GroupItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);
        GroupResponseItem groupResponseItem2 = GroupItemBuilder.buildGroupItem2();
        Integer groupId2 = groupRestService.saveGroup(groupResponseItem2);

        groupResponseItem2.setCodes(groupResponseItem.getCodes());

        try {
            groupRestService.updateGroup(groupId2, groupResponseItem2);
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
        GroupResponseItem groupResponseItem = GroupItemBuilder.buildGroupItem1();

        Integer groupId = groupRestService.saveGroup(groupResponseItem);
        groupRestService.deleteGroup(groupId);

        assertTrue(groupRestService.getAllGroup(new FindGroupCriteria()).isEmpty());
    }

    /**
     * Проверка, что удаление группы настроек по несуществующему идентификатору приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void deleteNotExistsGroupTest() {
        groupRestService.deleteGroup(0);
    }
}