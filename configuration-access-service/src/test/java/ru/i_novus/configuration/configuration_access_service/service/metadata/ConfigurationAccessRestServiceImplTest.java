package ru.i_novus.configuration.configuration_access_service.service.metadata;

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
import ru.i_novus.configuration.configuration_access_service.criteria.FindConfigurationCriteria;
import ru.i_novus.configuration.configuration_access_service.items.ConfigurationGroupResponseItem;
import ru.i_novus.configuration.configuration_access_service.items.ConfigurationResponseItem;
import ru.i_novus.configuration.configuration_access_service.service.group.ConfigurationGroupAccessRestService;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ConfigurationAccessServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableJaxRsProxyClient(
        classes = {
                ConfigurationAccessRestService.class,
                ConfigurationGroupAccessRestService.class
        },
        address = "http://localhost:${server.port}/api"
)
@DefinePort
@EnableEmbeddedPg
public class ConfigurationAccessRestServiceImplTest {

    /**
     * @noinspection SpringJavaInjectionPointsAutowiringInspection
     */
    @Autowired
    @Qualifier("configurationAccessRestServiceJaxRsProxyClient")
    private ConfigurationAccessRestService configurationAccessRestService;

    @Autowired
    @Qualifier("configurationGroupAccessRestServiceJaxRsProxyClient")
    private ConfigurationGroupAccessRestService configurationGroupAccessRestService;

    /// TODO - добавить мок на консул

