package ru.i_novus.configuration.config.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.i_novus.config.api.criteria.ApplicationConfigCriteria;
import ru.i_novus.config.api.model.*;
import ru.i_novus.config.api.model.enums.EventTypeEnum;
import ru.i_novus.config.api.model.enums.ObjectTypeEnum;
import ru.i_novus.config.api.service.ApplicationConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.api.util.AuditService;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.mapper.ConfigMapper;
import ru.i_novus.configuration.config.repository.ConfigRepository;
import ru.i_novus.configuration.config.specification.ApplicationConfigSpecification;

import javax.ws.rs.NotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Реализация REST сервиса для работы с приложениями
 */
@Service
@Primary
public class ApplicationConfigRestServiceImpl implements ApplicationConfigRestService {

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private ConfigValueService configValueService;

    @Autowired
    private AuditService auditService;

    @Value("${spring.cloud.consul.config.defaultContext}")
    private String commonSystemCode;


    @Override
    public List<ConfigsApplicationResponse> getAllConfigs(ApplicationConfigCriteria criteria) {
        Map<String, String> commonSystemConfigValues = configValueService.getKeyValueList(commonSystemCode);
        criteria.noPagination();
        ApplicationConfigSpecification specification = new ApplicationConfigSpecification(criteria);
        List<ConfigEntity> groupedConfigs = configRepository.findAll(specification, criteria).getContent();

        List<ConfigsApplicationResponse> result = new ArrayList<>();

        // TODO - сделать код менее запутанным
        for (int i = 0; i < groupedConfigs.size(); ) {
            ConfigEntity data = groupedConfigs.get(i);

            ConfigsApplicationResponse application = new ConfigsApplicationResponse();
            application.setCode(data.getApplication().getCode());
            application.setName(data.getApplication().getName());
            application.setGroups(new ArrayList<>());
            result.add(application);

            Map<String, String> appConfigValues = configValueService.getKeyValueList(application.getCode());

            do {
                data = groupedConfigs.get(i);
                ConfigGroupResponse group = new ConfigGroupResponse();
                if (data.getGroup() != null) {
                    group.setId(data.getGroup().getId());
                    group.setName(data.getGroup().getName());
                } else {
                    group = new EmptyGroup();
                }
                group.setConfigs(new ArrayList<>());

                do {
                    ApplicationConfigResponse config = new ApplicationConfigResponse();
                    data = groupedConfigs.get(i);
                    config.setCode(data.getCode());
                    config.setName(data.getName());
                    config.setValueType(data.getValueType().getName());
                    config.setValue(appConfigValues.get(config.getCode()));
                    config.setCommonSystemValue(commonSystemConfigValues.get(config.getCode()));
                    config.setDefaultValue(data.getDefaultValue());
                    if (!(Boolean.TRUE.equals(criteria.getWithValue()) && isNull(config.getValue())))
                        group.getConfigs().add(config);
                    i++;
                } while (i < groupedConfigs.size() && application.getCode().equals(groupedConfigs.get(i).getApplication().getCode()) &&
                        ((groupedConfigs.get(i).getGroup().getId() != null && groupedConfigs.get(i).getGroup().getId() == group.getId()) ||
                                groupedConfigs.get(i).getGroup().getId() == null && group.getId() == 0));
                application.getGroups().add(group);
            } while (i < groupedConfigs.size() && application.getCode().equals(groupedConfigs.get(i).getApplication().getCode()));
        }

        return clearEmptyGroups(result);
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

        if (entity.getApplication() != null) {
            configValueService.saveValue(entity.getApplication().getCode(), code, value);
        }
        audit(ConfigMapper.toConfigForm(entity, value), EventTypeEnum.APPLICATION_CONFIG_UPDATE);
    }

    @Override
    @Transactional
    public void deleteConfigValue(String code) {
        ConfigEntity entity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow(NotFoundException::new);
        String oldValue = configValueService.getValue(entity.getApplication().getCode(), code);
        configValueService.deleteValue(entity.getApplication().getCode(), code);
        audit(ConfigMapper.toConfigForm(entity, oldValue), EventTypeEnum.APPLICATION_CONFIG_DELETE);
    }

    private List<ConfigsApplicationResponse> clearEmptyGroups(List<ConfigsApplicationResponse> result) {
        return result.stream()
                .filter(r -> nonNull(r.getGroups()) && r.getGroups().stream().anyMatch(g -> !CollectionUtils.isEmpty(g.getConfigs())))
                .collect(Collectors.toList());
    }

    private void audit(ConfigForm configForm, EventTypeEnum eventType) {
        auditService.audit(eventType.getTitle(), configForm, configForm.getCode(), ObjectTypeEnum.APPLICATION_CONFIG.getTitle());
    }
}
