package ru.i_novus.config.service.service.builders;

import ru.i_novus.config.api.model.ConfigRequest;

public class ConfigRequestBuilder {

    public static ConfigRequest buildConfigRequest1() {
        ConfigRequest configRequest = new ConfigRequest();
        configRequest.setCode("auth.config");
        configRequest.setName("something");
        configRequest.setValueType("Строка");
        configRequest.setValue("test-value");
        configRequest.setApplicationCode("app-auth");
        return configRequest;
    }

    public static ConfigRequest buildConfigRequest2() {
        ConfigRequest configRequest = new ConfigRequest();
        configRequest.setCode("sec.url");
        configRequest.setName("name");
        configRequest.setValueType("Строка");
        configRequest.setValue("test-value");
        configRequest.setApplicationCode("app-security");
        return configRequest;
    }

    public static ConfigRequest buildConfigRequest3() {
        ConfigRequest configRequest = new ConfigRequest();
        configRequest.setCode("sec2.spring.sec-token");
        configRequest.setName("name 2");
        configRequest.setValueType("Строка");
        configRequest.setValue("test-value");
        configRequest.setApplicationCode(null);
        return configRequest;
    }

    public static ConfigRequest buildTestConfigRequest() {
        ConfigRequest configRequest = new ConfigRequest();
        configRequest.setCode("sec.test");
        configRequest.setName("test");
        configRequest.setValueType("Строка");
        configRequest.setValue("test-value");
        configRequest.setApplicationCode("app-security");
        return configRequest;
    }
}
