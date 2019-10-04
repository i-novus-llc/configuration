package ru.i_novus.system_application.service.service;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.api.model.GroupedApplicationConfig;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.service.entity.ConfigEntity;
import ru.i_novus.config.service.entity.GroupEntity;
import ru.i_novus.config.service.mapper.ConfigMapper;
import ru.i_novus.config.service.mapper.GroupMapper;
import ru.i_novus.config.service.mapper.GroupedApplicationConfigMapper;
import ru.i_novus.config.service.repository.ConfigRepository;
import ru.i_novus.system_application.api.criteria.ApplicationCriteria;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.service.ApplicationRestService;
import ru.i_novus.system_application.service.entity.ApplicationEntity;
import ru.i_novus.system_application.service.entity.QApplicationEntity;
import ru.i_novus.system_application.service.mapper.ApplicationMapper;
import ru.i_novus.system_application.service.repository.ApplicationRepository;

import java.util.*;

/**
 * Реализация REST сервиса для получения приложений
 */
@Service
@Primary
public class ApplicationRestServiceImpl implements ApplicationRestService {

    private ApplicationRepository applicationRepository;

    private ConfigValueService configValueService;

    private ConfigRepository configRepository;

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


    @Override
    public Page<ApplicationResponse> getAllApplication(ApplicationCriteria criteria) {
        criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "code"));

        return applicationRepository.findAll(toPredicate(criteria), criteria)
                .map(ApplicationMapper::toApplicationResponse);
    }

    @Override
    public ApplicationResponse getApplication(String code) {
        ApplicationEntity applicationEntity = applicationRepository.findByCode(code);
        return applicationEntity != null ? ApplicationMapper.toApplicationResponse(applicationEntity) : null;
    }

    @Override
    public List<GroupedApplicationConfig> getGroupedApplicationConfig(String appCode) {
        if (appCode.equals(commonSystemCode))
            appCode = null;

        List<Object[]> objectList = configRepository.findByAppCode(appCode);
        List<GroupedApplicationConfig> result = new ArrayList<>();

        Map<String, String> commonApplicationConfigKeyValues = configValueService.getKeyValueList(defaultAppCode);

        Map<String, String> applicationConfigKeyValues = null;
        boolean applicationConfigsNotExist = false;
        if (appCode != null) {
            try {
                applicationConfigKeyValues = configValueService.getKeyValueList(appCode);
            } catch (Exception e) {
                applicationConfigsNotExist = true;
            }
        }

        for (Object[] obj : objectList) {
            GroupForm groupForm = GroupMapper.toGroupForm((GroupEntity) obj[0]);
            ConfigEntity configEntity = (ConfigEntity) obj[1];

            String value;
            if (appCode != null && !applicationConfigsNotExist) {
                value = applicationConfigKeyValues.get(configEntity.getCode());
                if (value == null) {
                    value = commonApplicationConfigKeyValues.get(configEntity.getCode());
                }
            } else {
                value = commonApplicationConfigKeyValues.get(configEntity.getCode());
            }
            ConfigForm configForm = ConfigMapper.toConfigForm(configEntity, value);

            GroupedApplicationConfig existingGroupedApplicationConfig =
                    result.stream().filter(i -> i.getId().equals(groupForm.getId())).findFirst().orElse(null);
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
        if (data.get("data") == null) return;

        Map<String, String> updatedKeyValues = new HashMap<>();

        Map<String, String> commonApplicationConfigKeyValues =
                configValueService.getKeyValueList(defaultAppCode);
        Map<String, String> applicationConfigKeyValues = Collections.EMPTY_MAP;
        if (!code.equals(commonSystemCode)) {
            try {
                applicationConfigKeyValues = configValueService.getKeyValueList(code);
            } catch (Exception e) {}
        } else {
            code = defaultAppCode;
        }

        for (Map.Entry entry : ((Map<String, Object>) data.get("data")).entrySet()) {
            String key = ((String) entry.getKey()).replace("@", ".");
            String value = String.valueOf(entry.getValue());
            String applicationConfigValue = applicationConfigKeyValues.get(key);
            String commonApplicationValue = commonApplicationConfigKeyValues.get(key);

            if ((applicationConfigValue == null && commonApplicationValue == null) ||
                    (applicationConfigValue != null && !applicationConfigValue.equals(value)) ||
                    (applicationConfigValue == null && !commonApplicationValue.equals(value))) {
                updatedKeyValues.put(key, value);
            }
        }

        if (!updatedKeyValues.isEmpty()) {
            configValueService.saveAllValues(code, updatedKeyValues);
        }
    }

    @Override
    public void deleteApplicationConfig(String code) {
        configValueService.deleteAllValues(code);
    }

    private Predicate toPredicate(ApplicationCriteria criteria) {
        QApplicationEntity qApplicationEntity = QApplicationEntity.applicationEntity;

        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.getSystemCode() != null) {
            builder.and(qApplicationEntity.system.code.eq(criteria.getSystemCode()));
        }

        return builder.getValue();
    }
}
