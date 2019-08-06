package ru.i_novus.configuration.configuration_access_service.service.metadata;

import net.n2oapp.platform.jaxrs.RestPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.i_novus.configuration.configuration_access_service.entity.metadata.ConfigurationMetadataEntity;
import ru.i_novus.configuration.configuration_access_service.entity.metadata.ConfigurationMetadataResponseItem;
import ru.i_novus.configuration.configuration_access_service.entity.system.ConfigurationSystemEntity;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationGroupRepository;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationMetadataRepository;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationSystemRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация REST сервиса для работы с метаданными настроек
 */
@Controller
public class ConfigurationAccessRestServiceImpl implements ConfigurationAccessRestService {

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


    @Override
    public Page<ConfigurationMetadataResponseItem> getAllConfigurationsMetadata() {
        List<ConfigurationMetadataResponseItem> configurationMetadataResponseItems = configurationMetadataRepository.findAll().stream()
                .map(ConfigurationMetadataResponseItem::new).collect(Collectors.toList());
        return new RestPage<>(configurationMetadataResponseItems);
    }

    @Override
    public ConfigurationMetadataResponseItem getConfigurationMetadata(String code) {
        ConfigurationMetadataEntity configurationMetadataEntity = Optional.ofNullable(configurationMetadataRepository.findByCode(code))
                .orElseThrow(() -> new NotFoundException("Настройки с кодом " + code + " не существует."));
        return new ConfigurationMetadataResponseItem(configurationMetadataEntity);
    }

    @Override
    public void saveConfigurationMetadata(@Valid @NotNull ConfigurationMetadataResponseItem configurationMetadataResponseItem) {
        ConfigurationSystemEntity configurationSystemEntity = configurationSystemRepository.findByCode(configurationMetadataResponseItem.getSystemCode());

        ConfigurationMetadataEntity configurationMetadataEntity = new ConfigurationMetadataEntity();
        configurationMetadataEntity.setAttributes(configurationMetadataResponseItem, configurationSystemEntity);
        try {
            configurationMetadataRepository.save(configurationMetadataEntity);
        } catch (Exception e) {
            throw new BadRequestException("Метаданные настройки с кодом " + configurationMetadataResponseItem.getCode() + " уже созданы", e);
        }
    }

    @Override
    public void updateConfigurationMetadata(String code, @Valid @NotNull ConfigurationMetadataResponseItem configurationMetadataResponseItem) {
        if (!code.equals(configurationMetadataResponseItem.getCode())) {
            throw new BadRequestException("Код настройки в пути и теле json различны");
        }

        ConfigurationMetadataEntity configurationMetadataEntity = Optional.ofNullable(configurationMetadataRepository.findByCode(code))
                .orElseThrow(() -> new NotFoundException("Настройки с кодом " + code + " не существует."));
        ConfigurationSystemEntity configurationSystemEntity = configurationSystemRepository.findByCode(configurationMetadataResponseItem.getSystemCode());

        configurationMetadataEntity.setAttributes(configurationMetadataResponseItem, configurationSystemEntity);
        configurationMetadataRepository.save(configurationMetadataEntity);
    }

    @Override
    public void deleteConfigurationMetadata(String code) {
        if (configurationMetadataRepository.removeByCode(code) == 0) {
            throw new NotFoundException("Настройки с кодом " + code + " не существует.");
        }
    }
}
