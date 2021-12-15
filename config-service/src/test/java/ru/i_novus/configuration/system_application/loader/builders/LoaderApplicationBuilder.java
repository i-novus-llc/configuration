package ru.i_novus.configuration.system_application.loader.builders;

import ru.i_novus.system_application.api.model.SimpleApplicationResponse;

public class LoaderApplicationBuilder {

    public static SimpleApplicationResponse buildApplication1() {
        SimpleApplicationResponse application = new SimpleApplicationResponse();
        application.setCode("app1");
        application.setName("name1");
        return application;
    }

    public static SimpleApplicationResponse buildApplication2() {
        SimpleApplicationResponse application = new SimpleApplicationResponse();
        application.setCode("app2");
        application.setName("name2");
        return application;
    }

    public static SimpleApplicationResponse buildApplication2Updated() {
        SimpleApplicationResponse application = new SimpleApplicationResponse();
        application.setCode("app2");
        application.setName("name2-new");
        return application;
    }

    public static SimpleApplicationResponse buildApplication3() {
        SimpleApplicationResponse application = new SimpleApplicationResponse();
        application.setCode("app3");
        application.setName("name3");
        return application;
    }
}
