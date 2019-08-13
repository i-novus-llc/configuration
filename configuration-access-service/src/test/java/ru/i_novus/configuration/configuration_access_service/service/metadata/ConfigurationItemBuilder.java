package ru.i_novus.configuration.configuration_access_service.service.metadata;

import ru.i_novus.configuration.configuration_access_service.items.ConfigurationGroupResponseItem;
import ru.i_novus.configuration.configuration_access_service.items.ConfigurationResponseItem;

import java.util.Arrays;

public class ConfigurationItemBuilder {

    public static ConfigurationResponseItem buildConfigurationItem1() {
        ConfigurationResponseItem configurationResponseItem = new ConfigurationResponseItem();
        configurationResponseItem.setName("test name");
        configurationResponseItem.setCode("test.sec.url");
        configurationResponseItem.setValueType("Строка");
        configurationResponseItem.setValue("some-url");
        configurationResponseItem.setGroupName("Test settings");
        configurationResponseItem.setSystemName("application");
        return configurationResponseItem;
    }

    public static ConfigurationResponseItem buildConfigurationItem2() {
        ConfigurationResponseItem configurationResponseItem = new ConfigurationResponseItem();
        configurationResponseItem.setName("test name 2");
        configurationResponseItem.setCode("test.spring.sec-token");
        configurationResponseItem.setValueType("Строка");
        configurationResponseItem.setValue("some-token");
        configurationResponseItem.setGroupName("Test settings");
        configurationResponseItem.setSystemName("application");
        return configurationResponseItem;
    }

    public static ConfigurationResponseItem buildConfigurationItem3() {
        ConfigurationResponseItem configurationResponseItem = new ConfigurationResponseItem();
        configurationResponseItem.setName("something");
        configurationResponseItem.setCode("test.auth.config");
        configurationResponseItem.setValueType("Строка");
        configurationResponseItem.setValue("something");
        configurationResponseItem.setGroupName("Test settings");
        configurationResponseItem.setSystemName("application");
        return configurationResponseItem;
    }

    public static ConfigurationGroupResponseItem buildConfigurationGroupItem() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = new ConfigurationGroupResponseItem();
        configurationGroupResponseItem.setName("Test settings");
        configurationGroupResponseItem.setCodes(Arrays.asList("test"));
        return configurationGroupResponseItem;
    }
}
