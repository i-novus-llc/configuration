package ru.i_novus.configuration.config.service.builders;

import ru.i_novus.config.api.model.ApplicationResponse;

public class ApplicationResponseBuilder {

    public static ApplicationResponse buildApplication1() {
        ApplicationResponse application = new ApplicationResponse();
        application.setCode("app-auth");
        application.setName("auth");
        return application;
    }

    public static ApplicationResponse buildApplication2() {
        ApplicationResponse application = new ApplicationResponse();
        application.setCode("app-security");
        application.setName("security");
        return application;
    }

    public static ApplicationResponse buildApplication3() {
        ApplicationResponse application = new ApplicationResponse();
        application.setCode("app-test-security");
        application.setName("test security");
        return application;
    }
}
