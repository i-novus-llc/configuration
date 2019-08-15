package ru.i_novus.configuration_service.service;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.platform.test.autoconfigure.DefinePort;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.configuration_api.criteria.FindConfigurationCriteria;
import ru.i_novus.configuration_api.items.ConfigurationResponseItem;
import ru.i_novus.configuration_api.items.GroupResponseItem;
import ru.i_novus.configuration_api.service.ConfigurationGroupRestService;
import ru.i_novus.configuration_api.service.ConfigurationRestService;
import ru.i_novus.configuration_api.service.ConfigurationValueService;
import ru.i_novus.configuration_service.ConfigurationServiceApplication;
import ru.i_novus.configuration_service.service.builders.ConfigurationItemBuilder;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ConfigurationServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableJaxRsProxyClient(
        classes = {
                ConfigurationRestService.class,
                ConfigurationGroupRestService.class
        },
        address = "http://localhost:${server.port}/api"
)
@DefinePort
@EnableEmbeddedPg
public class ConfigurationRestServiceImplTest {

    /**
     * @noinspection SpringJavaInjectionPointsAutowiringInspection
     */
    @Autowired
    @Qualifier("configurationRestServiceJaxRsProxyClient")
    private ConfigurationRestService configurationRestService;

    @Autowired
    @Qualifier("configurationGroupRestServiceJaxRsProxyClient")
    private ConfigurationGroupRestService groupRestService;

    @MockBean
    private ConfigurationValueService configurationValueService;


    @Before
    public void setUp() {
        when(configurationValueService.getValue(any(), any())).thenReturn("test-value");
    }


