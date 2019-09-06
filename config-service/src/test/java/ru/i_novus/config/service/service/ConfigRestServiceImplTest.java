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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.config.api.model.ConfigRequest;
import ru.i_novus.config.api.model.ConfigResponse;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.api.service.ConfigGroupRestService;
import ru.i_novus.config.api.service.ConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.service.ConfigServiceApplication;
import ru.i_novus.config.service.entity.ValueTypeEnum;
import ru.i_novus.config.service.model.Application;
import ru.i_novus.config.service.model.System;
import ru.i_novus.config.service.service.builders.ApplicationBuilder;
import ru.i_novus.config.service.service.builders.ConfigRequestBuilder;
import ru.i_novus.config.service.service.builders.GroupFormBuilder;
import ru.i_novus.config.service.service.builders.SystemBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ConfigServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableJaxRsProxyClient(
        classes = {
                ConfigRestService.class,
                ConfigGroupRestService.class
        },
        address = "http://localhost:${server.port}/api"
)
@DefinePort
@EnableEmbeddedPg
public class ConfigRestServiceImplTest {

    /**
     * @noinspection SpringJavaInjectionPointsAutowiringInspection
     */
    @Autowired
    @Qualifier("configRestServiceJaxRsProxyClient")
    private ConfigRestService configRestService;

    @Autowired
    @Qualifier("configGroupRestServiceJaxRsProxyClient")
    private ConfigGroupRestService groupRestService;

    @MockBean
    private ConfigValueService configValueService;

    @MockBean
    private RestTemplate restTemplate;

    @Value("${security.admin.url}")
    private String url;


