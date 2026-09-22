package ru.i_novus.configuration.config.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.i_novus.config.api.criteria.ApplicationConfigCriteria;
import ru.i_novus.config.api.model.ApplicationConfigResponse;
import ru.i_novus.config.api.model.ConfigGroupResponse;
import ru.i_novus.config.api.model.ConfigValue;
import ru.i_novus.config.api.model.ConfigsApplicationResponse;
import ru.i_novus.config.api.model.EmptyGroup;
import ru.i_novus.config.api.model.enums.EventTypeEnum;
import ru.i_novus.config.api.model.enums.ObjectTypeEnum;
import ru.i_novus.config.api.service.ApplicationConfigRestService;
import ru.i_novus.config.api.service.ConfigValidationService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.repository.ConfigRepository;
import ru.i_novus.configuration.config.specification.ApplicationConfigSpecification;
import ru.i_novus.configuration.config.utils.LogUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Реализация REST сервиса для работы с приложениями
 */
@Service
@Primary
@RequiredArgsConstructor
public class ApplicationConfigRestServiceImpl implements ApplicationConfigRestService {

    private final ConfigRepository configRepository;
    private final ConfigValueService configValueService;
    private final ConfigValidationService configValidationService;

    @Value("${spring.cloud.consul.config.defaultContext:application}")
    private String commonSystemCode;

    @Override
    public List<ConfigsApplicationResponse> getAllConfigs(ApplicationConfigCriteria criteria) {
        Map<String, String> commonSystemConfigValues = configValueService.getKeyValueList(commonSystemCode);
        criteria.noPagination();
        ApplicationConfigSpecification specification = new ApplicationConfigSpecification(criteria);
        List<ConfigEntity> groupedConfigs = configRepository.findAll(specification, criteria).getContent();

        List<ConfigsApplicationResponse> result = groupByPreservingOrder(groupedConfigs, this::applicationCode)
                .values().stream()
                .map(configs -> toApplicationResponse(configs, criteria, commonSystemConfigValues))
                .toList();

        return clearEmptyGroups(result);
    }

    private String applicationCode(ConfigEntity config) {
        return config.getApplication() == null ? null : config.getApplication().getCode();
    }

    private Integer groupId(ConfigEntity config) {
        return config.getGroup() == null ? null : config.getGroup().getId();
    }

    private ConfigsApplicationResponse toApplicationResponse(List<ConfigEntity> configs, ApplicationConfigCriteria criteria,
                                                               Map<String, String> commonSystemConfigValues) {
        ConfigEntity first = configs.get(0);

        ConfigsApplicationResponse application = new ConfigsApplicationResponse();
        if (first.getApplication() != null) {
            application.setCode(first.getApplication().getCode());
            application.setName(first.getApplication().getName());
        }

        Map<String, String> appConfigValues = configValueService.getKeyValueList(application.getCode());

        List<ConfigGroupResponse> groups = groupByPreservingOrder(configs, this::groupId).values().stream()
                .map(groupConfigs -> toGroupResponse(groupConfigs, criteria, appConfigValues, commonSystemConfigValues))
                .toList();
        application.setGroups(groups);

        return application;
    }

    private ConfigGroupResponse toGroupResponse(List<ConfigEntity> configs, ApplicationConfigCriteria criteria,
                                                 Map<String, String> appConfigValues, Map<String, String> commonSystemConfigValues) {
        ConfigEntity first = configs.getFirst();

        ConfigGroupResponse group;
        if (first.getGroup() != null) {
            group = new ConfigGroupResponse();
            group.setId(first.getGroup().getId());
            group.setName(first.getGroup().getName());
        } else {
            group = new EmptyGroup();
        }

        List<ApplicationConfigResponse> configResponses = configs.stream()
                .map(data -> toConfigResponse(data, appConfigValues, commonSystemConfigValues))
                .filter(config -> !(Boolean.TRUE.equals(criteria.getWithValue()) && isNull(config.getValue())))
                .collect(Collectors.toList());
        group.setConfigs(configResponses);

        return group;
    }

    private ApplicationConfigResponse toConfigResponse(ConfigEntity data, Map<String, String> appConfigValues,
                                                         Map<String, String> commonSystemConfigValues) {
        ApplicationConfigResponse config = new ApplicationConfigResponse();
        config.setCode(data.getCode());
        config.setName(data.getName());
        config.setValueType(data.getValueType().getName());
        config.setValue(appConfigValues.get(config.getCode()));
        config.setCommonSystemValue(commonSystemConfigValues.get(config.getCode()));
        config.setDefaultValue(data.getDefaultValue());
        return config;
    }

    /**
     * Группирует настройки по ключу с сохранением порядка их следования в списке.
     */
    private <K> Map<K, List<ConfigEntity>> groupByPreservingOrder(List<ConfigEntity> configs, Function<ConfigEntity, K> keyFn) {
        Map<K, List<ConfigEntity>> grouped = new LinkedHashMap<>();
        for (ConfigEntity config : configs) {
            grouped.computeIfAbsent(keyFn.apply(config), k -> new ArrayList<>()).add(config);
        }
        return grouped;
    }

    @Override
    public ApplicationConfigResponse getConfig(String code) {
        // TODO - добавить дополнительные проверки на код приложения
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).
                orElseThrow(NotFoundException::new);

        String value = null;
        if (configEntity.getApplication() != null) {
            value = configValueService.getValue(configEntity.getApplication().getCode(), code);
        }
        ApplicationConfigResponse configResponse = new ApplicationConfigResponse();
        configResponse.setCode(configEntity.getCode());
        configResponse.setName(configEntity.getName());
        configResponse.setValue(value);

        return configResponse;
    }

    @Override
    @Transactional
    public void saveConfigValue(String code, ConfigValue configValue) {
        ConfigEntity entity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow(NotFoundException::new);
        String value = configValue.getValue();
        configValidationService.validateConfigValue(value, entity.getValueType());

        if (entity.getApplication() != null) {
            configValueService.saveValue(entity.getApplication().getCode(), code, value);
        }
        LogUtils.log(EventTypeEnum.APPLICATION_CONFIG_UPDATE.getTitle(), code, ObjectTypeEnum.APPLICATION_CONFIG.getTitle());
    }

    @Override
    @Transactional
    public void deleteConfigValue(String code) {
        ConfigEntity entity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow(NotFoundException::new);
        configValueService.deleteValue(entity.getApplication().getCode(), code);
        LogUtils.log(EventTypeEnum.APPLICATION_CONFIG_DELETE.getTitle(), code, ObjectTypeEnum.APPLICATION_CONFIG.getTitle());
    }

    private List<ConfigsApplicationResponse> clearEmptyGroups(List<ConfigsApplicationResponse> result) {
        return result.stream()
                .filter(r -> nonNull(r.getGroups()) && r.getGroups().stream().anyMatch(g -> !CollectionUtils.isEmpty(g.getConfigs())))
                .collect(Collectors.toList());
    }
}
