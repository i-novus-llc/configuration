package ru.i_novus.configuration.configuration_access_service.service.group;

import ru.i_novus.configuration.configuration_access_service.items.ConfigurationGroupResponseItem;

import java.util.Arrays;

public class ConfigurationGroupItemBuilder {

    public static ConfigurationGroupResponseItem buildConfigurationGroupItem1() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = new ConfigurationGroupResponseItem();
        configurationGroupResponseItem.setName("Security settings");
        configurationGroupResponseItem.setCodes(Arrays.asList("sec", "security"));
        return configurationGroupResponseItem;
    }

    public static ConfigurationGroupResponseItem buildConfigurationGroupItem2() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = new ConfigurationGroupResponseItem();
        configurationGroupResponseItem.setName("Base security settings");
        configurationGroupResponseItem.setCodes(Arrays.asList("base-sec"));
        return configurationGroupResponseItem;
    }

    public static ConfigurationGroupResponseItem buildConfigurationGroupItem3() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = new ConfigurationGroupResponseItem();
        configurationGroupResponseItem.setName("Authentication settings");
        configurationGroupResponseItem.setCodes(Arrays.asList("auth"));
        return configurationGroupResponseItem;
    }
}