    /**
     * Проверка, что список настроек возвращается корректно
     */
    @Test
    public void getAllConfigurationsTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationAccessRestService.saveConfiguration(configurationResponseItem);
        ConfigurationResponseItem configurationResponseItem2 = ConfigurationItemBuilder.buildConfigurationItem2();
        configurationAccessRestService.saveConfiguration(configurationResponseItem2);
        ConfigurationResponseItem configurationResponseItem3 = ConfigurationItemBuilder.buildConfigurationItem3();
        configurationAccessRestService.saveConfiguration(configurationResponseItem3);
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationItemBuilder.buildConfigurationGroupItem();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);

        List<ConfigurationResponseItem> configurationResponseItems =
                configurationAccessRestService.getAllConfigurations(new FindConfigurationCriteria()).getContent();

        try {
            assertEquals(3, configurationResponseItems.size());
            assertEquals(configurationResponseItem, configurationResponseItems.get(0));
            assertEquals(configurationResponseItem2, configurationResponseItems.get(1));
            assertEquals(configurationResponseItem3, configurationResponseItems.get(2));
        } finally {
            configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
            configurationAccessRestService.deleteConfiguration(configurationResponseItem.getCode());
            configurationAccessRestService.deleteConfiguration(configurationResponseItem2.getCode());
            configurationAccessRestService.deleteConfiguration(configurationResponseItem3.getCode());
        }
    }

    /**
     * Проверка, что фильтрация настроек по коду работает корректно
     */
    @Test
    public void getAllConfigurationsByCode() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationAccessRestService.saveConfiguration(configurationResponseItem);
        ConfigurationResponseItem configurationResponseItem2 = ConfigurationItemBuilder.buildConfigurationItem2();
        configurationAccessRestService.saveConfiguration(configurationResponseItem2);
        ConfigurationResponseItem configurationResponseItem3 = ConfigurationItemBuilder.buildConfigurationItem3();
        configurationAccessRestService.saveConfiguration(configurationResponseItem3);
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationItemBuilder.buildConfigurationGroupItem();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);

        FindConfigurationCriteria criteria = new FindConfigurationCriteria();
        criteria.setCode("sec");

        List<ConfigurationResponseItem> allConfigurationsMetadata =
                configurationAccessRestService.getAllConfigurations(criteria).getContent();
        try {
            assertEquals(2, allConfigurationsMetadata.size());
            assertEquals(configurationResponseItem, allConfigurationsMetadata.get(0));
            assertEquals(configurationResponseItem2, allConfigurationsMetadata.get(1));
        } finally {
            configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
            configurationAccessRestService.deleteConfiguration(configurationResponseItem.getCode());
            configurationAccessRestService.deleteConfiguration(configurationResponseItem2.getCode());
            configurationAccessRestService.deleteConfiguration(configurationResponseItem3.getCode());
        }
    }

    /**
     * Проверка, что фильтрация настроек по имени работает корректно
     */
    @Test
    public void getAllConfigurationsByName() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationAccessRestService.saveConfiguration(configurationResponseItem);
        ConfigurationResponseItem configurationResponseItem2 = ConfigurationItemBuilder.buildConfigurationItem2();
        configurationAccessRestService.saveConfiguration(configurationResponseItem2);
        ConfigurationResponseItem configurationResponseItem3 = ConfigurationItemBuilder.buildConfigurationItem3();
        configurationAccessRestService.saveConfiguration(configurationResponseItem3);
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationItemBuilder.buildConfigurationGroupItem();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);

        FindConfigurationCriteria criteria = new FindConfigurationCriteria();
        criteria.setName("test");

        List<ConfigurationResponseItem> allConfigurationsMetadata =
                configurationAccessRestService.getAllConfigurations(criteria).getContent();

        try {
            assertEquals(2, allConfigurationsMetadata.size());
            assertEquals(configurationResponseItem, allConfigurationsMetadata.get(0));
            assertEquals(configurationResponseItem2, allConfigurationsMetadata.get(1));
        } finally {
            configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
            configurationAccessRestService.deleteConfiguration(configurationResponseItem.getCode());
            configurationAccessRestService.deleteConfiguration(configurationResponseItem2.getCode());
            configurationAccessRestService.deleteConfiguration(configurationResponseItem3.getCode());
        }
    }

    /**
     * Проверка, что фильтрация настроек по именам групп работает корректно
     */
    @Test
    @Ignore
    public void getAllConfigurationsByGroupName() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationAccessRestService.saveConfiguration(configurationResponseItem);
        ConfigurationResponseItem configurationResponseItem2 = ConfigurationItemBuilder.buildConfigurationItem2();
        configurationAccessRestService.saveConfiguration(configurationResponseItem2);
        ConfigurationResponseItem configurationResponseItem3 = ConfigurationItemBuilder.buildConfigurationItem3();
        configurationAccessRestService.saveConfiguration(configurationResponseItem3);
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationItemBuilder.buildConfigurationGroupItem();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);

        FindConfigurationCriteria criteria = new FindConfigurationCriteria();
        criteria.setGroupNames(Arrays.asList("test"));

        List<ConfigurationResponseItem> allConfigurationsMetadata =
                configurationAccessRestService.getAllConfigurations(criteria).getContent();

        try {
            assertEquals(2, allConfigurationsMetadata.size());
            assertEquals(configurationResponseItem, allConfigurationsMetadata.get(0));
            assertEquals(configurationResponseItem2, allConfigurationsMetadata.get(1));
        } finally {
            configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
            configurationAccessRestService.deleteConfiguration(configurationResponseItem.getCode());
            configurationAccessRestService.deleteConfiguration(configurationResponseItem2.getCode());
            configurationAccessRestService.deleteConfiguration(configurationResponseItem3.getCode());
        }
    }

    /**
     * Проверка, что настройка успешно сохраняется
     */
    @Test
    public void saveConfigurationTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationAccessRestService.saveConfiguration(configurationResponseItem);
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationItemBuilder.buildConfigurationGroupItem();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);

        List<ConfigurationResponseItem> configurationsMetadata =
                configurationAccessRestService.getAllConfigurations(new FindConfigurationCriteria()).getContent();

        try {
            assertEquals(1, configurationsMetadata.size());
            assertEquals(configurationResponseItem, configurationsMetadata.get(0));
        } finally {
            configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
            configurationAccessRestService.deleteConfiguration(configurationResponseItem.getCode());
        }
    }

    /**
     * Проверка, что сохранение настройки с уже существующим кодом приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void saveAlreadyExistsConfigurationTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationAccessRestService.saveConfiguration(configurationResponseItem);

        try {
            configurationAccessRestService.saveConfiguration(configurationResponseItem);
        } finally {
            configurationAccessRestService.deleteConfiguration(configurationResponseItem.getCode());
        }
    }

    /**
     * Проверка, что настройка успешно обновляется
     */
    @Test
    public void updateConfigurationMetadataTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();
        configurationAccessRestService.saveConfiguration(configurationResponseItem);
        ConfigurationGroupResponseItem configurationGroupResponseItem = ConfigurationItemBuilder.buildConfigurationGroupItem();
        Integer groupId = configurationGroupAccessRestService.saveConfigurationGroup(configurationGroupResponseItem);

        configurationResponseItem.setServiceCode("test");
        configurationResponseItem.setDescription("test");
        configurationResponseItem.setValue("test");

        configurationAccessRestService.updateConfiguration(configurationResponseItem.getCode(), configurationResponseItem);

        try {
            assertEquals(configurationResponseItem, configurationAccessRestService.getConfiguration(configurationResponseItem.getCode()));
        } finally {
            configurationGroupAccessRestService.deleteConfigurationGroup(groupId);
            configurationAccessRestService.deleteConfiguration(configurationResponseItem.getCode());
        }
    }

    /**
     * Проверка, что удаление настройки по коду происходит корректно
     */
    @Test
    public void deleteConfigurationTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();

        configurationAccessRestService.saveConfiguration(configurationResponseItem);
        configurationAccessRestService.deleteConfiguration(configurationResponseItem.getCode());

        assertTrue(configurationAccessRestService.getAllConfigurations(new FindConfigurationCriteria()).isEmpty());
    }

    /**
     * Проверка, что удаление настройки по несуществующему коду приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void deleteAlreadyDeletedConfigurationTest() {
        ConfigurationResponseItem configurationResponseItem = ConfigurationItemBuilder.buildConfigurationItem1();

        configurationAccessRestService.deleteConfiguration(configurationResponseItem.getCode());
    }
}