package ru.i_novus.config.service.service.builders;


import ru.i_novus.config.service.model.Application;
import ru.i_novus.config.service.model.SimpleSystem;

public class ApplicationBuilder {

    public static Application buildApplication1() {
        Application application = new Application();
        application.setCode("app-security");
        application.setName("security");
        SimpleSystem simpleSystem = new SimpleSystem();
        simpleSystem.setCode("system-security");
        simpleSystem.setName("system security");
        application.setSystem(simpleSystem);
        return application;
    }

    public static Application buildApplication2() {
        Application application = new Application();
        application.setCode("app-auth");
        application.setName("auth");
        SimpleSystem simpleSystem = new SimpleSystem();
        simpleSystem.setCode("system-auth");
        simpleSystem.setName("system auth");
        application.setSystem(simpleSystem);
        return application;
    }
}
