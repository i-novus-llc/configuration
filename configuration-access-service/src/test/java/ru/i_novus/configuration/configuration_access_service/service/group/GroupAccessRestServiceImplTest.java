package ru.i_novus.configuration.configuration_access_service.service.group;

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
import ru.i_novus.configuration.configuration_access_service.ConfigurationAccessServiceApplication;
import ru.i_novus.configuration.configuration_access_service.criteria.FindConfigurationGroupsCriteria;
import ru.i_novus.configuration.configuration_access_service.entity.group.ConfigurationGroupEntity;
import ru.i_novus.configuration.configuration_access_service.entity.group.ConfigurationGroupResponseItem;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ConfigurationAccessServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableJaxRsProxyClient(
        classes = GroupAccessRestService.class,
        address = "http://localhost:${server.port}/api"
)
@DefinePort
@EnableEmbeddedPg
public class GroupAccessRestServiceImplTest {

    /** @noinspection SpringJavaInjectionPointsAutowiringInspection*/
    @Autowired
    @Qualifier("groupAccessRestServiceJaxRsProxyClient")
    private GroupAccessRestService groupAccessRestService;

    /**
     * Проверка, что список групп настроек возвращается корректно
     */
    @Test
    public void getAllConfigurationsGroup() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        ConfigurationGroupResponseItem configurationGroupResponseItem2 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem2();
        Integer groupId2 = groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem2);

        List<ConfigurationGroupEntity> allConfigurationsGroup =
                groupAccessRestService.getConfigurationsGroup(new FindConfigurationGroupsCriteria()).getContent();

        assertEquals(2, allConfigurationsGroup.size());
        assertEquals(groupId, allConfigurationsGroup.get(0).getId());
        assertEquals(configurationGroupResponseItem.getName(), allConfigurationsGroup.get(0).getName());
        assertEquals(configurationGroupResponseItem.getDescription(), allConfigurationsGroup.get(0).getDescription());

        assertEquals(groupId2, allConfigurationsGroup.get(1).getId());
        assertEquals(configurationGroupResponseItem2.getName(), allConfigurationsGroup.get(1).getName());
        assertEquals(configurationGroupResponseItem2.getDescription(), allConfigurationsGroup.get(1).getDescription());

        groupAccessRestService.deleteConfigurationGroup(groupId);
        groupAccessRestService.deleteConfigurationGroup(groupId2);
    }

    /**
     * Проверка, что группа настроек по некоторому заданному имени возвращается корректно
     */
    @Test
    public void getConfigurationGroupsByName() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        ConfigurationGroupResponseItem configurationGroupResponseItem2 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem2();
        Integer groupId2 = groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem2);
        ConfigurationGroupResponseItem configurationGroupResponseItem3 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem3();
        Integer groupId3 = groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem3);

        FindConfigurationGroupsCriteria criteria = new FindConfigurationGroupsCriteria();
        criteria.setGroupName("group");
        List<ConfigurationGroupEntity> configurationsGroup = groupAccessRestService.getConfigurationsGroup(criteria).getContent();

        assertEquals(2, configurationsGroup.size());
        assertEquals(groupId, configurationsGroup.get(0).getId());
        assertEquals(configurationGroupResponseItem.getName(), configurationsGroup.get(0).getName());
        assertEquals(configurationGroupResponseItem.getDescription(), configurationsGroup.get(0).getDescription());

        assertEquals(groupId2, configurationsGroup.get(1).getId());
        assertEquals(configurationGroupResponseItem2.getName(), configurationsGroup.get(1).getName());
        assertEquals(configurationGroupResponseItem2.getDescription(), configurationsGroup.get(1).getDescription());

        groupAccessRestService.deleteConfigurationGroup(groupId);
        groupAccessRestService.deleteConfigurationGroup(groupId2);
        groupAccessRestService.deleteConfigurationGroup(groupId3);
    }

