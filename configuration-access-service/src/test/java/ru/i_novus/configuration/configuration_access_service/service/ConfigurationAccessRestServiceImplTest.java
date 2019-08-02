package ru.i_novus.configuration.configuration_access_service.service;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.platform.test.autoconfigure.DefinePort;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.configuration.configuration_access_service.ConfigurationAccessServiceApplication;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationMetadataItem;

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
        address = "http://localhost:${server.port}/"
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
        ConfigurationMetadataItem configurationMetadataItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataItem);
        ConfigurationMetadataItem configurationMetadataItem2 = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem2();
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataItem2);

        List<ConfigurationMetadataItem> configurationsMetadata = configurationAccessRestService.getAllConfigurationsMetadata().getContent();

        assertEquals(2, configurationsMetadata.size());
        assertEquals(configurationMetadataItem, configurationsMetadata.get(0));
        assertEquals(configurationMetadataItem2, configurationsMetadata.get(1));

        configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataItem.getCode());
        configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataItem2.getCode());
    }

    /**
     * Проверка, что по коду настройки должны возвращаться все метаданные этой настройки
     */
    @Test
    public void getConfigurationMetadataTest() {
        ConfigurationMetadataItem configurationMetadataItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataItem);

        assertEquals(configurationMetadataItem,
                configurationAccessRestService.getConfigurationMetadata(configurationMetadataItem.getCode()));

        configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataItem.getCode());
    }

    /**
     * Проверка, что чтение метаданных по несуществующему коду приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void getConfigurationMetadataIfNotExistsTest() {
        ConfigurationMetadataItem configurationMetadataItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();

        configurationAccessRestService.getConfigurationMetadata(configurationMetadataItem.getCode());
    }

    /**
     * Проверка, что корректные метаданные настройки успешно сохраняются
     */
    @Test
    public void saveConfigurationMetadataTest() {
        ConfigurationMetadataItem configurationMetadataItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataItem);

        List<ConfigurationMetadataItem> configurationsMetadata = configurationAccessRestService.getAllConfigurationsMetadata().getContent();

        assertEquals(1, configurationsMetadata.size());
        assertEquals(configurationMetadataItem, configurationsMetadata.get(0));

        configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataItem.getCode());
    }

    /**
     * Проверка, что повторное сохранение метаданных настройки приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void saveAlreadyExistsConfigurationMetadataTest() {
        ConfigurationMetadataItem configurationMetadataItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataItem);

        try {
            configurationAccessRestService.saveConfigurationMetadata(configurationMetadataItem);
        } finally {
            configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataItem.getCode());
        }
    }

    /**
     * Проверка, что корректные метаданные успешно обновляются
     */
    @Test
    public void updateConfigurationMetadataTest() {
        ConfigurationMetadataItem configurationMetadataItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataItem);

        ConfigurationMetadataItem newConfigurationMetadataItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem2();
        newConfigurationMetadataItem.setCode(configurationMetadataItem.getCode());

        configurationAccessRestService.updateConfigurationMetadata(newConfigurationMetadataItem.getCode(), newConfigurationMetadataItem);

        assertEquals(newConfigurationMetadataItem, configurationAccessRestService.getConfigurationMetadata(newConfigurationMetadataItem.getCode()));

        configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataItem.getCode());
    }

    /**
     * Проверка, что обновлении метаданных настройки с несовпадающим кодом в пути и теле приводит к BadRequestException
     */
    @Test(expected = BadRequestException.class)
    public void updateConfigurationMetadataWithDifferentCodeTest() {
        ConfigurationMetadataItem configurationMetadataItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();
        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataItem);

        ConfigurationMetadataItem newConfigurationMetadataItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem2();

        try {
            configurationAccessRestService.updateConfigurationMetadata(configurationMetadataItem.getCode(), newConfigurationMetadataItem);
        } finally {
            configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataItem.getCode());
        }
    }

    /**
     * Проверка, что обновление метаданных по несуществующему коду приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void updateNotExistsConfigurationMetadataTest() {
        ConfigurationMetadataItem configurationMetadataItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();

        configurationAccessRestService.updateConfigurationMetadata(configurationMetadataItem.getCode(), configurationMetadataItem);
    }

    /**
     * Проверка, что удаление метаданных настройки по коду происходит корректно
     */
    @Test
    public void deleteConfigurationMetadataTest() {
        ConfigurationMetadataItem configurationMetadataItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();

        configurationAccessRestService.saveConfigurationMetadata(configurationMetadataItem);

        Page<ConfigurationMetadataItem> allConfigurationsMetadata = configurationAccessRestService.getAllConfigurationsMetadata();
        configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataItem.getCode());

        allConfigurationsMetadata = configurationAccessRestService.getAllConfigurationsMetadata();
        assertTrue(configurationAccessRestService.getAllConfigurationsMetadata().isEmpty());
    }

    /**
     * Проверка, что удаление метаданных настройки по несуществующему коду приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void deleteAlreadyDeletedConfigurationMetadataTest() {
        ConfigurationMetadataItem configurationMetadataItem = ConfigurationMetadataItemBuilder.buildConfigurationMetadataItem1();

        configurationAccessRestService.deleteConfigurationMetadata(configurationMetadataItem.getCode());
    }
}