package ru.i_novus.system_application.service.service;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.platform.test.autoconfigure.DefinePort;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.ConfigServiceApplication;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.service.service.ConfigValueServiceConsulImpl;
import ru.i_novus.config.service.service.MockedConfigValueService;
import ru.i_novus.system_application.api.criteria.ApplicationCriteria;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.model.SimpleApplicationResponse;
import ru.i_novus.system_application.api.service.ApplicationRestService;
import ru.i_novus.system_application.service.service.builders.SimpleApplicationResponseBuilder;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ConfigServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@EnableJaxRsProxyClient(
        classes = ApplicationRestService.class,
        address = "http://localhost:${server.port}/api"
)
@DefinePort
@EnableEmbeddedPg
@TestPropertySource(properties = "spring.liquibase.change-log=classpath:/db/db.changelog-master-test.yaml")
public class ApplicationRestServiceImplTest {

    /**
     * @noinspection SpringJavaInjectionPointsAutowiringInspection
     */
    @Autowired
    @Qualifier("applicationRestServiceJaxRsProxyClient")
    private ApplicationRestService applicationRestService;

    @Mock
    private ConfigValueService configValueService;

    @Value("${spring.cloud.consul.config.defaultContext}")
    private String defaultAppCode;


    /**
     * Проверка, что список приложений возвращается корректно
     */
    @Test
    public void getAllApplicationTest() {
        SimpleApplicationResponse simpleApplicationResponse = SimpleApplicationResponseBuilder.buildSimpleApplicationResponse1();
        SimpleApplicationResponse simpleApplicationResponse2 = SimpleApplicationResponseBuilder.buildSimpleApplicationResponse2();
        SimpleApplicationResponse simpleApplicationResponse3 = SimpleApplicationResponseBuilder.buildSimpleApplicationResponse3();

        List<ApplicationResponse> applicationResponses =
                applicationRestService.getAllApplication(new ApplicationCriteria()).getContent();
        assertEquals(3, applicationResponses.size());
        applicationAssertEquals(simpleApplicationResponse, applicationResponses.get(0));
        applicationAssertEquals(simpleApplicationResponse2, applicationResponses.get(1));
        applicationAssertEquals(simpleApplicationResponse3, applicationResponses.get(2));
    }

    /**
     * Проверка, что фильтрация приложений по коду системы возвращается корректно
     */
    @Test
    public void getAllApplicationBySystemCodeTest() {
        SimpleApplicationResponse simpleApplicationResponse = SimpleApplicationResponseBuilder.buildSimpleApplicationResponse2();
        SimpleApplicationResponse simpleApplicationResponse2 = SimpleApplicationResponseBuilder.buildSimpleApplicationResponse3();

        ApplicationCriteria criteria = new ApplicationCriteria();
        criteria.setSystemCode("system-security");
        List<ApplicationResponse> applicationResponses =
                applicationRestService.getAllApplication(criteria).getContent();

        assertEquals(2, applicationResponses.size());
        applicationAssertEquals(simpleApplicationResponse, applicationResponses.get(0));
        applicationAssertEquals(simpleApplicationResponse2, applicationResponses.get(1));
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    public void getAllApplicationPaginationTest() {
        SimpleApplicationResponse simpleApplicationResponse = SimpleApplicationResponseBuilder.buildSimpleApplicationResponse1();
        SimpleApplicationResponse simpleApplicationResponse2 = SimpleApplicationResponseBuilder.buildSimpleApplicationResponse2();
        SimpleApplicationResponse simpleApplicationResponse3 = SimpleApplicationResponseBuilder.buildSimpleApplicationResponse3();

        ApplicationCriteria criteria = new ApplicationCriteria();
        criteria.setPageSize(2);
        List<ApplicationResponse> applicationResponses =
                applicationRestService.getAllApplication(criteria).getContent();

        assertEquals(2, applicationResponses.size());
        applicationAssertEquals(simpleApplicationResponse, applicationResponses.get(0));
        applicationAssertEquals(simpleApplicationResponse2, applicationResponses.get(1));

        criteria.setPageNumber(1);
        applicationResponses = applicationRestService.getAllApplication(criteria).getContent();

        assertEquals(1, applicationResponses.size());
        applicationAssertEquals(simpleApplicationResponse3, applicationResponses.get(0));
    }

    /**
     * Проверка, что получение приложения по коду работает корректно
     */
    @Test
    public void getApplicationTest() {
        SimpleApplicationResponse simpleApplicationResponse = SimpleApplicationResponseBuilder.buildSimpleApplicationResponse1();
        ApplicationResponse applicationResponse =
                applicationRestService.getApplication(simpleApplicationResponse.getCode());

        applicationAssertEquals(simpleApplicationResponse, applicationResponse);
    }

    /**
     * Проверка, что в консул сохраняются ожидаемые данные
     */
    @Test
    public void saveApplicationConfigTest() {
        String appCode = "appCode";

        Map<String, String> dataValue = Map.of(
                "k1", "v1", "k2", "v2",
                "k3", "v3", "k4", "v444",
                "k5", "v555", "k6", "v666"
        );
        Map<String, Object> data = Map.of("data", dataValue);

        Map<String, String> commonApplicationConfigKeyValues = Map.of(
                "k1", "v1", "k2", "v2",
                "k4", "v4", "k5", "v5"
        );
        Map<String, String> applicationConfigKeyValues = Map.of(
                "k1", "v1", "k3", "v3",
                "k4", "v4", "k6", "v6"
        );

        when(configValueService.getKeyValueList(defaultAppCode)).thenReturn(commonApplicationConfigKeyValues);
        when(configValueService.getKeyValueList(appCode)).thenReturn(applicationConfigKeyValues);
        configValueService = new MockedConfigValueService();

        applicationRestService.saveApplicationConfig(appCode, data);
    }

    private void applicationAssertEquals(SimpleApplicationResponse simpleApplicationResponse, ApplicationResponse applicationResponse) {
        assertEquals(simpleApplicationResponse.getCode(), applicationResponse.getCode());
        assertEquals(simpleApplicationResponse.getName(), applicationResponse.getName());
        assertEquals(simpleApplicationResponse.getSystemCode(), applicationResponse.getSystem().getCode());
    }
}