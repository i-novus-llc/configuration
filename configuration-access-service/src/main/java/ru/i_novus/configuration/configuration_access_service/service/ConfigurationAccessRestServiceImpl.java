package ru.i_novus.configuration.configuration_access_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationGroupEntity;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationMetadataEntity;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationMetadataJsonItem;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationSystemEntity;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationGroupRepository;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationMetadataRepository;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationSystemRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация REST сервиса для работы с настройками
 */
@RestController
@RequestMapping("/configurations")
public class ConfigurationAccessRestServiceImpl {

    private ConfigurationMetadataRepository configurationMetadataRepository;
    private ConfigurationGroupRepository configurationGroupRepository;
    private ConfigurationSystemRepository configurationSystemRepository;

    @Autowired
    public void setConfigurationMetadataRepository(ConfigurationMetadataRepository configurationMetadataRepository) {
        this.configurationMetadataRepository = configurationMetadataRepository;
    }

    @Autowired
    public void setConfigurationGroupRepository(ConfigurationGroupRepository configurationGroupRepository) {
        this.configurationGroupRepository = configurationGroupRepository;
    }

    @Autowired
    public void setConfigurationSystemRepository(ConfigurationSystemRepository configurationSystemRepository) {
        this.configurationSystemRepository = configurationSystemRepository;
    }

    /// TODO: обработка ошибок
    @GetMapping("/")
    public List<ConfigurationMetadataJsonItem> getAllConfigurationsMetadata() {
        List<ConfigurationMetadataEntity> configurationMetadataEntities = configurationMetadataRepository.findAll();
        return configurationMetadataEntities.stream()
                .map(ConfigurationMetadataJsonItem::configurationEntityToConfigurationItem)
                .collect(Collectors.toList());
    }

    /// TODO: обработка ошибок
    @GetMapping("/{configurationCode}")
    public ConfigurationMetadataJsonItem getConfigurationMetadata(@PathVariable("configurationCode") String code) {
        ConfigurationMetadataEntity configurationMetadataEntity = configurationMetadataRepository.findByCode(code);
        if (configurationMetadataEntity != null) {
            return ConfigurationMetadataJsonItem.configurationEntityToConfigurationItem(configurationMetadataEntity);
        }
        return null;
    }

    /// TODO: обработка ошибок
    @PostMapping("/")
    public void saveConfigurationMetadata(@RequestBody ConfigurationMetadataJsonItem configurationMetadataJsonItem) {
        ConfigurationGroupEntity configurationGroupEntity = configurationGroupRepository.findByCode(configurationMetadataJsonItem.getGroupCode());
        ConfigurationSystemEntity configurationSystemEntity = configurationSystemRepository.findByCode(configurationMetadataJsonItem.getSystemCode());

        if (configurationGroupEntity != null && configurationSystemEntity!= null) {
            ConfigurationMetadataEntity configurationMetadataEntity = ConfigurationMetadataJsonItem.configurationItemToConfigurationEntity(
                    configurationMetadataJsonItem, configurationGroupEntity, configurationSystemEntity
            );
            configurationMetadataRepository.save(configurationMetadataEntity);
        }
    }

    /// TODO: обработка ошибок
    @PutMapping("/")
    public void updateConfigurationMetadata(@RequestBody ConfigurationMetadataJsonItem configurationMetadataJsonItem) {
        ConfigurationMetadataEntity configurationMetadataEntity = configurationMetadataRepository.findByCode(configurationMetadataJsonItem.getCode());
        if (configurationMetadataEntity != null) {
            ConfigurationGroupEntity configurationGroupEntity = configurationGroupRepository.findByCode(configurationMetadataJsonItem.getGroupCode());
            ConfigurationSystemEntity configurationSystemEntity = configurationSystemRepository.findByCode(configurationMetadataJsonItem.getSystemCode());

            if (configurationGroupEntity != null && configurationSystemEntity != null) {
                Integer id = configurationMetadataEntity.getId();
                configurationMetadataEntity = ConfigurationMetadataJsonItem.configurationItemToConfigurationEntity(
                        configurationMetadataJsonItem, configurationGroupEntity, configurationSystemEntity
                );
                configurationMetadataEntity.setId(id);

                configurationMetadataRepository.save(configurationMetadataEntity);
            }
        }
    }

    /// TODO: обработка ошибок
    @DeleteMapping("/{configurationCode}")
    public void deleteConfigurationMetadata(@PathVariable("configurationCode") String code) {
        configurationMetadataRepository.deleteByCode(code);
    }
}
