package ru.i_novus.system_application.service.service;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.platform.test.autoconfigure.DefinePort;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.ConfigServiceApplication;
import ru.i_novus.system_application.api.criteria.SystemCriteria;
import ru.i_novus.system_application.api.model.SystemRequest;
import ru.i_novus.system_application.api.model.SystemResponse;
import ru.i_novus.system_application.api.service.SystemRestService;
import ru.i_novus.system_application.service.CommonSystemResponse;
import ru.i_novus.system_application.service.service.builders.SystemRequestBuilder;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ConfigServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableJaxRsProxyClient(
        classes = SystemRestService.class,
        address = "http://localhost:${server.port}/api"
)
@DefinePort
@EnableEmbeddedPg
@TestPropertySource(properties = "spring.liquibase.change-log=classpath:/db/db.changelog-master-test.yaml")
public class SystemRestServiceImplTest {

    /**
     * @noinspection SpringJavaInjectionPointsAutowiringInspection
     */
    @Autowired
    @Qualifier("systemRestServiceJaxRsProxyClient")
    private SystemRestService systemRestService;


    /**
     * Проверка, что список систем возвращается корректно
     */
    @Test
    public void getAllSystemTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();
        SystemRequest systemRequest2 = SystemRequestBuilder.buildSystemRequest2();
        SystemRequest systemRequest3 = SystemRequestBuilder.buildSystemRequest3();

        List<SystemResponse> systemResponses = systemRestService.getAllSystem(new SystemCriteria()).getContent();
        assertEquals(4, systemResponses.size());
        systemAssertEquals(systemRequest, systemResponses.get(1));
        systemAssertEquals(systemRequest2, systemResponses.get(2));
        systemAssertEquals(systemRequest3, systemResponses.get(3));
    }

    /**
     * Проверка, что фильтрация систем по коду приложения работает корректно
     */
    @Test
    @Ignore
    public void getAllSystemByAppCodeTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();

        SystemCriteria criteria = new SystemCriteria();
        criteria.setAppCode("app-security");
        List<SystemResponse> systemResponses = systemRestService.getAllSystem(criteria).getContent();

        assertEquals(1, systemResponses.size());
        systemAssertEquals(systemRequest, systemResponses.get(0));
    }


    /**
     * Проверка, что фильтрация систем по кодам работает корректно
     */
    @Test
    public void getAllSystemByCodesTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();
        SystemRequest systemRequest2 = SystemRequestBuilder.buildSystemRequest2();

        SystemCriteria criteria = new SystemCriteria();
        criteria.setCodes(Arrays.asList("system-security", "system-auth"));
        List<SystemResponse> systemResponses = systemRestService.getAllSystem(criteria).getContent();

        assertEquals(2, systemResponses.size());
        systemAssertEquals(systemRequest, systemResponses.get(0));
        systemAssertEquals(systemRequest2, systemResponses.get(1));
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    public void getAllSystemPaginationTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();
        SystemRequest systemRequest2 = SystemRequestBuilder.buildSystemRequest2();
        SystemRequest systemRequest3 = SystemRequestBuilder.buildSystemRequest3();

        SystemCriteria criteria = new SystemCriteria();
        criteria.setPageSize(2);
        List<SystemResponse> systemResponses = systemRestService.getAllSystem(criteria).getContent();

        assertEquals(2, systemResponses.size());
        assertEquals(new CommonSystemResponse().getCode(), systemResponses.get(0).getCode());
        systemAssertEquals(systemRequest, systemResponses.get(1));

        criteria.setPageNumber(1);
        systemResponses = systemRestService.getAllSystem(criteria).getContent();

        assertEquals(2, systemResponses.size());
        systemAssertEquals(systemRequest2, systemResponses.get(0));
        systemAssertEquals(systemRequest3, systemResponses.get(1));
    }

    /**
     * Проверка, что получение системы по коду работает корректно
     */
    @Test
    public void getSystemTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();
        SystemResponse systemResponse = systemRestService.getSystem(systemRequest.getCode());

        systemAssertEquals(systemRequest, systemResponse);
    }

    private void systemAssertEquals(SystemRequest systemRequest, SystemResponse systemResponse) {
        assertEquals(systemRequest.getCode(), systemResponse.getCode());
        assertEquals(systemRequest.getName(), systemResponse.getName());
        assertEquals(systemRequest.getDescription(), systemResponse.getDescription());
    }
}