//    /**
//     * Проверка, что группа настроек по некоторому заданному коду возвращается корректно
//     */
//    @Test
//    public void getConfigurationGroupsByCode() {
//
//    }

    /**
     * Проверка, что корректно заданная группа настроек успешно сохраняется
     */
    @Test
    public void saveConfigurationGroup() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);

        List<ConfigurationGroupEntity> configurationGroup =
                groupAccessRestService.getConfigurationsGroup(new FindConfigurationGroupsCriteria()).getContent();

        assertEquals(1, configurationGroup.size());
        assertEquals(groupId, configurationGroup.get(0).getId());
        assertEquals(configurationGroupResponseItem.getName(), configurationGroup.get(0).getName());
        assertEquals(configurationGroupResponseItem.getDescription(), configurationGroup.get(0).getDescription());

        groupAccessRestService.deleteConfigurationGroup(groupId);
    }

    /**
     * Проверка, что сохранение группы настроек с неуникальным именем приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void saveConfigurationGroupWithAlreadyExistsName() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);

        try {
            groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        } finally {
            groupAccessRestService.deleteConfigurationGroup(groupId);
        }
    }

    /**
     * Проверка, что сохранение группы настроек с отсутствующим значением кодов приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void saveConfigurationGroupWithEmptyCodes() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        configurationGroupResponseItem.setCodes(new ArrayList<>());

        groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
    }

    /**
     * Проверка, что сохранение группы настроек с неуникальными кодами приводит к BadRequestException
     */
    @Ignore
    @Test(expected = BadRequestException.class)
    public void saveConfigurationGroupWithNotUniqueCodes() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        ConfigurationGroupResponseItem configurationGroupResponseItem2 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem2();
        configurationGroupResponseItem2.setCodes(configurationGroupResponseItem.getCodes());

        try {
            groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem2);
        } finally {
            groupAccessRestService.deleteConfigurationGroup(groupId);
        }

    }

    /**
     * Проверка, что группа настроек с корректными данными успешно обновляется
     */
    @Test
    public void updateConfigurationGroup() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);

        ConfigurationGroupResponseItem configurationGroupResponseItem2 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem2();
        groupAccessRestService.updateConfigurationGroup(groupId, configurationGroupResponseItem2);

        ConfigurationGroupEntity configurationGroup = groupAccessRestService.getConfigurationsGroup(new FindConfigurationGroupsCriteria()).getContent().get(0);
        assertEquals(groupId, configurationGroup.getId());
        assertEquals(configurationGroupResponseItem2.getName(), configurationGroup.getName());
        assertEquals(configurationGroupResponseItem2.getDescription(), configurationGroup.getDescription());

        groupAccessRestService.deleteConfigurationGroup(groupId);
    }

    /**
     * Проверка, что обновление несуществующей группы настроек приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void updateNotExistsConfigurationGroup() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        groupAccessRestService.updateConfigurationGroup(0, configurationGroupResponseItem);
    }

    /**
     * Проверка, что обновление группы настроек с уже существующем именем приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void updateConfigurationGroupWithNotUniqueName() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        ConfigurationGroupResponseItem configurationGroupResponseItem2 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem2();
        Integer groupId2 = groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem2);

        configurationGroupResponseItem2.setName(configurationGroupResponseItem.getName());

        try {
            groupAccessRestService.updateConfigurationGroup(groupId2, configurationGroupResponseItem2);
        } finally {
            groupAccessRestService.deleteConfigurationGroup(groupId);
            groupAccessRestService.deleteConfigurationGroup(groupId2);
        }
    }

    /**
     * Проверка, что удаление группы настроек по идентификатору происходит корректно
     */
    @Test
    public void deleteConfigurationGroup() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();

        Integer groupId = groupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        groupAccessRestService.deleteConfigurationGroup(groupId);

        assertTrue(groupAccessRestService.getConfigurationsGroup(new FindConfigurationGroupsCriteria()).isEmpty());
    }

    /**
     * Проверка, что удаление группы настроек по несуществующему идентификатору приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void deleteNotExistsConfigurationGroup() {
        groupAccessRestService.deleteConfigurationGroup(0);
    }
}