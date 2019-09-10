package ru.i_novus.system_application.service.service;

import net.n2oapp.platform.jaxrs.RestException;
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
import ru.i_novus.ConfigServiceApplication;
import ru.i_novus.system_application.api.criteria.SystemCriteria;
import ru.i_novus.system_application.api.model.ApplicationRequest;
import ru.i_novus.system_application.api.model.SystemRequest;
import ru.i_novus.system_application.api.model.SystemResponse;
import ru.i_novus.system_application.api.service.ApplicationRestService;
import ru.i_novus.system_application.api.service.SystemRestService;
import ru.i_novus.system_application.service.service.builders.ApplicationRequestBuilder;
import ru.i_novus.system_application.service.service.builders.SystemRequestBuilder;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ConfigServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableJaxRsProxyClient(
        classes = {
                SystemRestService.class,
                ApplicationRestService.class
        },
        address = "http://localhost:${server.port}/api"
)
@DefinePort
@EnableEmbeddedPg
public class SystemRestServiceImplTest {

    /**
     * @noinspection SpringJavaInjectionPointsAutowiringInspection
     */
    @Autowired
    @Qualifier("systemRestServiceJaxRsProxyClient")
    private SystemRestService systemRestService;

    @Autowired
    @Qualifier("applicationRestServiceJaxRsProxyClient")
    private ApplicationRestService applicationRestService;


    /**
     * Проверка, что список систем возвращается корректно
     */
    @Test
    public void getAllSystemTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();
        systemRestService.saveSystem(systemRequest);
        SystemRequest systemRequest2 = SystemRequestBuilder.buildSystemRequest2();
        systemRestService.saveSystem(systemRequest2);
        SystemRequest systemRequest3 = SystemRequestBuilder.buildSystemRequest3();
        systemRestService.saveSystem(systemRequest3);

        List<SystemResponse> systemResponses = systemRestService.getAllSystem(new SystemCriteria()).getContent();
        assertEquals(3, systemResponses.size());
        systemAssertEquals(systemRequest, systemResponses.get(0));
        systemAssertEquals(systemRequest2, systemResponses.get(1));
        systemAssertEquals(systemRequest3, systemResponses.get(2));