    @Before
    public void setUp() {
        when(configValueService.getValue(any(), any())).thenReturn("test-value");
        doNothing().when(configValueService).saveValue(any(), any(), any());

        when(restTemplate.getForObject(url + "/applications/" + ApplicationBuilder.buildApplication1().getCode(), Application.class))
                .thenReturn(ApplicationBuilder.buildApplication1());
        when(restTemplate.getForObject(url + "/applications/" + ApplicationBuilder.buildApplication2().getCode(), Application.class))
                .thenReturn(ApplicationBuilder.buildApplication2());

        PagedResources body = new PagedResources(Arrays.asList(SystemBuilder.buildSystem()),
                new PagedResources.PageMetadata(1, 1, 1, 1));
        ResponseEntity<PagedResources<System>> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                String.format("%s/systems/?size=%d&code=%s", url, Integer.MAX_VALUE, SystemBuilder.buildSystem().getCode()),
                HttpMethod.GET, null,
                new ParameterizedTypeReference<PagedResources<System>>() {
                })).thenReturn(responseEntity);
    }


    /**
     * Проверка, что список настроек возвращается корректно
     */
    @Test
    public void getAllConfigTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest1();
        configRestService.saveConfig(configRequest);
        ConfigRequest configRequest2 = ConfigRequestBuilder.buildConfigRequest2();
        configRestService.saveConfig(configRequest2);
        ConfigRequest configRequest3 = ConfigRequestBuilder.buildConfigRequest3();
        configRestService.saveConfig(configRequest3);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(new ConfigCriteria()).getContent();

        assertEquals(3, configResponses.size());
        configAssertEquals(configRequest, configResponses.get(0));
        configAssertEquals(configRequest2, configResponses.get(1));
        configAssertEquals(configRequest3, configResponses.get(2));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configRestService.deleteConfig(configRequest.getCode());
        configRestService.deleteConfig(configRequest2.getCode());
        configRestService.deleteConfig(configRequest3.getCode());
    }

    /**
     * Проверка, что фильтрация настроек по коду работает корректно
     */
    @Test
    public void getAllConfigByCodeTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest1();
        configRestService.saveConfig(configRequest);
        ConfigRequest configRequest2 = ConfigRequestBuilder.buildConfigRequest2();
        configRestService.saveConfig(configRequest2);
        ConfigRequest configRequest3 = ConfigRequestBuilder.buildConfigRequest3();
        configRestService.saveConfig(configRequest3);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);

        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setCode("sec");

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configResponses.size());
        configAssertEquals(configRequest2, configResponses.get(0));
        configAssertEquals(configRequest3, configResponses.get(1));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configRestService.deleteConfig(configRequest.getCode());
        configRestService.deleteConfig(configRequest2.getCode());
        configRestService.deleteConfig(configRequest3.getCode());
    }

    /**
     * Проверка, что фильтрация настроек по имени работает корректно
     */
    @Test
    public void getAllConfigByNameTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest1();
        configRestService.saveConfig(configRequest);
        ConfigRequest configRequest2 = ConfigRequestBuilder.buildConfigRequest2();
        configRestService.saveConfig(configRequest2);
        ConfigRequest configRequest3 = ConfigRequestBuilder.buildConfigRequest3();
        configRestService.saveConfig(configRequest3);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);

        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setName("name");

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configResponses.size());
        configAssertEquals(configRequest2, configResponses.get(0));
        configAssertEquals(configRequest3, configResponses.get(1));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configRestService.deleteConfig(configRequest.getCode());
        configRestService.deleteConfig(configRequest2.getCode());
        configRestService.deleteConfig(configRequest3.getCode());
    }

    /**
     * Проверка, что фильтрация настроек по именам групп работает корректно
     */
    @Test
    public void getAllConfigByGroupNameTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest1();
        configRestService.saveConfig(configRequest);
        ConfigRequest configRequest2 = ConfigRequestBuilder.buildConfigRequest2();
        configRestService.saveConfig(configRequest2);
        ConfigRequest configRequest3 = ConfigRequestBuilder.buildConfigRequest3();
        configRestService.saveConfig(configRequest3);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);


        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setGroupIds(Collections.singletonList(groupId2));

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configResponses.size());
        configAssertEquals(configRequest2, configResponses.get(0));
        configAssertEquals(configRequest3, configResponses.get(1));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configRestService.deleteConfig(configRequest.getCode());
        configRestService.deleteConfig(configRequest2.getCode());
        configRestService.deleteConfig(configRequest3.getCode());
    }

    /**
     * Проверка, что фильтрация настроек по именам систем работает корректно
     */
    @Test
    public void getAllConfigBySystemNameTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest1();
        configRestService.saveConfig(configRequest);
        ConfigRequest configRequest2 = ConfigRequestBuilder.buildConfigRequest2();
        configRestService.saveConfig(configRequest2);
        ConfigRequest configRequest3 = ConfigRequestBuilder.buildConfigRequest3();
        configRestService.saveConfig(configRequest3);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);


        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setSystemCodes(Arrays.asList("system-security", "common-system"));

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configResponses.size());
        configAssertEquals(configRequest2, configResponses.get(0));
        configAssertEquals(configRequest3, configResponses.get(1));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configRestService.deleteConfig(configRequest.getCode());
        configRestService.deleteConfig(configRequest2.getCode());
        configRestService.deleteConfig(configRequest3.getCode());
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    public void configPaginationTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest1();
        configRestService.saveConfig(configRequest);
        ConfigRequest configRequest2 = ConfigRequestBuilder.buildConfigRequest2();
        configRestService.saveConfig(configRequest2);
        ConfigRequest configRequest3 = ConfigRequestBuilder.buildConfigRequest3();
        configRestService.saveConfig(configRequest3);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);


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

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configRestService.deleteConfig(configRequest.getCode());
        configRestService.deleteConfig(configRequest2.getCode());
        configRestService.deleteConfig(configRequest3.getCode());
    }

    /**
     * Проверка, что настройка успешно сохраняется
     */
    @Test
    public void saveConfigTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest1();
        configRestService.saveConfig(configRequest);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(new ConfigCriteria()).getContent();

        assertEquals(1, configResponses.size());
        configAssertEquals(configRequest, configResponses.get(0));

        groupRestService.deleteGroup(groupId);
        configRestService.deleteConfig(configRequest.getCode());
    }

    /**
     * Проверка, что сохранение настройки с уже существующим кодом приводит к RestException
     */
    @Test(expected = RestException.class)
    public void saveAlreadyExistsConfigTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest1();
        configRestService.saveConfig(configRequest);

        try {
            configRestService.saveConfig(configRequest);
        } finally {
            configRestService.deleteConfig(configRequest.getCode());
        }
    }

    /**
     * Проверка, что настройка успешно обновляется
     */
    @Test
    public void updateConfigMetadataTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest1();
        configRestService.saveConfig(configRequest);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);

        configRequest.setApplicationCode(null);
        configRequest.setDescription("test");
        configRequest.setName("test");
        configRequest.setValue("1");
        configRequest.setValueType(ValueTypeEnum.NUMBER.getTitle());

        when(configValueService.getValue(any(), any())).thenReturn(configRequest.getValue());

        configRestService.updateConfig(configRequest.getCode(), configRequest);

        configAssertEquals(configRequest, configRestService.getConfig(configRequest.getCode()));

        groupRestService.deleteGroup(groupId);
        configRestService.deleteConfig(configRequest.getCode());
    }

    /**
     * Проверка, что удаление настройки по коду происходит корректно
     */
    @Test
    public void deleteConfigTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest1();

        configRestService.saveConfig(configRequest);
        configRestService.deleteConfig(configRequest.getCode());

        assertTrue(configRestService.getAllConfig(new ConfigCriteria()).isEmpty());
    }

    /**
     * Проверка, что удаление настройки по несуществующему коду приводит к RestException
     */
    @Test(expected = RestException.class)
    public void deleteAlreadyDeletedConfigTest() {
        ConfigRequest configRequest = ConfigRequestBuilder.buildConfigRequest1();

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