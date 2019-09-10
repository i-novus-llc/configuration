package ru.i_novus.config.service.service.builders;


import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.model.SystemRequest;

public class ApplicationResponseBuilder {

    public static ApplicationResponse buildApplicationResponse1() {
        ApplicationResponse application = new ApplicationResponse();
        application.setCode("app-security");
        application.setName("security");
        SystemRequest system = new SystemRequest();
        system.setCode("system-security");
        system.setName("system security");
        application.setSystem(system);
        return application;
    }

    public static ApplicationResponse buildApplicationResponse2() {
        ApplicationResponse application = new ApplicationResponse();
        application.setCode("app-auth");
        application.setName("auth");
        SystemRequest system = new SystemRequest();
        system.setCode("system-auth");
        system.setName("system auth");
        application.setSystem(system);
        return application;
    }
}
