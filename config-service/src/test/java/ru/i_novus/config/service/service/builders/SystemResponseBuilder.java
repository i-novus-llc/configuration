package ru.i_novus.config.service.service.builders;


import ru.i_novus.system_application.api.model.Application;
import ru.i_novus.system_application.api.model.SystemResponse;

import java.util.Arrays;

public class SystemResponseBuilder {

    public static SystemResponse buildSystem() {
        SystemResponse system = new SystemResponse();
        system.setCode("system-security");
        system.setName("system security");
        Application application = new Application();
        application.setCode("app-security");
        application.setName("security");
        application.setSystemCode("system-security");
        system.setApplications(Arrays.asList(application));
        return system;
    }
}