    /**
     * Проверка, что список настроек возвращается корректно
     */
    @Test
    public void getAllConfigurationsTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationRestService.saveConfiguration(configurationResponseItem);
        ConfigurationResponseItem configurationResponseItem2 = ConfigurationItemBuilder.buildConfigurationItem2();
        configurationRestService.saveConfiguration(configurationResponseItem2);
        ConfigurationResponseItem configurationResponseItem3 = ConfigurationItemBuilder.buildConfigurationItem3();
        configurationRestService.saveConfiguration(configurationResponseItem3);
        GroupResponseItem groupResponseItem = ConfigurationItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);
        GroupResponseItem groupResponseItem2 = ConfigurationItemBuilder.buildGroupItem2();
        Integer groupId2 = groupRestService.saveGroup(groupResponseItem2);

        List<ConfigurationResponseItem> configurationResponseItems =
                configurationRestService.getAllConfigurations(new FindConfigurationCriteria()).getContent();

        assertEquals(3, configurationResponseItems.size());
        assertEquals(configurationResponseItem, configurationResponseItems.get(0));
        assertEquals(configurationResponseItem2, configurationResponseItems.get(1));
        assertEquals(configurationResponseItem3, configurationResponseItems.get(2));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configurationRestService.deleteConfiguration(configurationResponseItem.getCode());
        configurationRestService.deleteConfiguration(configurationResponseItem2.getCode());
        configurationRestService.deleteConfiguration(configurationResponseItem3.getCode());
    }

    /**
     * Проверка, что фильтрация настроек по коду работает корректно
     */
    @Test
    public void getAllConfigurationsByCodeTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationRestService.saveConfiguration(configurationResponseItem);
        ConfigurationResponseItem configurationResponseItem2 = ConfigurationItemBuilder.buildConfigurationItem2();
        configurationRestService.saveConfiguration(configurationResponseItem2);
        ConfigurationResponseItem configurationResponseItem3 = ConfigurationItemBuilder.buildConfigurationItem3();
        configurationRestService.saveConfiguration(configurationResponseItem3);
        GroupResponseItem groupResponseItem = ConfigurationItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);
        GroupResponseItem groupResponseItem2 = ConfigurationItemBuilder.buildGroupItem2();
        Integer groupId2 = groupRestService.saveGroup(groupResponseItem2);

        FindConfigurationCriteria criteria = new FindConfigurationCriteria();
        criteria.setCode("sec");

        List<ConfigurationResponseItem> configurationResponseItems =
                configurationRestService.getAllConfigurations(criteria).getContent();

        assertEquals(2, configurationResponseItems.size());
        assertEquals(configurationResponseItem, configurationResponseItems.get(0));
        assertEquals(configurationResponseItem2, configurationResponseItems.get(1));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configurationRestService.deleteConfiguration(configurationResponseItem.getCode());
        configurationRestService.deleteConfiguration(configurationResponseItem2.getCode());
        configurationRestService.deleteConfiguration(configurationResponseItem3.getCode());
    }

    /**
     * Проверка, что фильтрация настроек по имени работает корректно
     */
    @Test
    public void getAllConfigurationsByNameTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationRestService.saveConfiguration(configurationResponseItem);
        ConfigurationResponseItem configurationResponseItem2 = ConfigurationItemBuilder.buildConfigurationItem2();
        configurationRestService.saveConfiguration(configurationResponseItem2);
        ConfigurationResponseItem configurationResponseItem3 = ConfigurationItemBuilder.buildConfigurationItem3();
        configurationRestService.saveConfiguration(configurationResponseItem3);
        GroupResponseItem groupResponseItem = ConfigurationItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);
        GroupResponseItem groupResponseItem2 = ConfigurationItemBuilder.buildGroupItem2();
        Integer groupId2 = groupRestService.saveGroup(groupResponseItem2);

        FindConfigurationCriteria criteria = new FindConfigurationCriteria();
        criteria.setName("test");

        List<ConfigurationResponseItem> configurationResponseItems =
                configurationRestService.getAllConfigurations(criteria).getContent();

        assertEquals(2, configurationResponseItems.size());
        assertEquals(configurationResponseItem, configurationResponseItems.get(0));
        assertEquals(configurationResponseItem2, configurationResponseItems.get(1));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configurationRestService.deleteConfiguration(configurationResponseItem.getCode());
        configurationRestService.deleteConfiguration(configurationResponseItem2.getCode());
        configurationRestService.deleteConfiguration(configurationResponseItem3.getCode());
    }

    /**
     * Проверка, что фильтрация настроек по именам групп работает корректно
     */
    @Test
    public void getAllConfigurationsByGroupNameTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationRestService.saveConfiguration(configurationResponseItem);
        ConfigurationResponseItem configurationResponseItem2 = ConfigurationItemBuilder.buildConfigurationItem2();
        configurationRestService.saveConfiguration(configurationResponseItem2);
        ConfigurationResponseItem configurationResponseItem3 = ConfigurationItemBuilder.buildConfigurationItem3();
        configurationRestService.saveConfiguration(configurationResponseItem3);
        GroupResponseItem groupResponseItem = ConfigurationItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);
        GroupResponseItem groupResponseItem2 = ConfigurationItemBuilder.buildGroupItem2();
        Integer groupId2 = groupRestService.saveGroup(groupResponseItem2);


        FindConfigurationCriteria criteria = new FindConfigurationCriteria();
        criteria.setGroupNames(Collections.singletonList("Security settings"));

        List<ConfigurationResponseItem> configurationResponseItems =
                configurationRestService.getAllConfigurations(criteria).getContent();

        assertEquals(2, configurationResponseItems.size());
        assertEquals(configurationResponseItem, configurationResponseItems.get(0));
        assertEquals(configurationResponseItem2, configurationResponseItems.get(1));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configurationRestService.deleteConfiguration(configurationResponseItem.getCode());
        configurationRestService.deleteConfiguration(configurationResponseItem2.getCode());
        configurationRestService.deleteConfiguration(configurationResponseItem3.getCode());
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    public void configurationPaginationTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationRestService.saveConfiguration(configurationResponseItem);
        ConfigurationResponseItem configurationResponseItem2 = ConfigurationItemBuilder.buildConfigurationItem2();
        configurationRestService.saveConfiguration(configurationResponseItem2);
        ConfigurationResponseItem configurationResponseItem3 = ConfigurationItemBuilder.buildConfigurationItem3();
        configurationRestService.saveConfiguration(configurationResponseItem3);
        GroupResponseItem groupResponseItem = ConfigurationItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);
        GroupResponseItem groupResponseItem2 = ConfigurationItemBuilder.buildGroupItem2();
        Integer groupId2 = groupRestService.saveGroup(groupResponseItem2);


        FindConfigurationCriteria criteria = new FindConfigurationCriteria();
        criteria.setPageSize(2);

        List<ConfigurationResponseItem> configurationResponseItems =
                configurationRestService.getAllConfigurations(criteria).getContent();

        assertEquals(2, configurationResponseItems.size());
        assertEquals(configurationResponseItem, configurationResponseItems.get(0));
        assertEquals(configurationResponseItem2, configurationResponseItems.get(1));

        criteria.setPageNumber(1);
        configurationResponseItems = configurationRestService.getAllConfigurations(criteria).getContent();

        assertEquals(1, configurationResponseItems.size());
        assertEquals(configurationResponseItem3, configurationResponseItems.get(0));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configurationRestService.deleteConfiguration(configurationResponseItem.getCode());
        configurationRestService.deleteConfiguration(configurationResponseItem2.getCode());
        configurationRestService.deleteConfiguration(configurationResponseItem3.getCode());
    }

    /**
     * Проверка, что настройка успешно сохраняется
     */
    @Test
    public void saveConfigurationTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationRestService.saveConfiguration(configurationResponseItem);
        GroupResponseItem groupResponseItem = ConfigurationItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);

        List<ConfigurationResponseItem> configurationResponseItems =
                configurationRestService.getAllConfigurations(new FindConfigurationCriteria()).getContent();

        assertEquals(1, configurationResponseItems.size());
        assertEquals(configurationResponseItem, configurationResponseItems.get(0));

        groupRestService.deleteGroup(groupId);
        configurationRestService.deleteConfiguration(configurationResponseItem.getCode());
    }

    /**
     * Проверка, что сохранение настройки с уже существующим кодом приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void saveAlreadyExistsConfigurationTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationRestService.saveConfiguration(configurationResponseItem);

        try {
            configurationRestService.saveConfiguration(configurationResponseItem);
        } finally {
            configurationRestService.deleteConfiguration(configurationResponseItem.getCode());
        }
    }

    /**
     * Проверка, что настройка успешно обновляется
     */
    @Test
    public void updateConfigurationMetadataTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationRestService.saveConfiguration(configurationResponseItem);
        GroupResponseItem groupResponseItem = ConfigurationItemBuilder.buildGroupItem1();
        Integer groupId = groupRestService.saveGroup(groupResponseItem);

        configurationResponseItem.setServiceCode("test");
        configurationResponseItem.setDescription("test");
        configurationResponseItem.setValue("test");

        when(configurationValueService.getValue(any(), any())).thenReturn(configurationResponseItem.getValue());

        configurationRestService.updateConfiguration(configurationResponseItem.getCode(), configurationResponseItem);

        assertEquals(configurationResponseItem, configurationRestService.getConfiguration(configurationResponseItem.getCode()));

        groupRestService.deleteGroup(groupId);
        configurationRestService.deleteConfiguration(configurationResponseItem.getCode());
    }

    /**
     * Проверка, что удаление настройки по коду происходит корректно
     */
    @Test
    public void deleteConfigurationTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();

        configurationRestService.saveConfiguration(configurationResponseItem);
        configurationRestService.deleteConfiguration(configurationResponseItem.getCode());

        assertTrue(configurationRestService.getAllConfigurations(new FindConfigurationCriteria()).isEmpty());
    }

    /**
     * Проверка, что удаление настройки по несуществующему коду приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void deleteAlreadyDeletedConfigurationTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();

        configurationRestService.deleteConfiguration(configurationResponseItem.getCode());
    }
}