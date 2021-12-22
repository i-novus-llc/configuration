package ru.i_novus.configuration.config.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.criteria.ApplicationConfigCriteria;
import ru.i_novus.config.api.model.ApplicationConfigResponse;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.ConfigsApplicationResponse;
import ru.i_novus.config.api.model.enums.EventTypeEnum;
import ru.i_novus.config.api.model.enums.ObjectTypeEnum;
import ru.i_novus.config.api.service.ApplicationConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.api.util.AuditService;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.mapper.ConfigMapper;
import ru.i_novus.configuration.config.repository.ApplicationRepository;
import ru.i_novus.configuration.config.repository.ConfigRepository;

import javax.ws.rs.NotFoundException;
import java.util.*;

/**
 * Реализация REST сервиса для работы с приложениями
 */
@Service
@Primary
public class ApplicationConfigRestServiceImpl implements ApplicationConfigRestService {

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private ConfigValueService configValueService;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private AuditService auditService;

    @Value("${spring.cloud.consul.config.defaultContext}")
    private String defaultAppCode;

    @Value("${config.common.system.code}")
    private String commonSystemCode;


    @Override
    public Page<ConfigsApplicationResponse> getAllConfigs(ApplicationConfigCriteria criteria) {
        return null;
    }

    @Override
    public ApplicationConfigResponse getConfig(String code) {
        return null;
    }

    @Override
    @Transactional
    public void saveApplicationConfig(String code, Map<String, Object> data) {
        if (!code.equals(commonSystemCode))
            Optional.ofNullable(applicationRepository.findByCode(code)).orElseThrow(NotFoundException::new);
        if (data.get("data") == null) {
            return;
        }

        Map<String, String> updatedKeyValues = new HashMap<>();
        Map<String, String> deletedKeyValues = new HashMap<>();

        Map<String, String> commonApplicationConfigKeyValues = Collections.EMPTY_MAP;
        try {
            commonApplicationConfigKeyValues = configValueService.getKeyValueList(defaultAppCode);
        } catch (Exception ignored) {
        }
        Map<String, String> applicationConfigKeyValues = Collections.EMPTY_MAP;
        if (!code.equals(commonSystemCode)) {
            try {
                applicationConfigKeyValues = configValueService.getKeyValueList(code);
                deletedKeyValues = applicationConfigKeyValues;
            } catch (Exception ignored) {
            }
        } else {
            code = defaultAppCode;
            deletedKeyValues = commonApplicationConfigKeyValues;
        }

        for (Map.Entry entry : ((Map<String, Object>) data.get("data")).entrySet()) {
            String key = ((String) entry.getKey()).replace("__", ".");
            String value = String.valueOf(entry.getValue());

            if (entry.getValue() == null || value.equals("")) continue;

            String applicationConfigValue = applicationConfigKeyValues.get(key);
            String commonApplicationValue = commonApplicationConfigKeyValues.get(key);
            ConfigForm configForm = ConfigMapper.toConfigForm(configRepository.findByCode(key), value);

            if (applicationConfigValue == null &&
                    (commonApplicationValue == null || !commonApplicationValue.equals(value))) {
                updatedKeyValues.put(key, value);
                audit(configForm, EventTypeEnum.APPLICATION_CONFIG_CREATE);
            } else if (applicationConfigValue != null && !applicationConfigValue.equals(value)) {
                updatedKeyValues.put(key, value);
                audit(configForm, EventTypeEnum.APPLICATION_CONFIG_UPDATE);
            }
            deletedKeyValues.remove(key);
        }

        for (Map.Entry<String, String> e : deletedKeyValues.entrySet()) {
            ConfigEntity configEntity = configRepository.findByCode(e.getKey());
            if (configEntity == null) configEntity = new ConfigEntity();
            audit(ConfigMapper.toConfigForm(configEntity, e.getValue()), EventTypeEnum.APPLICATION_CONFIG_DELETE);
        }

        configValueService.saveAllValues(code, updatedKeyValues, deletedKeyValues);
    }

    @Override
    @Transactional
    public void deleteApplicationConfig(String code) {
        Optional.ofNullable(applicationRepository.findByCode(code)).orElseThrow(NotFoundException::new);
        List<ConfigEntity> configEntities = configRepository.findByApplicationCode(code);

        for (ConfigEntity e : configEntities) {
            try {
                String value = configValueService.getValue(code, e.getCode());
                audit(ConfigMapper.toConfigForm(e, value), EventTypeEnum.APPLICATION_CONFIG_DELETE);
            } catch (Exception ignored) {
            }
        }
        configValueService.deleteAllValues(code);
    }

    private void audit(ConfigForm configForm, EventTypeEnum eventType) {
        auditService.audit(eventType.getTitle(), configForm, configForm.getCode(), ObjectTypeEnum.APPLICATION_CONFIG.getTitle());
    }
}
