package ru.i_novus.configuration.configuration_access_service.service.metadata;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import ru.i_novus.configuration.configuration_access_service.criteria.FindConfigurationCriteria;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationMetadataEntity;
import ru.i_novus.configuration.configuration_access_service.entity.QConfigurationMetadataEntity;
import ru.i_novus.configuration.configuration_access_service.items.ConfigurationResponseItem;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationGroupCodeRepository;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationGroupRepository;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationMetadataRepository;
import ru.i_novus.configuration.configuration_access_service.service.value_receiving.ConfigurationValueService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.*;

/**
 * Реализация REST сервиса для работы с настройками
 */
@Controller
public class ConfigurationAccessRestServiceImpl implements ConfigurationAccessRestService {

    private ConfigurationValueService configurationValueService;

    private ConfigurationMetadataRepository configurationMetadataRepository;
    private ConfigurationGroupRepository configurationGroupRepository;
    private ConfigurationGroupCodeRepository configurationGroupCodeRepository;

    @Autowired
    public void setConfigurationValueService(ConfigurationValueService configurationValueService) {
        this.configurationValueService = configurationValueService;
    }

    @Autowired
    public void setConfigurationMetadataRepository(ConfigurationMetadataRepository configurationMetadataRepository) {
        this.configurationMetadataRepository = configurationMetadataRepository;
    }

    @Autowired
    public void setConfigurationGroupRepository(ConfigurationGroupRepository configurationGroupRepository) {
        this.configurationGroupRepository = configurationGroupRepository;
    }

    @Autowired
    public void setConfigurationGroupCodeRepository(ConfigurationGroupCodeRepository configurationGroupCodeRepository) {
        this.configurationGroupCodeRepository = configurationGroupCodeRepository;
    }


    @Override
    public Page<ConfigurationResponseItem> getAllConfigurations(FindConfigurationCriteria criteria) {
        Predicate predicate = buildFindConfigurationPredicate(criteria);
        Iterable<ConfigurationMetadataEntity> configurationMetadataEntities = configurationMetadataRepository.findAll(predicate);

        List<ConfigurationResponseItem> configurationResponseItems = new ArrayList<>();
        for (ConfigurationMetadataEntity entity : configurationMetadataEntities) {
            String value = configurationValueService.getConfigurationValue(getServiceCode(entity), entity.getCode());
            /// TODO - вытащить system_name по service_code из виртуальной таблицы
            String systemName = "application";
            String groupName = configurationGroupRepository.findGroupsNameByConfigurationCode(entity.getCode(), new PageRequest(0, 1)).get(0);
            configurationResponseItems.add(new ConfigurationResponseItem(entity, value, systemName, groupName));
        }

        Collections.sort(configurationResponseItems, Comparator.comparing(ConfigurationResponseItem::getSystemName));
        return new PageImpl<>(configurationResponseItems, criteria, criteria.getPageSize());
    }

    @Override
    public ConfigurationResponseItem getConfiguration(String code) {
        ConfigurationMetadataEntity configurationMetadataEntity = Optional.ofNullable(configurationMetadataRepository.findByCode(code))
                .orElseThrow(() -> new NotFoundException("Настройки с кодом " + code + " не существует."));

        String value = configurationValueService.getConfigurationValue(getServiceCode(configurationMetadataEntity), configurationMetadataEntity.getCode());
        /// TODO - вытащить system_name по service_code из виртуальной таблицы
        String systemName = "application";
        String groupName = configurationGroupRepository.findGroupsNameByConfigurationCode(configurationMetadataEntity.getCode(), new PageRequest(0, 1)).get(0);


        return new ConfigurationResponseItem(configurationMetadataEntity, value, systemName, groupName);
    }


    @Override
    public void saveConfiguration(@Valid @NotNull ConfigurationResponseItem configurationResponseItem) {
        if (configurationResponseItem.getServiceCode() != null)
        {
            /// TODO - проверяем есть ли такой serviceCode в виртуальной таблице
        }

        ConfigurationMetadataEntity configurationMetadataEntity = new ConfigurationMetadataEntity(configurationResponseItem);
        try {
            configurationMetadataRepository.save(configurationMetadataEntity);
        } catch (Exception e) {
            throw new BadRequestException("Настройка с кодом " + configurationResponseItem.getCode() + " уже существует", e);
        }

        configurationValueService.saveConfigurationValue(getServiceCode(configurationMetadataEntity), configurationResponseItem.getCode(), configurationResponseItem.getValue());
    }

    @Override
    public void updateConfiguration(String code, @Valid @NotNull ConfigurationResponseItem configurationResponseItem) {
        ConfigurationMetadataEntity configurationMetadataEntity = Optional.ofNullable(
                configurationMetadataRepository.findByCode(code))
                .orElseThrow(() -> new NotFoundException("Настройки с кодом " + code + " не существует"));

        configurationMetadataEntity.setServiceCode(configurationResponseItem.getServiceCode());
        configurationMetadataEntity.setDescription(configurationResponseItem.getDescription());
        configurationMetadataRepository.save(configurationMetadataEntity);

        configurationValueService.saveConfigurationValue(getServiceCode(configurationMetadataEntity),
                configurationResponseItem.getCode(),
                configurationResponseItem.getValue());
    }

    @Override
    public void deleteConfiguration(String code) {
        ConfigurationMetadataEntity configurationMetadataEntity = Optional.ofNullable(configurationMetadataRepository.findByCode(code))
                .orElseThrow(() -> new NotFoundException("Настройки с кодом " + code + " не существует."));
        configurationMetadataRepository.removeByCode(code);

        configurationValueService.deleteConfigurationValue(getServiceCode(configurationMetadataEntity), code);
    }

    private Predicate buildFindConfigurationPredicate(FindConfigurationCriteria criteria) {
        QConfigurationMetadataEntity qConfigurationMetadataEntity = QConfigurationMetadataEntity.configurationMetadataEntity;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (criteria.getCode() != null) {
            booleanBuilder.and(qConfigurationMetadataEntity.code.containsIgnoreCase(criteria.getCode()));
        }

        if (criteria.getName() != null) {
            booleanBuilder.and(qConfigurationMetadataEntity.name.containsIgnoreCase(criteria.getName()));
        }

        List<String> groups = criteria.getGroupNames();
        if (groups != null && !groups.isEmpty()) {
            /// TODO
        }

        List<String> systems = criteria.getSystemNames();
        if (systems != null && !systems.isEmpty()) {
            /// TODO
        }

        return booleanBuilder.getValue();
    }

    private String getServiceCode(ConfigurationMetadataEntity configurationMetadataEntity) {
        return Objects.requireNonNullElse(configurationMetadataEntity.getServiceCode(), "application");
    }
}
