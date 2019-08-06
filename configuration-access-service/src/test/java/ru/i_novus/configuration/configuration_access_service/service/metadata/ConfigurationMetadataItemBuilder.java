package ru.i_novus.configuration.configuration_access_service.service.metadata;

import ru.i_novus.configuration.configuration_access_service.entity.metadata.ConfigurationMetadataResponseItem;

public class ConfigurationMetadataItemBuilder {

    public static ConfigurationMetadataResponseItem buildConfigurationMetadataItem1() {
        ConfigurationMetadataResponseItem configurationMetadataResponseItem = new ConfigurationMetadataResponseItem();
        configurationMetadataResponseItem.setCode("test.conf");
        configurationMetadataResponseItem.setName("test name");
        configurationMetadataResponseItem.setDescription("test desc");
        configurationMetadataResponseItem.setValueType("Дата");
        return configurationMetadataResponseItem;
    }

    public static ConfigurationMetadataResponseItem buildConfigurationMetadataItem2() {
        ConfigurationMetadataResponseItem configurationMetadataResponseItem = new ConfigurationMetadataResponseItem();
        configurationMetadataResponseItem.setCode("test.conf2");
        configurationMetadataResponseItem.setName("test name2");
        configurationMetadataResponseItem.setDescription("test desc2");
        configurationMetadataResponseItem.setValueType("Строковый");
        return configurationMetadataResponseItem;
    }
}
