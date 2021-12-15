package ru.i_novus.configuration.system_application.loader.builders;

import ru.i_novus.system_application.api.model.SimpleSystemResponse;

public class LoaderSystemBuilder {

    public static SimpleSystemResponse buildSystem1() {
        SimpleSystemResponse system = new SimpleSystemResponse();
        system.setCode("sys1");
        system.setName("name1");
        system.setDescription("desc1");
        return system;
    }

    public static SimpleSystemResponse buildSystem2() {
        SimpleSystemResponse system = new SimpleSystemResponse();
        system.setCode("sys2");
        system.setName("name2");
        system.setDescription("desc2");
        return system;
    }

    public static SimpleSystemResponse buildSystem2Updated() {
        SimpleSystemResponse system = new SimpleSystemResponse();
        system.setCode("sys2");
        system.setName("name2-new");
        system.setDescription("desc2-new");
        return system;
    }

    public static SimpleSystemResponse buildSystem3() {
        SimpleSystemResponse system = new SimpleSystemResponse();
        system.setCode("sys3");
        system.setName("name3");
        system.setDescription("desc3");
        return system;
    }
}
