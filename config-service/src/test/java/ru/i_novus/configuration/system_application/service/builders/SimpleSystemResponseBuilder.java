package ru.i_novus.configuration.system_application.service.builders;

import ru.i_novus.system_application.api.model.SimpleSystemResponse;

public class SimpleSystemResponseBuilder {

    public static SimpleSystemResponse buildSimpleSystemResponse1() {
        SimpleSystemResponse system = new SimpleSystemResponse();
        system.setCode("system-auth");
        system.setName("system auth");
        return system;
    }

    public static SimpleSystemResponse buildSimpleSystemResponse2() {
        SimpleSystemResponse system = new SimpleSystemResponse();
        system.setCode("system-security");
        system.setName("system security");
        return system;
    }

    public static SimpleSystemResponse buildSimpleSystemResponse3() {
        SimpleSystemResponse system = new SimpleSystemResponse();
        system.setCode("test");
        system.setName("test");
        return system;
    }
}
