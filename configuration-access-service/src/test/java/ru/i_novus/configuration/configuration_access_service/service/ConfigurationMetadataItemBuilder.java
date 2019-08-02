package ru.i_novus.configuration.configuration_access_service.service;

import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationMetadataItem;

public class ConfigurationMetadataItemBuilder {

    public static ConfigurationMetadataItem buildConfigurationMetadataItem1() {
        ConfigurationMetadataItem configurationMetadataItem = new ConfigurationMetadataItem();
        configurationMetadataItem.setCode("test.conf");
        configurationMetadataItem.setName("test name");
        configurationMetadataItem.setDescription("test desc");
        configurationMetadataItem.setValueType("Дата");
        return configurationMetadataItem;
    }

    public static ConfigurationMetadataItem buildConfigurationMetadataItem2() {
        ConfigurationMetadataItem configurationMetadataItem = new ConfigurationMetadataItem();
        configurationMetadataItem.setCode("test.conf2");
        configurationMetadataItem.setName("test name2");
        configurationMetadataItem.setDescription("test desc2");
        configurationMetadataItem.setValueType("Строковый");
        return configurationMetadataItem;
    }
}
