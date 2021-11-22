package ru.i_novus.configuration.system_application.service.builders;

import ru.i_novus.system_application.api.model.SimpleApplicationResponse;

public class SimpleApplicationResponseBuilder {

    public static SimpleApplicationResponse buildSimpleApplicationResponse1() {
        SimpleApplicationResponse application = new SimpleApplicationResponse();
        application.setCode("app-auth");
        application.setName("auth");
        application.setSystemCode("system-auth");
        return application;
    }

    public static SimpleApplicationResponse buildSimpleApplicationResponse2() {
        SimpleApplicationResponse application = new SimpleApplicationResponse();
        application.setCode("app-security");
        application.setName("security");
        application.setSystemCode("system-security");
        return application;
    }

    public static SimpleApplicationResponse buildSimpleApplicationResponse3() {
        SimpleApplicationResponse application = new SimpleApplicationResponse();
        application.setCode("app-test-security");
        application.setName("test security");
        application.setSystemCode("system-security");
        return application;
    }
}
