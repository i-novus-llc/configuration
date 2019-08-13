package ru.i_novus.configuration.configuration_access_service.service.group;

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
import ru.i_novus.configuration.configuration_access_service.ConfigurationAccessServiceApplication;
import ru.i_novus.configuration.configuration_access_service.criteria.FindConfigurationGroupCriteria;
import ru.i_novus.configuration.configuration_access_service.items.ConfigurationGroupResponseItem;

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
        classes = ConfigurationGroupAccessRestService.class,
        address = "http://localhost:${server.port}/api"
)
@DefinePort
@EnableEmbeddedPg
public class ConfigurationGroupAccessRestServiceImplTest {

    /** @noinspection SpringJavaInjectionPointsAutowiringInspection*/
    @Autowired
    @Qualifier("configurationGroupAccessRestServiceJaxRsProxyClient")
    private ConfigurationGroupAccessRestService configurationGroupAccessRestService;

    /**
     * Проверка, что список групп настроек возвращается корректно
     */
    @Test
    public void getAllConfigurationsGroupTest() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        ConfigurationGroupResponseItem configurationGroupResponseItem2 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem2();
        Integer groupId2 = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem2);
        ConfigurationGroupResponseItem configurationGroupResponseItem3 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem3();
        Integer groupId3 = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem3);

        List<ConfigurationGroupResponseItem> allConfigurationsGroup =
                configurationGroupAccessRestService.getAllConfigurationsGroup(new FindConfigurationGroupCriteria()).getContent();

        assertEquals(3, allConfigurationsGroup.size());
        assertEquals(configurationGroupResponseItem, allConfigurationsGroup.get(0));
        assertEquals(configurationGroupResponseItem2, allConfigurationsGroup.get(1));
        assertEquals(configurationGroupResponseItem3, allConfigurationsGroup.get(2));

        configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
        configurationGroupAccessRestService.deleteConfigurationGroup(groupId2);
        configurationGroupAccessRestService.deleteConfigurationGroup(groupId3);
    }

    /**
     * Проверка, что группа настроек по некоторому заданному имени возвращается корректно
     */
    @Test
    public void getConfigurationGroupsByNameTest() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        ConfigurationGroupResponseItem configurationGroupResponseItem2 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem2();
        Integer groupId2 = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem2);
        ConfigurationGroupResponseItem configurationGroupResponseItem3 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem3();
        Integer groupId3 = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem3);

        FindConfigurationGroupCriteria criteria = new FindConfigurationGroupCriteria();
        criteria.setName("security");
        List<ConfigurationGroupResponseItem> configurationsGroup =
                configurationGroupAccessRestService.getAllConfigurationsGroup(criteria).getContent();

        assertEquals(2, configurationsGroup.size());
        assertEquals(configurationGroupResponseItem, configurationsGroup.get(0));
        assertEquals(configurationGroupResponseItem2, configurationsGroup.get(1));

        configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
        configurationGroupAccessRestService.deleteConfigurationGroup(groupId2);
        configurationGroupAccessRestService.deleteConfigurationGroup(groupId3);
    }

    /**
     * Проверка, что группа настроек по некоторому заданному коду возвращается корректно
     */
    @Test
    public void getConfigurationGroupsByCodeTest() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        ConfigurationGroupResponseItem configurationGroupResponseItem2 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem2();
        Integer groupId2 = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem2);
        ConfigurationGroupResponseItem configurationGroupResponseItem3 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem3();
        Integer groupId3 = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem3);

        FindConfigurationGroupCriteria criteria = new FindConfigurationGroupCriteria();
        criteria.setCode("sec");
        List<ConfigurationGroupResponseItem> configurationsGroup =
                configurationGroupAccessRestService.getAllConfigurationsGroup(criteria).getContent();

        assertEquals(2, configurationsGroup.size());
        assertEquals(configurationGroupResponseItem, configurationsGroup.get(0));
        assertEquals(configurationGroupResponseItem2, configurationsGroup.get(1));

        configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
        configurationGroupAccessRestService.deleteConfigurationGroup(groupId2);
        configurationGroupAccessRestService.deleteConfigurationGroup(groupId3);
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    @Ignore
    public void configurationGroupPaginationTest() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        ConfigurationGroupResponseItem configurationGroupResponseItem2 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem2();
        Integer groupId2 = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem2);
        ConfigurationGroupResponseItem configurationGroupResponseItem3 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem3();
        Integer groupId3 = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem3);

        FindConfigurationGroupCriteria criteria = new FindConfigurationGroupCriteria();
        criteria.setPageSize(2);
        List<ConfigurationGroupResponseItem> configurationsGroup =
                configurationGroupAccessRestService.getAllConfigurationsGroup(criteria).getContent();

        assertEquals(2, configurationsGroup.size());
        assertEquals(configurationGroupResponseItem, configurationsGroup.get(0));
        assertEquals(configurationGroupResponseItem, configurationsGroup.get(1));

        criteria.setPageNumber(1);
        configurationsGroup = configurationGroupAccessRestService.getAllConfigurationsGroup(criteria).getContent();

        assertEquals(1, configurationsGroup.size());
        assertEquals(configurationGroupResponseItem2, configurationsGroup.get(0));

        configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
        configurationGroupAccessRestService.deleteConfigurationGroup(groupId2);
        configurationGroupAccessRestService.deleteConfigurationGroup(groupId3);
    }

    /**
     * Проверка, что корректно заданная группа настроек успешно сохраняется
     */
    @Test
    public void saveConfigurationGroupTest() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);

        ConfigurationGroupResponseItem configurationGroup =
                configurationGroupAccessRestService.getAllConfigurationsGroup(new FindConfigurationGroupCriteria()).getContent().get(0);

        assertEquals(configurationGroupResponseItem, configurationGroup);

        configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
    }

    /**
     * Проверка, что сохранение группы настроек с неуникальным именем приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void saveConfigurationGroupWithAlreadyExistsNameTest() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);

        try {
            configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        } finally {
            configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
        }
    }

    /**
     * Проверка, что сохранение группы настроек с отсутствующим значением кодов приводит к RestException
     */
    @Test(expected = RestException.class)
    public void saveConfigurationGroupWithEmptyCodesTest() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        configurationGroupResponseItem.setCodes(new ArrayList<>());

        configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
    }

    /**
     * Проверка, что сохранение группы настроек с неуникальными кодами приводит к BadRequestException
     */
    @Ignore
    @Test(expected = BadRequestException.class)
    public void saveConfigurationGroupWithNotUniqueCodesTest() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        ConfigurationGroupResponseItem configurationGroupResponseItem2 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem2();
        configurationGroupResponseItem2.setCodes(configurationGroupResponseItem.getCodes());

        try {
            configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem2);
        } finally {
            configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
        }
    }

    /**
     * Проверка, что группа настроек с корректными данными успешно обновляется
     */
    @Test
    public void updateConfigurationGroupTest() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);

        ConfigurationGroupResponseItem configurationGroupResponseItem2 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem2();
        configurationGroupAccessRestService.updateConfigurationGroup(groupId, configurationGroupResponseItem2);

        ConfigurationGroupResponseItem configurationGroup =
                configurationGroupAccessRestService.getAllConfigurationsGroup(new FindConfigurationGroupCriteria()).getContent().get(0);
        assertEquals(configurationGroupResponseItem2, configurationGroup);

        configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
    }

    /**
     * Проверка, что обновление несуществующей группы настроек приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void updateNotExistsConfigurationGroupTest() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        configurationGroupAccessRestService.updateConfigurationGroup(0, configurationGroupResponseItem);
    }

    /**
     * Проверка, что обновление группы настроек с уже существующем именем приводит к BadRequestException
     */
    @Ignore
    @Test(expected = BadRequestException.class)
    public void updateConfigurationGroupWithNotUniqueNameTest() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        ConfigurationGroupResponseItem configurationGroupResponseItem2 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem2();
        Integer groupId2 = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem2);

        configurationGroupResponseItem2.setName(configurationGroupResponseItem.getName());

        try {
            configurationGroupAccessRestService.updateConfigurationGroup(groupId2, configurationGroupResponseItem2);
        } finally {
            configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
            configurationGroupAccessRestService.deleteConfigurationGroup(groupId2);
        }
    }

    /**
     * Проверка, что обновление группы настроек с уже существующими кодами приводит к BadRequestException
     */
    @Ignore
    @Test(expected = BadRequestException.class)
    public void updateConfigurationGroupWithNotUniqueCodesTest() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        ConfigurationGroupResponseItem configurationGroupResponseItem2 = ConfigurationGroupItemBuilder.buildConfigurationGroupItem2();
        Integer groupId2 = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem2);

        configurationGroupResponseItem2.setCodes(configurationGroupResponseItem.getCodes());

        try {
            configurationGroupAccessRestService.updateConfigurationGroup(groupId2, configurationGroupResponseItem2);
        } finally {
            configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
            configurationGroupAccessRestService.deleteConfigurationGroup(groupId2);
        }
    }

    /**
     * Проверка, что удаление группы настроек по идентификатору происходит корректно
     */
    @Test
    public void deleteConfigurationGroupTest() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationGroupItemBuilder.buildConfigurationGroupItem1();

        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);
        configurationGroupAccessRestService.deleteConfigurationGroup(groupId);

        assertTrue(configurationGroupAccessRestService.getAllConfigurationsGroup(new FindConfigurationGroupCriteria()).isEmpty());
    }

    /**
     * Проверка, что удаление группы настроек по несуществующему идентификатору приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void deleteNotExistsConfigurationGroupTest() {
        configurationGroupAccessRestService.deleteConfigurationGroup(0);
    }
}