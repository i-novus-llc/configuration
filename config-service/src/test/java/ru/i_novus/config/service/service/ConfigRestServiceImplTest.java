package ru.i_novus.config.service.service;

import net.n2oapp.platform.jaxrs.RestException;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.ConfigServiceApplication;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.config.api.model.ConfigRequest;
import ru.i_novus.config.api.model.ConfigResponse;
import ru.i_novus.config.api.service.ConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.service.entity.ValueTypeEnum;
import ru.i_novus.config.service.service.builders.ConfigRequestBuilder;
import ru.i_novus.system_application.service.CommonSystemResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ConfigServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableJaxRsProxyClient(
        classes = ConfigRestService.class,
        address = "http://localhost:${server.port}/api"
)
@DefinePort
@EnableEmbeddedPg
@TestPropertySource(properties = "spring.liquibase.change-log=classpath:/db/db.changelog-master-test.yaml")
public class ConfigRestServiceImplTest {

    /**
     * @noinspection SpringJavaInjectionPointsAutowiringInspection
     */
    @Autowired
    @Qualifier("configRestServiceJaxRsProxyClient")
    private ConfigRestService configRestService;

    @MockBean
    private ConfigValueService configValueService;


    @Before
    public void setUp() {
        when(configValueService.getValue(any(), any())).thenReturn("test-value");
        doNothing().when(configValueService).saveValue(any(), any(), any());
    }


    /**
     * Проверка, что список настроек возвращается корректно
     */
    @Test
    public void getAllConfigTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest1();
        ConfigRequest configRequest2 = ConfigRequestBuilder.buildConfigRequest2();
        ConfigRequest configRequest3 = ConfigRequestBuilder.buildConfigRequest3();

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(new ConfigCriteria()).getContent();

        assertEquals(3, configResponses.size());
        configAssertEquals(configRequest, configResponses.get(0));
        configAssertEquals(configRequest2, configResponses.get(1));
        configAssertEquals(configRequest3, configResponses.get(2));
    }

    /**
     * Проверка, что фильтрация настроек по коду работает корректно
     */
    @Test
    public void getAllConfigByCodeTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest2();
        ConfigRequest configRequest2 = ConfigRequestBuilder.buildConfigRequest3();

        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setCode("sec");

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configResponses.size());
        configAssertEquals(configRequest, configResponses.get(0));
        configAssertEquals(configRequest2, configResponses.get(1));
    }

    /**
     * Проверка, что фильтрация настроек по имени работает корректно
     */
    @Test
    public void getAllConfigByNameTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest2();
        ConfigRequest configRequest2 = ConfigRequestBuilder.buildConfigRequest3();

        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setName("name");

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configResponses.size());
        configAssertEquals(configRequest, configResponses.get(0));
        configAssertEquals(configRequest2, configResponses.get(1));
    }

    /**
     * Проверка, что фильтрация настроек по именам групп работает корректно
     */
    @Test
    public void getAllConfigByGroupNameTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest2();
        ConfigRequest configRequest2 = ConfigRequestBuilder.buildConfigRequest3();


        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setGroupIds(Collections.singletonList(102));

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configResponses.size());
        configAssertEquals(configRequest, configResponses.get(0));
        configAssertEquals(configRequest2, configResponses.get(1));
    }

    /**
     * Проверка, что фильтрация настроек по именам систем работает корректно
     */
    @Test
    public void getAllConfigBySystemNameTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest2();
        ConfigRequest configRequest2 = ConfigRequestBuilder.buildConfigRequest3();


        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setSystemCodes(Arrays.asList("system-security", new CommonSystemResponse().getCode()));

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configResponses.size());
        configAssertEquals(configRequest, configResponses.get(0));
        configAssertEquals(configRequest2, configResponses.get(1));
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    public void configPaginationTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest1();
        ConfigRequest configRequest2 = ConfigRequestBuilder.buildConfigRequest2();
        ConfigRequest configRequest3 = ConfigRequestBuilder.buildConfigRequest3();

        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setPageSize(2);

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configResponses.size());
        configAssertEquals(configRequest, configResponses.get(0));
        configAssertEquals(configRequest2, configResponses.get(1));

        criteria.setPageNumber(1);
        configResponses = configRestService.getAllConfig(criteria).getContent();

        assertEquals(1, configResponses.size());
        configAssertEquals(configRequest3, configResponses.get(0));
    }

    /**
     * Проверка, что настройка по некоторому заданному коду возвращается корректно
     */
    @Test
    public void getConfigTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildTestConfigRequest();
        configRestService.saveConfig(configRequest);

        configAssertEquals(configRequest, configRestService.getConfig(configRequest.getCode()));

        configRestService.deleteConfig(configRequest.getCode());
    }

    /**
     * Проверка, что настройка успешно сохраняется
     */
    @Test
    public void saveConfigTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildTestConfigRequest();
        configRestService.saveConfig(configRequest);

        ConfigResponse configResponse = configRestService.getConfig(configRequest.getCode());

        configAssertEquals(configRequest, configResponse);

        configRestService.deleteConfig(configRequest.getCode());
    }

    /**
     * Проверка, что сохранение настройки с уже существующим кодом приводит к RestException
     */
    @Test(expected = RestException.class)
    public void saveAlreadyExistsConfigTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest1();

        configRestService.saveConfig(configRequest);
    }

    /**
     * Проверка, что настройка успешно обновляется
     */
    @Test
    public void updateConfigMetadataTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildTestConfigRequest();
        configRestService.saveConfig(configRequest);

        configRequest.setApplicationCode(null);
        configRequest.setDescription("test-test");
        configRequest.setName("test-test");
        configRequest.setValue("1");
        configRequest.setValueType(ValueTypeEnum.NUMBER.getTitle());

        configRestService.updateConfig(configRequest.getCode(), configRequest);

        when(configValueService.getValue(any(), any())).thenReturn(configRequest.getValue());
        configAssertEquals(configRequest, configRestService.getConfig(configRequest.getCode()));

        configRestService.deleteConfig(configRequest.getCode());
    }

    /**
     * Проверка, что удаление настройки по коду происходит корректно
     */
    @Test(expected = RestException.class)
    public void deleteConfigTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildTestConfigRequest();

        configRestService.saveConfig(configRequest);
        configRestService.deleteConfig(configRequest.getCode());

        configRestService.getConfig(configRequest.getCode());
    }

    /**
     * Проверка, что удаление настройки по несуществующему коду приводит к RestException
     */
    @Test(expected = RestException.class)
    public void deleteAlreadyDeletedConfigTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildTestConfigRequest();

        configRestService.deleteConfig(configRequest.getCode());
    }

    private void configAssertEquals(ConfigRequest configRequest, ConfigResponse configResponse) {
        assertEquals(configRequest.getCode(), configResponse.getCode());
        assertEquals(configRequest.getName(), configResponse.getName());
        assertEquals(configRequest.getDescription(), configResponse.getDescription());
        assertEquals(configRequest.getApplicationCode(), configResponse.getApplication().getCode());
        assertEquals(configRequest.getValueType(), configResponse.getValueType());
    }
}