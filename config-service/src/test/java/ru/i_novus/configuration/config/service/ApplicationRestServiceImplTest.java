package ru.i_novus.configuration.config.service;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.TestApp;
import ru.i_novus.config.api.criteria.ApplicationCriteria;
import ru.i_novus.config.api.model.ApplicationResponse;
import ru.i_novus.config.api.service.ApplicationRestService;
import ru.i_novus.config.api.service.ConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.api.util.AuditService;
import ru.i_novus.configuration.config.service.builders.ApplicationResponseBuilder;

import javax.ws.rs.NotFoundException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableEmbeddedPg
public class ApplicationRestServiceImplTest {

    @Autowired
    private ApplicationRestService applicationRestService;

    @MockBean
    private ConfigValueService configValueService;

    @MockBean
    private AuditService auditService;

    @Autowired
    private ConfigRestService configRestService;

    @Value("${spring.cloud.consul.config.defaultContext}")
    private String defaultAppCode;


    /**
     * Проверка, что список приложений возвращается корректно
     */
    @Test
    public void getAllApplicationTest() {
        ApplicationResponse application1 = ApplicationResponseBuilder.buildApplication1();
        ApplicationResponse application2 = ApplicationResponseBuilder.buildApplication2();
        ApplicationResponse application3 = ApplicationResponseBuilder.buildApplication3();

        List<ApplicationResponse> applicationResponses =
                applicationRestService.getAllApplication(new ApplicationCriteria()).getContent();
        assertEquals(3, applicationResponses.size());
        applicationAssertEquals(application1, applicationResponses.get(0));
        applicationAssertEquals(application2, applicationResponses.get(1));
        applicationAssertEquals(application3, applicationResponses.get(2));
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    public void getAllApplicationPaginationTest() {
        ApplicationResponse application1 = ApplicationResponseBuilder.buildApplication1();
        ApplicationResponse application2 = ApplicationResponseBuilder.buildApplication2();
        ApplicationResponse application3 = ApplicationResponseBuilder.buildApplication3();

        ApplicationCriteria criteria = new ApplicationCriteria();
        criteria.setPageSize(2);
        List<ApplicationResponse> applicationResponses =
                applicationRestService.getAllApplication(criteria).getContent();

        assertEquals(2, applicationResponses.size());
        applicationAssertEquals(application1, applicationResponses.get(0));
        applicationAssertEquals(application2, applicationResponses.get(1));

        criteria.setPageNumber(1);
        applicationResponses = applicationRestService.getAllApplication(criteria).getContent();

        assertEquals(1, applicationResponses.size());
        applicationAssertEquals(application3, applicationResponses.get(0));
    }

    /**
     * Проверка, что получение приложения по коду работает корректно
     */
    @Test
    public void getApplicationTest() {
        ApplicationResponse simpleApplicationResponse = ApplicationResponseBuilder.buildApplication1();
        ApplicationResponse applicationResponse =
                applicationRestService.getApplication(simpleApplicationResponse.getCode());

        applicationAssertEquals(simpleApplicationResponse, applicationResponse);
    }

    /**
     * Проверка, что получение приложения по несуществующему коду приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void getApplicationByNotExistsCodeTest() {
        applicationRestService.getApplication("bad-code");
    }

    /**
     * Проверка, что получение настроек приложения по несуществующему коду приводит к NotFoundException
     */
//    @Test(expected = NotFoundException.class)
//    public void getGroupedApplicationConfigByNotExistsCodeTest() {
//        applicationRestService.getGroupedApplicationConfig("bad-code");
//    }

    /**
     * Проверка, что в консул сохраняются ожидаемые данные
     */
//    @Test
//    public void saveApplicationConfigTest() {
//        String appCode = "app-auth";
//
//        IntStream.rangeClosed(0, 13).mapToObj(i -> {
//            ConfigForm configForm = new ConfigForm();
//            configForm.setCode("k" + i);
//            configForm.setValueType(ValueTypeEnum.STRING);
//            configForm.setApplicationCode(appCode);
//            return configForm;
//        }).forEach(configRestService::saveConfig);
//
//        // 0 - проверка, что в случае отсутствия настройка создается
//        // 1, 2, 3 - проверка, что настройки в консуле не меняются
//        // 4, 5, 6 - проверка, что настройки обновляются
//        // 7, 8, 9, 10 - проверка, что null или "" значения настроек приводят к их удалению из консула
//        // 11, 12, 13 - проверка, что если настройки нет на входе она также будет удалена из консула
//        Map<String, String> dataValue = new HashMap<>(Map.of(
//                "k0", "v0",
//                "k1", "v1", "k2", "v2", "k3", "v3",
//                "k4", "v444", "k5", "v555", "k6", "v666",
//                "k8", "", "k10", ""
//        ));
//        dataValue.put("k7", null);
//        dataValue.put("k9", null);
//        Map<String, Object> data = Map.of("data", dataValue);
//
//        Map<String, String> commonApplicationConfigKeyValues = Map.of(
//                "k1", "v1", "k2", "v2",
//                "k4", "v4", "k5", "v5",
//                "k7", "v7", "k8", "v8",
//                "k11", "v11", "k12", "v12"
//        );
//        Map<String, String> applicationConfigKeyValues = Map.of(
//                "k1", "v1", "k3", "v3",
//                "k4", "v4", "k6", "v6",
//                "k7", "v7", "k9", "v9",
//                "k11", "v11", "k13", "v13"
//        );
//
//        when(configValueService.getKeyValueList(defaultAppCode)).thenReturn(new HashMap<>(commonApplicationConfigKeyValues));
//        when(configValueService.getKeyValueList(appCode)).thenReturn(new HashMap<>(applicationConfigKeyValues));
//        doAnswer(inv -> {
//            new MockedConfigValueService().saveAllValues(inv.getArgument(0), inv.getArgument(1), inv.getArgument(2));
//            return null;
//        }).when(configValueService).saveAllValues(eq(appCode), anyMap(), anyMap());
//
//        applicationRestService.saveApplicationConfig(appCode, data);
//    }
//
//    /**
//     * Проверка, что сохранение настроек приложения по несуществующему коду приводит к NotFoundException
//     */
//    @Test(expected = NotFoundException.class)
//    public void saveApplicationConfigByNotExistsCodeTest() {
//        Map<String, Object> data = Map.of("data", Map.of());
//        applicationRestService.saveApplicationConfig("bad-code", data);
//    }
//
//    /**
//     * Проверка, что удаление настроек приложения по несуществующему коду приводит к NotFoundException
//     */
//    @Test(expected = NotFoundException.class)
//    public void deleteApplicationConfigByNotExistsCode() {
//        applicationRestService.deleteApplicationConfig("bad-code");
//    }

    private void applicationAssertEquals(ApplicationResponse expected, ApplicationResponse actual) {
        assertEquals(expected.getCode(), actual.getCode());
        assertEquals(expected.getName(), actual.getName());
    }
}