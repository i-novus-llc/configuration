package ru.i_novus.configuration.configuration_access_service.service;

import net.n2oapp.platform.jaxrs.RestPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationGroupEntity;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationMetadataEntity;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationMetadataItem;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationSystemEntity;
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
    public Page<ConfigurationMetadataItem> getAllConfigurationsMetadata() {
        List<ConfigurationMetadataItem> configurationMetadataItems = configurationMetadataRepository.findAll().stream()
                .map(ConfigurationMetadataItem::new).collect(Collectors.toList());
        return new RestPage<>(configurationMetadataItems);
    }

    @Override
    public ConfigurationMetadataItem getConfigurationMetadata(String code) {
        ConfigurationMetadataEntity configurationMetadataEntity = Optional.ofNullable(configurationMetadataRepository.findByCode(code))
                .orElseThrow(() -> new NotFoundException("Настройки с кодом " + code + " не существует."));
        return new ConfigurationMetadataItem(configurationMetadataEntity);
    }

    @Override
    public void saveConfigurationMetadata(@Valid @NotNull ConfigurationMetadataItem configurationMetadataItem) {
        ConfigurationGroupEntity configurationGroupEntity = configurationGroupRepository.findByCode(configurationMetadataItem.getGroupCode());
        ConfigurationSystemEntity configurationSystemEntity = configurationSystemRepository.findByCode(configurationMetadataItem.getSystemCode());

        ConfigurationMetadataEntity configurationMetadataEntity = new ConfigurationMetadataEntity();
        configurationMetadataEntity.setAttributes(configurationMetadataItem, configurationGroupEntity, configurationSystemEntity);
        try {
            configurationMetadataRepository.save(configurationMetadataEntity);
        } catch (Exception e) {
            throw new BadRequestException("Метаданные некорректны или уже сохранены", e);
        }
    }


    ///TODO - Status 500 при name = null
    @Override
    public void updateConfigurationMetadata(String code, @Valid @NotNull ConfigurationMetadataItem configurationMetadataItem) {
        if (!code.equals(configurationMetadataItem.getCode())) {
            throw new BadRequestException("Код настройки в пути и теле json различны");
        }

        ConfigurationMetadataEntity configurationMetadataEntity = Optional.ofNullable(configurationMetadataRepository.findByCode(code))
                .orElseThrow(() -> new NotFoundException("Настройки с кодом " + code + " не существует."));
        ConfigurationGroupEntity configurationGroupEntity = configurationGroupRepository.findByCode(configurationMetadataItem.getGroupCode());
        ConfigurationSystemEntity configurationSystemEntity = configurationSystemRepository.findByCode(configurationMetadataItem.getSystemCode());

        configurationMetadataEntity.setAttributes(configurationMetadataItem, configurationGroupEntity, configurationSystemEntity);
        configurationMetadataRepository.save(configurationMetadataEntity);
    }

    @Override
    public void deleteConfigurationMetadata(String code) {
        if (configurationMetadataRepository.deleteByCode(code) == 0) {
            throw new NotFoundException("Настройки с кодом " + code + " не существует.");
        }
    }
}