        systemRestService.deleteSystem(systemRequest.getCode());
        systemRestService.deleteSystem(systemRequest2.getCode());
        systemRestService.deleteSystem(systemRequest3.getCode());
    }

    /**
     * Проверка, что фильтрация систем по коду приложения работает корректно
     */
    @Test
    @Ignore
    public void getAllSystemByAppCodeTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();
        systemRestService.saveSystem(systemRequest);
        SystemRequest systemRequest2 = SystemRequestBuilder.buildSystemRequest2();
        systemRestService.saveSystem(systemRequest2);
        SystemRequest systemRequest3 = SystemRequestBuilder.buildSystemRequest3();
        systemRestService.saveSystem(systemRequest3);
        ApplicationRequest applicationRequest = ApplicationRequestBuilder.buildApplicationRequest1();
        applicationRestService.saveApplication(applicationRequest);
        ApplicationRequest applicationRequest2 = ApplicationRequestBuilder.buildApplicationRequest2();
        applicationRestService.saveApplication(applicationRequest2);

        SystemCriteria criteria = new SystemCriteria();
        criteria.setAppCode("app-security");
        List<SystemResponse> systemResponses = systemRestService.getAllSystem(criteria).getContent();

        assertEquals(1, systemResponses.size());
        systemAssertEquals(systemRequest, systemResponses.get(0));

        systemRestService.deleteSystem(systemRequest.getCode());
        systemRestService.deleteSystem(systemRequest2.getCode());
        systemRestService.deleteSystem(systemRequest3.getCode());
        applicationRestService.deleteApplication(applicationRequest.getCode());
        applicationRestService.deleteApplication(applicationRequest2.getCode());
    }


    /**
     * Проверка, что фильтрация систем по кодам работает корректно
     */
    @Test
    public void getAllSystemByCodesTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();
        systemRestService.saveSystem(systemRequest);
        SystemRequest systemRequest2 = SystemRequestBuilder.buildSystemRequest2();
        systemRestService.saveSystem(systemRequest2);
        SystemRequest systemRequest3 = SystemRequestBuilder.buildSystemRequest3();
        systemRestService.saveSystem(systemRequest3);

        SystemCriteria criteria = new SystemCriteria();
        criteria.setCodes(Arrays.asList("system-security", "system-auth"));
        List<SystemResponse> systemResponses = systemRestService.getAllSystem(criteria).getContent();

        assertEquals(2, systemResponses.size());
        systemAssertEquals(systemRequest, systemResponses.get(0));
        systemAssertEquals(systemRequest2, systemResponses.get(1));

        systemRestService.deleteSystem(systemRequest.getCode());
        systemRestService.deleteSystem(systemRequest2.getCode());
        systemRestService.deleteSystem(systemRequest3.getCode());
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    public void getAllSystemPaginationTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();
        systemRestService.saveSystem(systemRequest);
        SystemRequest systemRequest2 = SystemRequestBuilder.buildSystemRequest2();
        systemRestService.saveSystem(systemRequest2);
        SystemRequest systemRequest3 = SystemRequestBuilder.buildSystemRequest3();
        systemRestService.saveSystem(systemRequest3);

        SystemCriteria criteria = new SystemCriteria();
        criteria.setPageSize(2);
        List<SystemResponse> systemResponses = systemRestService.getAllSystem(criteria).getContent();

        assertEquals(2, systemResponses.size());
        systemAssertEquals(systemRequest, systemResponses.get(0));
        systemAssertEquals(systemRequest2, systemResponses.get(1));

        criteria.setPageNumber(1);
        systemResponses = systemRestService.getAllSystem(criteria).getContent();

        assertEquals(1, systemResponses.size());
        systemAssertEquals(systemRequest3, systemResponses.get(0));

        systemRestService.deleteSystem(systemRequest.getCode());
        systemRestService.deleteSystem(systemRequest2.getCode());
        systemRestService.deleteSystem(systemRequest3.getCode());
    }

    /**
     * Проверка, что получение системы по коду работает корректно
     */
    @Test
    public void getSystemTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();
        systemRestService.saveSystem(systemRequest);
        SystemRequest systemRequest2 = SystemRequestBuilder.buildSystemRequest2();
        systemRestService.saveSystem(systemRequest2);

        SystemResponse systemResponse = systemRestService.getSystem(systemRequest2.getCode());
        systemAssertEquals(systemRequest, systemResponse);

        systemRestService.deleteSystem(systemRequest.getCode());
        systemRestService.deleteSystem(systemRequest2.getCode());
    }

    /**
     * Проверка, что сохранение системы работает корректно
     */
    @Test
    public void saveSystemTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();
        systemRestService.saveSystem(systemRequest);

        List<SystemResponse> systemResponses = systemRestService.getAllSystem(new SystemCriteria()).getContent();
        assertEquals(1, systemResponses.size());
        systemAssertEquals(systemRequest, systemResponses.get(0));

        systemRestService.deleteSystem(systemRequest.getCode());
    }

    /**
     * Проверка, что сохранение системы с уже существующим кодом приводит к RestException
     */
    @Test(expected = RestException.class)
    public void saveNotUniqueSystemTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();
        systemRestService.saveSystem(systemRequest);

        try {
            systemRestService.saveSystem(systemRequest);
        } finally {
            systemRestService.deleteSystem(systemRequest.getCode());
        }
    }

    /**
     * Проверка, что удаление системы работает корректно
     */
    @Test
    public void deleteSystemTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();

        systemRestService.saveSystem(systemRequest);
        systemRestService.deleteSystem(systemRequest.getCode());

        assertTrue(systemRestService.getAllSystem(new SystemCriteria()).isEmpty());
    }

    private void systemAssertEquals(SystemRequest systemRequest, SystemResponse systemResponse) {
        assertEquals(systemRequest.getCode(), systemResponse.getCode());
        assertEquals(systemRequest.getName(), systemResponse.getName());
        assertEquals(systemRequest.getDescription(), systemResponse.getDescription());
    }
}