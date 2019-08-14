package ru.i_novus.configuration_service.service.builders;

import ru.i_novus.configuration_api.items.ConfigurationResponseItem;
import ru.i_novus.configuration_api.items.GroupResponseItem;

import java.util.Arrays;

public class ConfigurationItemBuilder {

    public static ConfigurationResponseItem buildConfigurationItem1() {
        ConfigurationResponseItem configurationResponseItem = new ConfigurationResponseItem();
        configurationResponseItem.setName("test name");
        configurationResponseItem.setCode("test.sec.url");
        configurationResponseItem.setValueType("Строка");
        configurationResponseItem.setValue("test-value");
        configurationResponseItem.setGroupName("Test settings");
        configurationResponseItem.setSystemName("application");
        return configurationResponseItem;
    }

    public static ConfigurationResponseItem buildConfigurationItem2() {
        ConfigurationResponseItem configurationResponseItem = new ConfigurationResponseItem();
        configurationResponseItem.setName("test name 2");
        configurationResponseItem.setCode("test.spring.sec-token");
        configurationResponseItem.setValueType("Строка");
        configurationResponseItem.setValue("test-value");
        configurationResponseItem.setGroupName("Test settings");
        configurationResponseItem.setSystemName("application");
        return configurationResponseItem;
    }

    public static ConfigurationResponseItem buildConfigurationItem3() {
        ConfigurationResponseItem configurationResponseItem = new ConfigurationResponseItem();
        configurationResponseItem.setName("something");
        configurationResponseItem.setCode("test.auth.config");
        configurationResponseItem.setValueType("Строка");
        configurationResponseItem.setValue("test-value");
        configurationResponseItem.setGroupName("Test settings");
        configurationResponseItem.setSystemName("application");
        return configurationResponseItem;
    }

    public static GroupResponseItem buildGroupItem() {
        GroupResponseItem groupResponseItem = new GroupResponseItem();
        groupResponseItem.setName("Test settings");
        groupResponseItem.setCodes(Arrays.asList("test"));
        return groupResponseItem;
    }
}
