package ru.i_novus.configuration.configuration_access_service.service.group;

import ru.i_novus.configuration.configuration_access_service.entity.group.ConfigurationGroupResponseItem;

import java.util.Arrays;

public class ConfigurationGroupItemBuilder {

    public static ConfigurationGroupResponseItem buildConfigurationGroupItem1() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = new ConfigurationGroupResponseItem();
        configurationGroupResponseItem.setName("group name");
        configurationGroupResponseItem.setDescription("group desc");
        configurationGroupResponseItem.setCodes(Arrays.asList("a", "b", "c"));
        return configurationGroupResponseItem;
    }

    public static ConfigurationGroupResponseItem buildConfigurationGroupItem2() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = new ConfigurationGroupResponseItem();
        configurationGroupResponseItem.setName("group name 2");
        configurationGroupResponseItem.setDescription("group desc 2");
        configurationGroupResponseItem.setCodes(Arrays.asList("x", "y", "z"));
        return configurationGroupResponseItem;
    }

    public static ConfigurationGroupResponseItem buildConfigurationGroupItem3() {
        ConfigurationGroupResponseItem configurationGroupResponseItem = new ConfigurationGroupResponseItem();
        configurationGroupResponseItem.setName("some name");
        configurationGroupResponseItem.setDescription("some desc");
        configurationGroupResponseItem.setCodes(Arrays.asList("abc.xyz", "efg", "hij"));
        return configurationGroupResponseItem;
    }
}
