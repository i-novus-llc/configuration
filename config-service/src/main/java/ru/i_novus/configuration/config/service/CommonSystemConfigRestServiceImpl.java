package ru.i_novus.configuration.config.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.i_novus.config.api.criteria.ApplicationConfigCriteria;
import ru.i_novus.config.api.model.*;
import ru.i_novus.config.api.model.enums.EventTypeEnum;
import ru.i_novus.config.api.model.enums.ObjectTypeEnum;
import ru.i_novus.config.api.service.CommonSystemConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.api.util.AuditService;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.mapper.ConfigMapper;
import ru.i_novus.configuration.config.repository.ConfigRepository;
import ru.i_novus.configuration.config.specification.ApplicationConfigSpecification;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class CommonSystemConfigRestServiceImpl implements CommonSystemConfigRestService {

    @Autowired
    ConfigRepository configRepository;

    @Autowired
    ConfigValueService configValueService;

    @Autowired
    private AuditService auditService;

    @Value("${spring.cloud.consul.config.defaultContext}")
    private String commonSystemCode;


    @Override
    public List<ConfigGroupResponse> getAllConfigs(ApplicationConfigCriteria criteria) {
        Map<String, String> configValues = configValueService.getKeyValueList(commonSystemCode);
        criteria.noPagination();
        ApplicationConfigSpecification specification = new ApplicationConfigSpecification(criteria);
        List<ConfigEntity> groupedConfigs = configRepository.findAll(specification, criteria).getContent();

        List<ConfigGroupResponse> result = new ArrayList<>();

        for (int i = 0; i < groupedConfigs.size(); ) {
            ConfigEntity data = groupedConfigs.get(i);
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
                config.setValue(configValues.get(config.getCode()));
                if (!(Boolean.TRUE.equals(criteria.getWithValue()) && isNull(config.getValue())))
                    group.getConfigs().add(config);
                i++;
            } while (i < groupedConfigs.size() &&
                    ((groupedConfigs.get(i).getGroup() != null && groupedConfigs.get(i).getGroup().getId() == group.getId()) ||
                            groupedConfigs.get(i).getGroup() == null && group.getId() == 0));
            result.add(group);
        }

        return clearEmptyGroups(result);
    }

    @Override
    public ApplicationConfigResponse getConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).
                orElseThrow(NotFoundException::new);

        String value = configValueService.getValue(commonSystemCode, code);
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

        configValueService.saveValue(commonSystemCode, code, value);
        audit(ConfigMapper.toConfigForm(entity, value), EventTypeEnum.COMMON_SYSTEM_CONFIG_UPDATE);
    }

    @Override
    @Transactional
    public void deleteConfigValue(String code) {
        ConfigEntity entity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow(NotFoundException::new);
        String oldValue = configValueService.getValue(commonSystemCode, code);
        configValueService.deleteValue(commonSystemCode, code);

        audit(ConfigMapper.toConfigForm(entity, oldValue), EventTypeEnum.COMMON_SYSTEM_CONFIG_DELETE);
    }

    private List<ConfigGroupResponse> clearEmptyGroups(List<ConfigGroupResponse> result) {
        return result.stream()
                .filter(g -> !CollectionUtils.isEmpty(g.getConfigs()))
                .collect(Collectors.toList());
    }

    private void audit(ConfigForm configForm, EventTypeEnum eventType) {
        auditService.audit(eventType.getTitle(), configForm, configForm.getCode(), ObjectTypeEnum.COMMON_SYSTEM_CONFIG.getTitle());
    }
}
