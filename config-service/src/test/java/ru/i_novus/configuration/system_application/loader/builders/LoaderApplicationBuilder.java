package ru.i_novus.configuration.system_application.loader.builders;

import ru.i_novus.system_application.api.model.SimpleApplicationResponse;

public class LoaderApplicationBuilder {

    public static SimpleApplicationResponse buildApplication1() {
        SimpleApplicationResponse application = new SimpleApplicationResponse();
        application.setCode("app1");
        application.setName("name1");
        application.setSystemCode("system-auth");
        return application;
    }

    public static SimpleApplicationResponse buildApplication2() {
        SimpleApplicationResponse application = new SimpleApplicationResponse();
        application.setCode("app2");
        application.setName("name2");
        application.setSystemCode("test");
        return application;
    }

    public static SimpleApplicationResponse buildApplication2Updated() {
        SimpleApplicationResponse application = new SimpleApplicationResponse();
        application.setCode("app2");
        application.setName("name2-new");
        application.setSystemCode("system-security");
        return application;
    }

    public static SimpleApplicationResponse buildApplication3() {
        SimpleApplicationResponse application = new SimpleApplicationResponse();
        application.setCode("app3");
        application.setName("name3");
        application.setSystemCode("test");
        return application;
    }
}
