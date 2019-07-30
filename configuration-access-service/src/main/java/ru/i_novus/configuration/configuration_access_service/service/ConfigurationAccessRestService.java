package ru.i_novus.configuration.configuration_access_service.service;

import org.springframework.web.bind.annotation.*;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationMetadataJsonItem;

import java.util.List;

/**
 * Интерфейс REST API для работы с метаданными настроек
 */
@RequestMapping("/configurations")
public interface ConfigurationAccessRestService {

    @GetMapping("/")
    List<ConfigurationMetadataJsonItem> getAllConfigurationsMetadata();

    @GetMapping("/{configurationCode}")
    ConfigurationMetadataJsonItem getConfigurationMetadata(@PathVariable("configurationCode") String code);

    @PostMapping("/")
    void saveConfigurationMetadata(@RequestBody ConfigurationMetadataJsonItem configuration);

    @PutMapping("/{configurationCode}")
    void updateConfigurationMetadata(@PathVariable("configurationCode") String code,
                                     @RequestBody ConfigurationMetadataJsonItem configuration);

    @DeleteMapping("/{configurationCode}")
    void deleteConfigurationMetadata(@PathVariable("configurationCode") String code);
}
