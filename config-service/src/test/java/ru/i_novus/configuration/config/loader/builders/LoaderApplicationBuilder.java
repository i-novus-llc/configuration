package ru.i_novus.configuration.config.loader.builders;

import ru.i_novus.config.api.model.ApplicationResponse;

public class LoaderApplicationBuilder {

    public static ApplicationResponse buildApplication1() {
        ApplicationResponse application = new ApplicationResponse();
        application.setCode("app1");
        application.setName("name1");
        return application;
    }

    public static ApplicationResponse buildApplication2() {
        ApplicationResponse application = new ApplicationResponse();
        application.setCode("app2");
        application.setName("name2");
        return application;
    }

    public static ApplicationResponse buildApplication2Updated() {
        ApplicationResponse application = new ApplicationResponse();
        application.setCode("app2");
        application.setName("name2-new");
        return application;
    }

    public static ApplicationResponse buildApplication3() {
        ApplicationResponse application = new ApplicationResponse();
        application.setCode("app3");
        application.setName("name3");
        return application;
    }
}
