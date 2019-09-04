package ru.i_novus.config.service.service.builders;

import ru.i_novus.config.service.model.SimpleApplication;
import ru.i_novus.config.service.model.System;

import java.util.Arrays;

public class SystemBuilder {

    public static System buildSystem() {
        System system = new System();
        system.setCode("system-security");
        system.setName("system security");
        SimpleApplication application = new SimpleApplication();
        application.setCode("app-security");
        application.setName("security");
        application.setSystemCode("system-security");
        system.setApplications(Arrays.asList(application));
        return system;
    }
}
