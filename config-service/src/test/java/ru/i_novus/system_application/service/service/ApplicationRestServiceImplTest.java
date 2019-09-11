package ru.i_novus.system_application.service.service;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import net.n2oapp.platform.test.autoconfigure.DefinePort;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.ConfigServiceApplication;
import ru.i_novus.system_application.api.criteria.ApplicationCriteria;
import ru.i_novus.system_application.api.model.ApplicationRequest;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.service.ApplicationRestService;
import ru.i_novus.system_application.service.service.builders.ApplicationRequestBuilder;

import java.util.List;

import static org.junit.Assert.assertEquals;

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


    /**
     * Проверка, что список приложений возвращается корректно
     */
    @Test
    public void getAllApplicationTest() {
        ApplicationRequest applicationRequest = ApplicationRequestBuilder.buildApplicationRequest1();
        ApplicationRequest applicationRequest2 = ApplicationRequestBuilder.buildApplicationRequest2();
        ApplicationRequest applicationRequest3 = ApplicationRequestBuilder.buildApplicationRequest3();

        List<ApplicationResponse> applicationResponses =
                applicationRestService.getAllApplication(new ApplicationCriteria()).getContent();
        assertEquals(3, applicationResponses.size());
        applicationAssertEquals(applicationRequest, applicationResponses.get(0));
        applicationAssertEquals(applicationRequest2, applicationResponses.get(1));
        applicationAssertEquals(applicationRequest3, applicationResponses.get(2));
    }

    /**
     * Проверка, что фильтрация приложений по коду системы возвращается корректно
     */
    @Test
    public void getAllApplicationBySystemCodeTest() {
        ApplicationRequest applicationRequest = ApplicationRequestBuilder.buildApplicationRequest2();
        ApplicationRequest applicationRequest2 = ApplicationRequestBuilder.buildApplicationRequest3();

        ApplicationCriteria criteria = new ApplicationCriteria();
        criteria.setSystemCode("system-security");
        List<ApplicationResponse> applicationResponses =
                applicationRestService.getAllApplication(criteria).getContent();

        assertEquals(2, applicationResponses.size());
        applicationAssertEquals(applicationRequest, applicationResponses.get(0));
        applicationAssertEquals(applicationRequest2, applicationResponses.get(1));
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    public void getAllApplicationPaginationTest() {
        ApplicationRequest applicationRequest = ApplicationRequestBuilder.buildApplicationRequest1();
        ApplicationRequest applicationRequest2 = ApplicationRequestBuilder.buildApplicationRequest2();
        ApplicationRequest applicationRequest3 = ApplicationRequestBuilder.buildApplicationRequest3();

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
    }

    /**
     * Проверка, что получение приложения по коду работает корректно
     */
    @Test
    public void getApplicationTest() {
        ApplicationRequest applicationRequest = ApplicationRequestBuilder.buildApplicationRequest1();
        ApplicationResponse applicationResponse =
                applicationRestService.getApplication(applicationRequest.getCode());

        applicationAssertEquals(applicationRequest, applicationResponse);
    }

    private void applicationAssertEquals(ApplicationRequest applicationRequest, ApplicationResponse applicationResponse) {
        assertEquals(applicationRequest.getCode(), applicationResponse.getCode());
        assertEquals(applicationRequest.getName(), applicationResponse.getName());
        assertEquals(applicationRequest.getSystemCode(), applicationResponse.getSystem().getCode());
    }
}