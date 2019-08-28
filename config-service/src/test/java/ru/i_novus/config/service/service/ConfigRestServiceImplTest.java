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
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.api.service.ConfigGroupRestService;
import ru.i_novus.config.api.service.ConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.service.ConfigServiceApplication;
import ru.i_novus.config.service.service.builders.ConfigFormBuilder;
import ru.i_novus.config.service.service.builders.GroupFormBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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


    @Before
    public void setUp() {
        when(configValueService.getValue(any(), any())).thenReturn("test-value");
    }


    /**
     * Проверка, что список настроек возвращается корректно
     */
    @Test
    public void getAllConfigTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm1();
        configRestService.saveConfig(configForm);
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm2();
        configRestService.saveConfig(configForm2);
        ConfigForm configForm3 = ConfigFormBuilder.buildConfigForm3();
        configRestService.saveConfig(configForm3);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);

        List<ConfigForm> configForms =
                configRestService.getAllConfig(new ConfigCriteria()).getContent();

        assertEquals(3, configForms.size());
        assertEquals(configForm, configForms.get(0));
        assertEquals(configForm2, configForms.get(1));
        assertEquals(configForm3, configForms.get(2));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configRestService.deleteConfig(configForm.getCode());
        configRestService.deleteConfig(configForm2.getCode());
        configRestService.deleteConfig(configForm3.getCode());
    }

    /**
     * Проверка, что фильтрация настроек по коду работает корректно
     */
    @Test
    public void getAllConfigByCodeTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm1();
        configRestService.saveConfig(configForm);
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm2();
        configRestService.saveConfig(configForm2);
        ConfigForm configForm3 = ConfigFormBuilder.buildConfigForm3();
        configRestService.saveConfig(configForm3);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);

        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setCode("sec");

        List<ConfigForm> configForms =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configForms.size());
        assertEquals(configForm, configForms.get(0));
        assertEquals(configForm2, configForms.get(1));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configRestService.deleteConfig(configForm.getCode());
        configRestService.deleteConfig(configForm2.getCode());
        configRestService.deleteConfig(configForm3.getCode());
    }

    /**
     * Проверка, что фильтрация настроек по имени работает корректно
     */
    @Test
    public void getAllConfigByNameTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm1();
        configRestService.saveConfig(configForm);
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm2();
        configRestService.saveConfig(configForm2);
        ConfigForm configForm3 = ConfigFormBuilder.buildConfigForm3();
        configRestService.saveConfig(configForm3);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);

        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setName("name");

        List<ConfigForm> configForms =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configForms.size());
        assertEquals(configForm, configForms.get(0));
        assertEquals(configForm2, configForms.get(1));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configRestService.deleteConfig(configForm.getCode());
        configRestService.deleteConfig(configForm2.getCode());
        configRestService.deleteConfig(configForm3.getCode());
    }

    /**
     * Проверка, что фильтрация настроек по именам групп работает корректно
     */
    @Test
    public void getAllConfigByGroupNameTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm1();
        configRestService.saveConfig(configForm);
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm2();
        configRestService.saveConfig(configForm2);
        ConfigForm configForm3 = ConfigFormBuilder.buildConfigForm3();
        configRestService.saveConfig(configForm3);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);


        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setGroupIds(Collections.singletonList(groupId));

        List<ConfigForm> configForms =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configForms.size());
        assertEquals(configForm, configForms.get(0));
        assertEquals(configForm2, configForms.get(1));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configRestService.deleteConfig(configForm.getCode());
        configRestService.deleteConfig(configForm2.getCode());
        configRestService.deleteConfig(configForm3.getCode());
    }

    /**
     * Проверка, что фильтрация настроек по именам систем работает корректно
     */
    @Test
    public void getAllConfigBySystemNameTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm1();
        configRestService.saveConfig(configForm);
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm2();
        configRestService.saveConfig(configForm2);
        ConfigForm configForm3 = ConfigFormBuilder.buildConfigForm3();
        configRestService.saveConfig(configForm3);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);


        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setSystemCodes(Arrays.asList("system-security"));

        // TODO mock

        List<ConfigForm> configForms =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configForms.size());
        assertEquals(configForm, configForms.get(0));
        assertEquals(configForm2, configForms.get(1));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configRestService.deleteConfig(configForm.getCode());
        configRestService.deleteConfig(configForm2.getCode());
        configRestService.deleteConfig(configForm3.getCode());
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    public void configPaginationTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm1();
        configRestService.saveConfig(configForm);
        ConfigForm configForm2 = ConfigFormBuilder.buildConfigForm2();
        configRestService.saveConfig(configForm2);
        ConfigForm configForm3 = ConfigFormBuilder.buildConfigForm3();
        configRestService.saveConfig(configForm3);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);
        GroupForm groupForm2 = GroupFormBuilder.buildGroupForm2();
        Integer groupId2 = groupRestService.saveGroup(groupForm2);


        ConfigCriteria criteria = new ConfigCriteria();
        criteria.setPageSize(2);

        List<ConfigForm> configForms =
                configRestService.getAllConfig(criteria).getContent();

        assertEquals(2, configForms.size());
        assertEquals(configForm, configForms.get(0));
        assertEquals(configForm2, configForms.get(1));

        criteria.setPageNumber(1);
        configForms = configRestService.getAllConfig(criteria).getContent();

        assertEquals(1, configForms.size());
        assertEquals(configForm3, configForms.get(0));

        groupRestService.deleteGroup(groupId);
        groupRestService.deleteGroup(groupId2);
        configRestService.deleteConfig(configForm.getCode());
        configRestService.deleteConfig(configForm2.getCode());
        configRestService.deleteConfig(configForm3.getCode());
    }

    /**
     * Проверка, что настройка успешно сохраняется
     */
    @Test
    public void saveConfigTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm1();
        configRestService.saveConfig(configForm);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);

        List<ConfigForm> configForms =
                configRestService.getAllConfig(new ConfigCriteria()).getContent();

        assertEquals(1, configForms.size());
        assertEquals(configForm, configForms.get(0));

        groupRestService.deleteGroup(groupId);
        configRestService.deleteConfig(configForm.getCode());
    }

    /**
     * Проверка, что сохранение настройки с уже существующим кодом приводит к RestException
     */
    @Test(expected = RestException.class)
    public void saveAlreadyExistsConfigTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm1();
        configRestService.saveConfig(configForm);

        try {
            configRestService.saveConfig(configForm);
        } finally {
            configRestService.deleteConfig(configForm.getCode());
        }
    }

    /**
     * Проверка, что настройка успешно обновляется
     */
    @Test
    public void updateConfigMetadataTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm1();
        configRestService.saveConfig(configForm);
        GroupForm groupForm = GroupFormBuilder.buildGroupForm1();
        Integer groupId = groupRestService.saveGroup(groupForm);

        configForm.setApplicationCode("test");
        configForm.setDescription("test");
        configForm.setValue("test");

        when(configValueService.getValue(any(), any())).thenReturn(configForm.getValue());

        configRestService.updateConfig(configForm.getCode(), configForm);

        assertEquals(configForm, configRestService.getConfig(configForm.getCode()));

        groupRestService.deleteGroup(groupId);
        configRestService.deleteConfig(configForm.getCode());
    }

    /**
     * Проверка, что удаление настройки по коду происходит корректно
     */
    @Test
    public void deleteConfigTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm1();

        configRestService.saveConfig(configForm);
        configRestService.deleteConfig(configForm.getCode());

        assertTrue(configRestService.getAllConfig(new ConfigCriteria()).isEmpty());
    }

    /**
     * Проверка, что удаление настройки по несуществующему коду приводит к RestException
     */
    @Test(expected = RestException.class)
    public void deleteAlreadyDeletedConfigTest() {
        ConfigForm configForm = ConfigFormBuilder.buildConfigForm1();

        configRestService.deleteConfig(configForm.getCode());
    }
}