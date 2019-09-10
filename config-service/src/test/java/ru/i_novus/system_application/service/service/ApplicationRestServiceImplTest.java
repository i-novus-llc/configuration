package ru.i_novus.system_application.service.service;

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
import ru.i_novus.ConfigServiceApplication;
import ru.i_novus.system_application.api.criteria.ApplicationCriteria;
import ru.i_novus.system_application.api.model.ApplicationRequest;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.model.SystemRequest;
import ru.i_novus.system_application.api.service.ApplicationRestService;
import ru.i_novus.system_application.api.service.SystemRestService;
import ru.i_novus.system_application.service.service.builders.ApplicationRequestBuilder;
import ru.i_novus.system_application.service.service.builders.SystemRequestBuilder;

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
public class ApplicationRestServiceImplTest {

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
     * Проверка, что список приложений возвращается корректно
     */
    @Test
    public void getAllApplicationTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();
        systemRestService.saveSystem(systemRequest);
        SystemRequest systemRequest2 = SystemRequestBuilder.buildSystemRequest2();
        systemRestService.saveSystem(systemRequest2);
        ApplicationRequest applicationRequest = ApplicationRequestBuilder.buildApplicationRequest1();
        applicationRestService.saveApplication(applicationRequest);
        ApplicationRequest applicationRequest2 = ApplicationRequestBuilder.buildApplicationRequest2();
        applicationRestService.saveApplication(applicationRequest2);
        ApplicationRequest applicationRequest3 = ApplicationRequestBuilder.buildApplicationRequest3();
        applicationRestService.saveApplication(applicationRequest3);

        List<ApplicationResponse> applicationResponses =
                applicationRestService.getAllApplication(new ApplicationCriteria()).getContent();
        assertEquals(3, applicationResponses.size());
        applicationAssertEquals(applicationRequest, applicationResponses.get(0));
        applicationAssertEquals(applicationRequest2, applicationResponses.get(1));
        applicationAssertEquals(applicationRequest3, applicationResponses.get(2));

