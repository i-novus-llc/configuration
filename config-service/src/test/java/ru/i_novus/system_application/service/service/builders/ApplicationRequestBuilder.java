package ru.i_novus.system_application.service.service.builders;

import ru.i_novus.system_application.api.model.ApplicationRequest;

public class ApplicationRequestBuilder {
    public static ApplicationRequest buildApplicationRequest1() {
        ApplicationRequest application = new ApplicationRequest();
        application.setCode("app-security");
        application.setName("security");
        application.setSystemCode("system-security");
        return application;
    }

    public static ApplicationRequest buildApplicationRequest2() {
        ApplicationRequest application = new ApplicationRequest();
        application.setCode("app-auth");
        application.setName("auth");
        application.setSystemCode("system-auth");
        return application;
    }

    public static ApplicationRequest buildApplicationRequest3() {
        ApplicationRequest application = new ApplicationRequest();
        application.setCode("app-base-security");
        application.setName("base security");
        application.setSystemCode("system-security");
        return application;
    }
}
