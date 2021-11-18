package ru.i_novus.configuration.system_application.service;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.i_novus.TestApp;
import ru.i_novus.configuration.system_application.service.builders.SimpleSystemResponseBuilder;
import ru.i_novus.system_application.api.criteria.SystemCriteria;
import ru.i_novus.system_application.api.model.SimpleSystemResponse;
import ru.i_novus.system_application.api.model.SystemResponse;
import ru.i_novus.system_application.api.service.SystemRestService;

import javax.ws.rs.NotFoundException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableEmbeddedPg
public class SystemRestServiceImplTest {

    @Autowired
    private SystemRestService systemRestService;

    @Value("${config.common.system.code}")
    private String commonSystemCode;


    /**
     * Проверка, что список систем возвращается корректно
     */
    @Test
    public void getAllSystemTest() {
        SimpleSystemResponse simpleSystemResponse = SimpleSystemResponseBuilder.buildSimpleSystemResponse1();
        SimpleSystemResponse simpleSystemResponse2 = SimpleSystemResponseBuilder.buildSimpleSystemResponse2();
        SimpleSystemResponse simpleSystemResponse3 = SimpleSystemResponseBuilder.buildSimpleSystemResponse3();

        List<SystemResponse> systemResponses = systemRestService.getAllSystem(new SystemCriteria()).getContent();
        assertEquals(4, systemResponses.size());
        systemAssertEquals(simpleSystemResponse, systemResponses.get(1));
        systemAssertEquals(simpleSystemResponse2, systemResponses.get(2));
        systemAssertEquals(simpleSystemResponse3, systemResponses.get(3));
    }

    /**
     * Проверка, что фильтрация систем по коду приложения работает корректно
     */
    @Test
    public void getAllSystemByAppCodeTest() {
        SimpleSystemResponse simpleSystemResponse = SimpleSystemResponseBuilder.buildSimpleSystemResponse2();

        SystemCriteria criteria = new SystemCriteria();
        criteria.setAppCode("sec");
        List<SystemResponse> systemResponses = systemRestService.getAllSystem(criteria).getContent();

        assertEquals(1, systemResponses.size());
        systemAssertEquals(simpleSystemResponse, systemResponses.get(0));
    }


    /**
     * Проверка, что фильтрация систем по кодам работает корректно
     */
    @Test
    public void getAllSystemByCodesTest() {
        SimpleSystemResponse simpleSystemResponse = SimpleSystemResponseBuilder.buildSimpleSystemResponse1();
        SimpleSystemResponse simpleSystemResponse2 = SimpleSystemResponseBuilder.buildSimpleSystemResponse2();

        SystemCriteria criteria = new SystemCriteria();
        criteria.setCodes(Arrays.asList("system-security", "system-auth"));
        List<SystemResponse> systemResponses = systemRestService.getAllSystem(criteria).getContent();

        assertEquals(2, systemResponses.size());
        systemAssertEquals(simpleSystemResponse, systemResponses.get(0));
        systemAssertEquals(simpleSystemResponse2, systemResponses.get(1));
    }

    /**
     * Проверка, что пагинация работает корректно
     */
    @Test
    public void getAllSystemPaginationTest() {
        SimpleSystemResponse simpleSystemResponse = SimpleSystemResponseBuilder.buildSimpleSystemResponse1();
        SimpleSystemResponse simpleSystemResponse2 = SimpleSystemResponseBuilder.buildSimpleSystemResponse2();
        SimpleSystemResponse simpleSystemResponse3 = SimpleSystemResponseBuilder.buildSimpleSystemResponse3();

        SystemCriteria criteria = new SystemCriteria();
        criteria.setPageSize(2);
        List<SystemResponse> systemResponses = systemRestService.getAllSystem(criteria).getContent();

        assertEquals(2, systemResponses.size());
        assertEquals(commonSystemCode, systemResponses.get(0).getCode());
        systemAssertEquals(simpleSystemResponse, systemResponses.get(1));

        criteria.setPageNumber(1);
        systemResponses = systemRestService.getAllSystem(criteria).getContent();

        assertEquals(2, systemResponses.size());
        systemAssertEquals(simpleSystemResponse2, systemResponses.get(0));
        systemAssertEquals(simpleSystemResponse3, systemResponses.get(1));
    }

    /**
     * Проверка, что получение системы по коду работает корректно
     */
    @Test
    public void getSystemTest() {
        SimpleSystemResponse simpleSystemResponse = SimpleSystemResponseBuilder.buildSimpleSystemResponse1();
        SystemResponse systemResponse = systemRestService.getSystem(simpleSystemResponse.getCode());

        systemAssertEquals(simpleSystemResponse, systemResponse);
    }

    /**
     * Проверка, что получение системы по несуществующему коду приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void getSystemByNotExistsCodeTest() {
        systemRestService.getSystem("bad-code");
    }

    private void systemAssertEquals(SimpleSystemResponse simpleSystemResponse, SystemResponse systemResponse) {
        assertEquals(simpleSystemResponse.getCode(), systemResponse.getCode());
        assertEquals(simpleSystemResponse.getName(), systemResponse.getName());
        assertEquals(simpleSystemResponse.getDescription(), systemResponse.getDescription());
    }
}