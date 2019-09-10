package ru.i_novus.system_application.service.service.builders;

import ru.i_novus.system_application.api.model.SystemRequest;

public class SystemRequestBuilder {

    public static SystemRequest buildSystemRequest1() {
        SystemRequest system = new SystemRequest();
        system.setCode("system-security");
        system.setName("system security");
        return system;
    }

    public static SystemRequest buildSystemRequest2() {
        SystemRequest system = new SystemRequest();
        system.setCode("system-auth");
        system.setName("system auth");
        return system;
    }

    public static SystemRequest buildSystemRequest3() {
        SystemRequest system = new SystemRequest();
        system.setCode("something");
        system.setName("something");
        return system;
    }
}