        applicationRestService.deleteApplication(applicationRequest.getCode());
        applicationRestService.deleteApplication(applicationRequest2.getCode());
        applicationRestService.deleteApplication(applicationRequest3.getCode());
        systemRestService.deleteSystem(systemRequest.getCode());
        systemRestService.deleteSystem(systemRequest2.getCode());
    }

    /**
     * Проверка, что фильтрация приложений по коду системы возвращается корректно
     */
    @Test
    public void getAllApplicationBySystemCodeTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();
        systemRestService.saveSystem(systemRequest);
        SystemRequest systemRequest2 = SystemRequestBuilder.buildSystemRequest2();
        systemRestService.saveSystem(systemRequest2);
        ApplicationRequest applicationRequest = ApplicationRequestBuilder.buildApplicationRequest1();
        applicationRestService.saveApplication(applicationRequest);
        ApplicationRequest applicationRequest2 = ApplicationRequestBuilder.buildApplicationRequest2();
        applicationRestService.saveApplication(applicationRequest2);
        ApplicationRequest applicationRequest3 = ApplicationRequestBuilder.buildApplicationRequest3();
        applicationRestService.saveApplication(applicationRequest3);

        ApplicationCriteria criteria = new ApplicationCriteria();
        criteria.setSystemCode("system-security");
        List<ApplicationResponse> applicationResponses =
                applicationRestService.getAllApplication(criteria).getContent();

        assertEquals(2, applicationResponses.size());
        applicationAssertEquals(applicationRequest, applicationResponses.get(0));
        applicationAssertEquals(applicationRequest2, applicationResponses.get(2));

        applicationRestService.deleteApplication(applicationRequest.getCode());
        applicationRestService.deleteApplication(applicationRequest2.getCode());
        applicationRestService.deleteApplication(applicationRequest3.getCode());
        systemRestService.deleteSystem(systemRequest.getCode());
        systemRestService.deleteSystem(systemRequest2.getCode());
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    public void getAllApplicationPaginationTest() {
        SystemRequest systemRequest = SystemRequestBuilder.buildSystemRequest1();
        systemRestService.saveSystem(systemRequest);
        SystemRequest systemRequest2 = SystemRequestBuilder.buildSystemRequest2();
        systemRestService.saveSystem(systemRequest2);
        ApplicationRequest applicationRequest = ApplicationRequestBuilder.buildApplicationRequest1();
        applicationRestService.saveApplication(applicationRequest);
        ApplicationRequest applicationRequest2 = ApplicationRequestBuilder.buildApplicationRequest2();
        applicationRestService.saveApplication(applicationRequest2);
        ApplicationRequest applicationRequest3 = ApplicationRequestBuilder.buildApplicationRequest3();
        applicationRestService.saveApplication(applicationRequest3);

        ApplicationCriteria criteria = new ApplicationCriteria();
        criteria.setPageSize(2);
        List<ApplicationResponse> applicationResponses =
                applicationRestService.getAllApplication(criteria).getContent();

        assertEquals(2, applicationResponses.size());
        applicationAssertEquals(applicationRequest, applicationResponses.get(0));
        applicationAssertEquals(applicationRequest2, applicationResponses.get(1));

        criteria.setPageNumber(1);
        applicationResponses = applicationRestService.getAllApplication(criteria).getContent();

        assertEquals(1, applicationResponses.size());
        applicationAssertEquals(applicationRequest3, applicationResponses.get(0));

        applicationRestService.deleteApplication(applicationRequest.getCode());
        applicationRestService.deleteApplication(applicationRequest2.getCode());
        applicationRestService.deleteApplication(applicationRequest3.getCode());
        systemRestService.deleteSystem(systemRequest.getCode());
        systemRestService.deleteSystem(systemRequest2.getCode());
    }

    /**
     * Проверка, что получение приложения по коду работает корректно
     */
    @Test
    public void getApplicationTest() {
        ApplicationRequest applicationRequest = ApplicationRequestBuilder.buildApplicationRequest1();
        applicationRestService.saveApplication(applicationRequest);
        ApplicationRequest applicationRequest2 = ApplicationRequestBuilder.buildApplicationRequest2();
        applicationRestService.saveApplication(applicationRequest2);

        ApplicationResponse applicationResponse =
                applicationRestService.getApplication(applicationRequest2.getCode());
        applicationAssertEquals(applicationRequest, applicationResponse);

        applicationRestService.deleteApplication(applicationRequest.getCode());
        applicationRestService.deleteApplication(applicationRequest2.getCode());
    }

    /**
     * Проверка, что сохранение приложения работает корректно
     */
    @Test
    public void saveApplicationTest() {
        ApplicationRequest applicationRequest = ApplicationRequestBuilder.buildApplicationRequest1();
        applicationRestService.saveApplication(applicationRequest);

        List<ApplicationResponse> applicationResponses = 
                applicationRestService.getAllApplication(new ApplicationCriteria()).getContent();
        assertEquals(1, applicationResponses.size());
        applicationAssertEquals(applicationRequest, applicationResponses.get(0));

        applicationRestService.deleteApplication(applicationRequest.getCode());
    }

    /**
     * Проверка, что сохранение приложения с уже существующим кодом приводит к RestException
     */
    @Test(expected = RestException.class)
    public void saveNotUniqueApplicationTest() {
        ApplicationRequest applicationRequest = ApplicationRequestBuilder.buildApplicationRequest1();
        applicationRestService.saveApplication(applicationRequest);

        try {
            applicationRestService.saveApplication(applicationRequest);
        } finally {
            applicationRestService.deleteApplication(applicationRequest.getCode());
        }
    }

    /**
     * Проверка, что удаление приложения работает корректно
     */
    @Test
    public void deleteApplicationTest() {
        ApplicationRequest applicationRequest = ApplicationRequestBuilder.buildApplicationRequest1();

        applicationRestService.saveApplication(applicationRequest);
        applicationRestService.deleteApplication(applicationRequest.getCode());

        assertTrue(applicationRestService.getAllApplication(new ApplicationCriteria()).isEmpty());
    }

    private void applicationAssertEquals(ApplicationRequest applicationRequest, ApplicationResponse applicationResponse) {
        assertEquals(applicationRequest.getCode(), applicationResponse.getCode());
        assertEquals(applicationRequest.getName(), applicationResponse.getName());
        assertEquals(applicationRequest.getSystemCode(), applicationResponse.getSystem().getCode());
    }
}