package ru.i_novus.configuration.config.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.criteria.ApplicationConfigCriteria;
import ru.i_novus.config.api.model.ApplicationConfigResponse;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.ConfigGroupResponse;
import ru.i_novus.config.api.model.ConfigValue;
import ru.i_novus.config.api.model.enums.EventTypeEnum;
import ru.i_novus.config.api.model.enums.ObjectTypeEnum;
import ru.i_novus.config.api.service.CommonSystemConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.api.util.AuditService;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.mapper.ConfigMapper;
import ru.i_novus.configuration.config.repository.ConfigRepository;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        List<Object[]> groupedConfigs = configRepository.findGroupedCommonSystemConfigs();

        // TODO list -> page
        List<ConfigGroupResponse> result = new ArrayList<>();

        for (int i = 0; i < groupedConfigs.size(); ) {
            Object[] data = groupedConfigs.get(i);
            ConfigGroupResponse group = new ConfigGroupResponse();
            if (data[0] != null) {
                group.setId((int) data[0]);
                group.setName((String) data[1]);
            } else {
                group.setId(0);
            }
            group.setConfigs(new ArrayList<>());

            do {
                ApplicationConfigResponse config = new ApplicationConfigResponse();
                data = groupedConfigs.get(i);
                config.setCode((String) data[2]);
                config.setName((String) data[3]);
                config.setValue(configValues.get(config.getCode()));
                group.getConfigs().add(config);
                i++;
            } while (i < groupedConfigs.size() &&
                    ((groupedConfigs.get(i)[0] != null && (int) groupedConfigs.get(i)[0] == group.getId()) ||
                            groupedConfigs.get(i)[0] == null && group.getId() == 0));
            result.add(group);
        }

        return result;
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

        if (value != null) {
            configValueService.saveValue(commonSystemCode, code, value);
            audit(ConfigMapper.toConfigForm(entity, value), EventTypeEnum.COMMON_SYSTEM_CONFIG_UPDATE);
        } else {
            String oldValue = configValueService.getValue(commonSystemCode, code);
            configValueService.deleteValue(commonSystemCode, code);
            audit(ConfigMapper.toConfigForm(entity, oldValue), EventTypeEnum.COMMON_SYSTEM_CONFIG_DELETE);
        }
    }

    private void audit(ConfigForm configForm, EventTypeEnum eventType) {
        auditService.audit(eventType.getTitle(), configForm, configForm.getCode(), ObjectTypeEnum.COMMON_SYSTEM_CONFIG.getTitle());
    }
}
