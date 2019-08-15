package ru.i_novus.configuration_service.service.builders;

import ru.i_novus.configuration_api.items.ConfigurationResponseItem;
import ru.i_novus.configuration_api.items.GroupResponseItem;

import java.util.Arrays;

public class ConfigurationItemBuilder {

    public static ConfigurationResponseItem buildConfigurationItem1() {
        ConfigurationResponseItem configurationResponseItem = new ConfigurationResponseItem();
        configurationResponseItem.setName("test name");
        configurationResponseItem.setCode("sec.url");
        configurationResponseItem.setValueType("Строка");
        configurationResponseItem.setValue("test-value");
        configurationResponseItem.setGroupName("Security settings");
        configurationResponseItem.setSystemName("application");
        return configurationResponseItem;
    }

    public static ConfigurationResponseItem buildConfigurationItem2() {
        ConfigurationResponseItem configurationResponseItem = new ConfigurationResponseItem();
        configurationResponseItem.setName("test name 2");
        configurationResponseItem.setCode("sec2.spring.sec-token");
        configurationResponseItem.setValueType("Строка");
        configurationResponseItem.setValue("test-value");
        configurationResponseItem.setGroupName("Security settings");
        configurationResponseItem.setSystemName("application");
        return configurationResponseItem;
    }

    public static ConfigurationResponseItem buildConfigurationItem3() {
        ConfigurationResponseItem configurationResponseItem = new ConfigurationResponseItem();
        configurationResponseItem.setName("something");
        configurationResponseItem.setCode("auth.config");
        configurationResponseItem.setValueType("Строка");
        configurationResponseItem.setValue("test-value");
        configurationResponseItem.setGroupName("Authentication settings");
        configurationResponseItem.setSystemName("application");
        return configurationResponseItem;
    }

    public static GroupResponseItem buildGroupItem1() {
        GroupResponseItem groupResponseItem = new GroupResponseItem();
        groupResponseItem.setName("Security settings");
        groupResponseItem.setCodes(Arrays.asList("sec", "sec2"));
        return groupResponseItem;
    }

    public static GroupResponseItem buildGroupItem2() {
        GroupResponseItem groupResponseItem = new GroupResponseItem();
        groupResponseItem.setName("Authentication settings");
        groupResponseItem.setCodes(Arrays.asList("auth"));
        return groupResponseItem;
    }
}
