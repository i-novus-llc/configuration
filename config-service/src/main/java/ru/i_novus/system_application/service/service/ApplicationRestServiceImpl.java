package ru.i_novus.system_application.service.service;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.i_novus.config.api.model.*;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.service.entity.ConfigEntity;
import ru.i_novus.config.service.mapper.ConfigMapper;
import ru.i_novus.config.service.mapper.GroupedApplicationConfigMapper;
import ru.i_novus.config.service.repository.ConfigRepository;
import ru.i_novus.config.service.utils.AuditHelper;
import ru.i_novus.ms.audit.client.AuditClient;
import ru.i_novus.ms.audit.client.model.AuditClientRequest;
import ru.i_novus.system_application.api.criteria.ApplicationCriteria;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.service.ApplicationRestService;
import ru.i_novus.system_application.service.entity.ApplicationEntity;
import ru.i_novus.system_application.service.entity.QApplicationEntity;
import ru.i_novus.system_application.service.mapper.ApplicationMapper;
import ru.i_novus.system_application.service.repository.ApplicationRepository;

import javax.ws.rs.NotFoundException;
import java.util.*;

/**
 * Реализация REST сервиса для работы с приложениями
 */
@Service
@Primary
public class ApplicationRestServiceImpl implements ApplicationRestService {

    private ApplicationRepository applicationRepository;
    private ConfigValueService configValueService;
    private ConfigRepository configRepository;

    private AuditClient auditClient;

    @Value("${spring.cloud.consul.config.defaultContext}")
    private String defaultAppCode;

    @Value("${config.common.system.code}")
    private String commonSystemCode;

    @Autowired
    public void setApplicationRepository(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Autowired
    public void setConfigValueService(ConfigValueService configValueService) {
        this.configValueService = configValueService;
    }

    @Autowired
    public void setConfigRepository(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Autowired
    public void setAuditClient(AuditClient auditClient) {
        this.auditClient = auditClient;
    }


    @Override
    public Page<ApplicationResponse> getAllApplication(ApplicationCriteria criteria) {
        return applicationRepository.findAll(toPredicate(criteria), criteria)
                .map(ApplicationMapper::toApplicationResponse);
    }

    @Override
    public ApplicationResponse getApplication(String code) {
        ApplicationEntity applicationEntity = Optional.ofNullable(applicationRepository.findByCode(code)).orElseThrow(NotFoundException::new);
        return ApplicationMapper.toApplicationResponse(applicationEntity);
    }

    @Override
    public List<GroupedApplicationConfig> getGroupedApplicationConfig(String appCode) {
        if (appCode.equals(commonSystemCode))
            appCode = null;
        else
            Optional.ofNullable(applicationRepository.findByCode(appCode)).orElseThrow(NotFoundException::new);

        List<Object[]> objectList = configRepository.findGroupedConfigByAppCode(appCode);
        List<GroupedApplicationConfig> result = new ArrayList<>();

        Map<String, String> commonApplicationConfigKeyValues = Collections.EMPTY_MAP;
        try {
            commonApplicationConfigKeyValues = configValueService.getKeyValueList(defaultAppCode);
        } catch (Exception ignored) {
        }

        Map<String, String> applicationConfigKeyValues = Collections.EMPTY_MAP;
        if (appCode != null) {
            try {
                applicationConfigKeyValues = configValueService.getKeyValueList(appCode);
            } catch (Exception ignored) {
            }
        }

        for (Object[] obj : objectList) {
            GroupForm groupForm = new GroupForm();
            if (obj[0] != null) {
                groupForm.setId((Integer) obj[0]);
                groupForm.setName((String) obj[1]);
            }
            ConfigForm configForm = new ConfigForm();
            configForm.setCode((String) obj[2]);
            configForm.setName((String) obj[3]);
            configForm.setDescription((String) obj[4]);
            configForm.setValueType(ValueTypeEnum.valueOf((String) obj[5]));
            configForm.setDefaultValue((String) obj[6]);
            configForm.setApplicationCode((String) obj[7]);

            String value;
            if (appCode != null) {
                value = applicationConfigKeyValues.get(configForm.getCode());
                if (commonApplicationConfigKeyValues.containsKey(configForm.getCode())) {
                    String defaultValue = commonApplicationConfigKeyValues.get(configForm.getCode());
                    if (defaultValue != null) {
                        configForm.setDefaultValue(defaultValue);
                    }
                }
            } else {
                value = commonApplicationConfigKeyValues.get(configForm.getCode());
            }
            configForm.setValue(value);

            GroupedApplicationConfig existingGroupedApplicationConfig =
                    result.stream().filter(i -> Objects.equals(i.getId(), groupForm.getId())).findFirst().orElse(null);
            if (existingGroupedApplicationConfig != null) {
                existingGroupedApplicationConfig.getConfigs().add(configForm);
            } else {
                result.add(GroupedApplicationConfigMapper.toGroupedApplicationConfig(groupForm, Lists.newArrayList(configForm)));
            }
        }

        return result;
    }

    @Override
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
            String key = ((String) entry.getKey()).replace("@", ".");
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

    private Predicate toPredicate(ApplicationCriteria criteria) {
        QApplicationEntity qApplicationEntity = QApplicationEntity.applicationEntity;

        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.getSystemCode() != null) {
            builder.and(qApplicationEntity.system.code.eq(criteria.getSystemCode()));
        }
        builder.and(qApplicationEntity.isDeleted.isFalse().or(qApplicationEntity.isDeleted.isNull()));

        return builder.getValue();
    }

    private void audit(ConfigForm configForm, EventTypeEnum eventType) {
        AuditClientRequest request = AuditHelper.getAuditClientRequest();
        request.setEventType(eventType.getTitle());
        request.setObjectType(ObjectTypeEnum.APPLICATION_CONFIG.toString());
        request.setObjectId(configForm.getCode());
        request.setObjectName(ObjectTypeEnum.APPLICATION_CONFIG.getTitle());
        request.setContext(AuditHelper.getContext(configForm));
        auditClient.add(request);
    }
}
