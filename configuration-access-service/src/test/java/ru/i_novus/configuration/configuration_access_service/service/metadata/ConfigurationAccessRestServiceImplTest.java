package ru.i_novus.configuration.configuration_access_service.service.metadata;

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
import ru.i_novus.configuration.configuration_access_service.ConfigurationAccessServiceApplication;
import ru.i_novus.configuration.configuration_access_service.entity.metadata.ConfigurationMetadataResponseItem;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ConfigurationAccessServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableJaxRsProxyClient(
        classes = ConfigurationAccessRestService.class,
        address = "http://localhost:${server.port}/api"
)
@DefinePort
@EnableEmbeddedPg
public class ConfigurationAccessRestServiceImplTest {

    /** @noinspection SpringJavaInjectionPointsAutowiringInspection*/
    @Autowired
    @Qualifier("configurationAccessRestServiceJaxRsProxyClient")
    private ConfigurationAccessRestService configurationAccessRestService;


    /**
     * Проверка, что список метаданных настроек возвращается корректно
     */
    @Test
    public void getAllConfigurationsMetadataTest() {
        ConfigurationMetadataResponseItem configurationMetadataResponseItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataResponseItem);
        ConfigurationMetadataResponseItem configurationMetadataResponseItem2 = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem2();
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataResponseItem2);

        List<ConfigurationMetadataResponseItem> configurationsMetadata = configurationAccessRestService.getAllConfigurationsMetadata().getContent();

        assertEquals(2, configurationsMetadata.size());
        assertEquals(configurationMetadataResponseItem, configurationsMetadata.get(0));
        assertEquals(configurationMetadataResponseItem2, configurationsMetadata.get(1));

        configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataResponseItem.getCode());
        configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataResponseItem2.getCode());
    }

    /**
     * Проверка, что по коду настройки должны возвращаться все метаданные этой настройки
     */
    @Test
    public void getConfigurationMetadataTest() {
        ConfigurationMetadataResponseItem configurationMetadataResponseItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataResponseItem);

        assertEquals(configurationMetadataResponseItem,
                configurationAccessRestService.getConfigurationMetadata(configurationMetadataResponseItem.getCode()));

        configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataResponseItem.getCode());
    }

    /**
     * Проверка, что чтение метаданных по несуществующему коду приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void getConfigurationMetadataIfNotExistsTest() {
        ConfigurationMetadataResponseItem configurationMetadataResponseItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();

        configurationAccessRestService.getConfigurationMetadata(configurationMetadataResponseItem.getCode());
    }

    /**
     * Проверка, что корректные метаданные настройки успешно сохраняются
     */
    @Test
    public void saveConfigurationMetadataTest() {
        ConfigurationMetadataResponseItem configurationMetadataResponseItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataResponseItem);

        List<ConfigurationMetadataResponseItem> configurationsMetadata = configurationAccessRestService.getAllConfigurationsMetadata().getContent();

        assertEquals(1, configurationsMetadata.size());
        assertEquals(configurationMetadataResponseItem, configurationsMetadata.get(0));

        configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataResponseItem.getCode());
    }

    /**
     * Проверка, что повторное сохранение метаданных настройки приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void saveAlreadyExistsConfigurationMetadataTest() {
        ConfigurationMetadataResponseItem configurationMetadataResponseItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataResponseItem);

        try {
            configurationAccessRestService.saveConfigurationMetadata(configurationMetadataResponseItem);
        } finally {
            configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataResponseItem.getCode());
        }
    }

    /**
     * Проверка, что сохранение метаданных настройки c пустым значением имени приводит к RestException
     */
    @Test(expected = RestException.class)
    public void saveConfigurationMetadataWithBlankNameTest() {
        ConfigurationMetadataResponseItem configurationMetadataResponseItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();
        configurationMetadataResponseItem.setName("");
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataResponseItem);

        try {
            configurationAccessRestService.saveConfigurationMetadata(configurationMetadataResponseItem);
        } finally {
            configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataResponseItem.getCode());
        }
    }

    /**
     * Проверка, что корректные метаданные успешно обновляются
     */
    @Test
    public void updateConfigurationMetadataTest() {
        ConfigurationMetadataResponseItem configurationMetadataResponseItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataResponseItem);

        ConfigurationMetadataResponseItem newConfigurationMetadataResponseItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem2();
        newConfigurationMetadataResponseItem.setCode(configurationMetadataResponseItem.getCode());

        configurationAccessRestService.updateConfigurationMetadata(newConfigurationMetadataResponseItem.getCode(), newConfigurationMetadataResponseItem);

        assertEquals(newConfigurationMetadataResponseItem, configurationAccessRestService.getConfigurationMetadata(newConfigurationMetadataResponseItem.getCode()));

        configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataResponseItem.getCode());
    }

    /**
     * Проверка, что обновлении метаданных настройки с несовпадающим кодом в пути и теле приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void updateConfigurationMetadataWithDifferentCodeTest() {
        ConfigurationMetadataResponseItem configurationMetadataResponseItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataResponseItem);

        ConfigurationMetadataResponseItem newConfigurationMetadataResponseItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem2();

        try {
            configurationAccessRestService.updateConfigurationMetadata(configurationMetadataResponseItem.getCode(), newConfigurationMetadataResponseItem);
        } finally {
            configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataResponseItem.getCode());
        }
    }

    /**
     * Проверка, что обновление метаданных по несуществующему коду приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void updateNotExistsConfigurationMetadataTest() {
        ConfigurationMetadataResponseItem configurationMetadataResponseItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();

        configurationAccessRestService.updateConfigurationMetadata(configurationMetadataResponseItem.getCode(), configurationMetadataResponseItem);
    }

    /**
     * Проверка, что удаление метаданных настройки по коду происходит корректно
     */
    @Test
    public void deleteConfigurationMetadataTest() {
        ConfigurationMetadataResponseItem configurationMetadataResponseItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();

        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataResponseItem);
        configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataResponseItem.getCode());

        assertTrue(configurationAccessRestService.getAllConfigurationsMetadata().isEmpty());
    }

    /**
     * Проверка, что удаление метаданных настройки по несуществующему коду приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void deleteAlreadyDeletedConfigurationMetadataTest() {
        ConfigurationMetadataResponseItem configurationMetadataResponseItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();

        configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataResponseItem.getCode());
    }
}