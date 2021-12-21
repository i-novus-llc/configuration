package ru.i_novus.configuration.config.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.TestApp;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.ConfigResponse;
import ru.i_novus.config.api.model.ValueTypeEnum;
import ru.i_novus.config.api.service.ConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.api.util.AuditService;
import ru.i_novus.configuration.config.service.builders.ConfigFormBuilder;

import javax.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableEmbeddedPg
public class ConfigRestServiceImplTest {

    @Autowired
    private ConfigRestService configRestService;

    @MockBean
    private ConfigValueService configValueService;

    @MockBean
    private AuditService auditService;

    @Value("${config.common.system.code}")
    private String commonSystemCode;


    @Before
    public void setUp() {
        when(configValueService.getValue(any(), any())).thenReturn("test-value");
        doNothing().when(configValueService).saveValue(any(), any(), any());
        doNothing().when(configValueService).deleteValue(any(), any());
    }


    /**
     * Проверка, что список настроек возвращается корректно
     */
    @Test
    public void getAllConfigTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm1();
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm2();
        ConfigForm configForm3 = ConfigFormBuilder.buildConfigForm3();

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(new ConfigCriteria()).getContent();

        assertEquals(3, configResponses.size());
        configAssertEquals(configForm3, configResponses.get(0));
        configAssertEquals(configForm, configResponses.get(1));
        configAssertEquals(configForm2, configResponses.get(2));
    }

    /**
     * Проверка, что фильтрация настроек по коду работает корректно
     */
    @Test
    public void getAllConfigByCodeTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm2();
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm3();

        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setCode("sec");

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configResponses.size());
        configAssertEquals(configForm2, configResponses.get(0));
        configAssertEquals(configForm, configResponses.get(1));
    }

    /**
     * Проверка, что фильтрация настроек по имени работает корректно
     */
    @Test
    public void getAllConfigByNameTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm2();
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm3();

        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setName("name");

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configResponses.size());
        configAssertEquals(configForm2, configResponses.get(0));
        configAssertEquals(configForm, configResponses.get(1));
    }

    /**
     * Проверка, что фильтрация настроек по именам групп работает корректно
     */
    @Test
    public void getAllConfigByGroupNameTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm2();
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm3();


        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setGroupIds(Collections.singletonList(102));

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configResponses.size());
        configAssertEquals(configForm2, configResponses.get(0));
        configAssertEquals(configForm, configResponses.get(1));
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    public void configPaginationTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm1();
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm2();
        ConfigForm configForm3 = ConfigFormBuilder.buildConfigForm3();

        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setPageSize(2);

        List<ConfigResponse> configResponses =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configResponses.size());
        configAssertEquals(configForm3, configResponses.get(0));
        configAssertEquals(configForm, configResponses.get(1));

        criteria.setPageNumber(1);
        configResponses = configRestService.getAllConfig(criteria).getContent();

        assertEquals(1, configResponses.size());
        configAssertEquals(configForm2, configResponses.get(0));
    }

    /**
     * Проверка, что настройка по некоторому заданному коду возвращается корректно
     */
    @Test
    public void getConfigTest() {
        ConfigForm configForm = ConfigFormBuilder.buildTestConfigForm();
        configRestService.saveConfig(configForm);

        configAssertEquals(configForm, configRestService.getConfig(configForm.getCode()));

        configRestService.deleteConfig(configForm.getCode());
    }

    /**
     * Проверка, что получение настройки по несуществующему коду приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void getConfigByNotExistsCodeTest() {
        configRestService.getConfig("bad-code");
    }

    /**
     * Проверка, что настройка успешно сохраняется
     */
    @Test
    public void saveConfigTest() {
        ConfigForm configForm = ConfigFormBuilder.buildTestConfigForm();
        configRestService.saveConfig(configForm);

        ConfigResponse configResponse = configRestService.getConfig(configForm.getCode());

        configAssertEquals(configForm, configResponse);

        configRestService.deleteConfig(configForm.getCode());
    }

    /**
     * Проверка, что сохранение настройки с уже существующим кодом приводит к UserException
     */
    @Test(expected = UserException.class)
    public void saveAlreadyExistsConfigTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm1();

        configRestService.saveConfig(configForm);
    }

    /**
     * Проверка, что настройка успешно обновляется
     */
    @Test
    public void updateConfigMetadataTest() {
        ConfigForm configForm = ConfigFormBuilder.buildTestConfigForm();
        configRestService.saveConfig(configForm);

        configForm.setApplicationCode(null);
        configForm.setDescription("test-test");
        configForm.setName("test-test");
        configForm.setValue("1");
        configForm.setValueType(ValueTypeEnum.NUMBER.name());

        configRestService.updateConfig(configForm.getCode(), configForm);

        when(configValueService.getValue(any(), any())).thenReturn(configForm.getValue());
        configAssertEquals(configForm, configRestService.getConfig(configForm.getCode()));

        configRestService.deleteConfig(configForm.getCode());
    }

    /**
     * Проверка, что обновление настройки по несуществующему коду приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void updateConfigByNotExistsCodeTest() {
        ConfigForm configForm = ConfigFormBuilder.buildTestConfigForm();
        configForm.setCode("bad-code");
        configRestService.updateConfig(configForm.getCode(), configForm);
    }

    /**
     * Проверка, что удаление настройки по коду происходит корректно
     */
    @Test(expected = NotFoundException.class)
    public void deleteConfigTest() {
        ConfigForm configForm = ConfigFormBuilder.buildTestConfigForm();

        configRestService.saveConfig(configForm);
        configRestService.deleteConfig(configForm.getCode());

        configRestService.getConfig(configForm.getCode());
    }

    /**
     * Проверка, что удаление настройки по несуществующему коду приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void deleteConfigByNotExistsCodeTest() {
        configRestService.deleteConfig("bad-code");
    }

    private void configAssertEquals(ConfigForm configForm, ConfigResponse configResponse) {
        assertEquals(configForm.getCode(), configResponse.getCode());
        assertEquals(configForm.getName(), configResponse.getName());
        assertEquals(configForm.getDescription(), configResponse.getDescription());
        assertEquals(configForm.getApplicationCode(), configResponse.getApplication().getCode());
        assertEquals(configForm.getValueType(), configResponse.getValueType().getId());
    }
}