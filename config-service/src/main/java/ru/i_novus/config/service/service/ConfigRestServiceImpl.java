package ru.i_novus.config.service.service;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import net.n2oapp.platform.i18n.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.config.api.model.ConfigRequest;
import ru.i_novus.config.api.model.ConfigResponse;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.api.model.GroupedConfigRequest;
import ru.i_novus.config.api.service.ConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.service.entity.*;
import ru.i_novus.config.service.mapper.ConfigMapper;
import ru.i_novus.config.service.mapper.GroupMapper;
import ru.i_novus.config.service.mapper.GroupedConfigRequestMapper;
import ru.i_novus.config.service.repository.ConfigRepository;
import ru.i_novus.config.service.repository.GroupRepository;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.service.ApplicationRestService;
import ru.i_novus.system_application.service.CommonSystemResponse;
import ru.i_novus.system_application.service.entity.QApplicationEntity;
import ru.i_novus.system_application.service.mapper.ApplicationMapper;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Реализация REST сервиса для работы с настройками
 */
@Service
public class ConfigRestServiceImpl implements ConfigRestService {

    private ConfigValueService configValueService;
    private ApplicationRestService applicationRestService;

    private ConfigRepository configRepository;
    private GroupRepository groupRepository;


    @Autowired
    public void setConfigValueService(ConfigValueService configValueService) {
        this.configValueService = configValueService;
    }

    @Autowired
    public void setApplicationRestService(ApplicationRestService applicationRestService) {
        this.applicationRestService = applicationRestService;
    }

    @Autowired
    public void setConfigRepository(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }


    @Override
    public Page<ConfigResponse> getAllConfig(ConfigCriteria criteria) {
        criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "code"));

        Page<ConfigEntity> configEntities = configRepository.findAll(toPredicate(criteria), criteria);

        return configEntities.map(e -> {
                    ApplicationResponse application = getApplicationResponse(e.getApplicationCode());
                    return ConfigMapper.toConfigResponse(
                            e,
                            configValueService.getValue(getAppName(application), e.getCode()),
                            application,
                            GroupMapper.toGroupForm(groupRepository.findOneGroupByConfigCodeStarts(e.getCode()))
                    );
                }
        );
    }

    @Override
    public List<GroupedConfigRequest> getGroupedConfigByAppCode(String appCode) {
        List<Object[]> objectList = configRepository.findByAppCode(appCode);
        List<GroupedConfigRequest> result = new ArrayList<>();
        String appName = applicationRestService.getApplication(appCode).getName();

        Map<String, String> applicationConfigKeyValues =
                configValueService.getKeyValueListByApplicationName(appName);
        Map<String, String> commonApplicationConfigKeyValues =
                configValueService.getKeyValueListByApplicationName(getAppName(null));


        for (Object[] obj : objectList) {
            GroupForm groupForm = GroupMapper.toGroupForm((GroupEntity) obj[0]);
            ConfigEntity configEntity = (ConfigEntity) obj[1];

            String value = applicationConfigKeyValues.get(configEntity.getCode());
            if (value == null) {
                value = commonApplicationConfigKeyValues.get(configEntity.getCode());
            }
            ConfigRequest configRequest = ConfigMapper.toConfigRequest(configEntity, value);

            GroupedConfigRequest existingGroupedConfigRequest =
                    result.stream().filter(i -> i.getId().equals(groupForm.getId())).findFirst().orElse(null);
            if (existingGroupedConfigRequest != null) {
                existingGroupedConfigRequest.getConfigs().add(configRequest);
            } else {
                result.add(GroupedConfigRequestMapper.toGroupedConfigRequest(groupForm, Lists.newArrayList(configRequest)));
            }
        }

        return result;
    }

    @Override
    public void saveApplicationConfig(Map<String, Object> data) {
        String appName = applicationRestService.getApplication((String) data.get("appCode")).getName();
        Map<String, String> updatedKeyValues = new HashMap<>();

        Map<String, String> applicationConfigKeyValues =
                configValueService.getKeyValueListByApplicationName(appName);
        Map<String, String> commonApplicationConfigKeyValues =
                configValueService.getKeyValueListByApplicationName(getAppName(null));

        for (Map.Entry entry : ((Map<String, Object>) data.get("data")).entrySet()) {
            String code = ((String) entry.getKey()).replace("*", ".");
            String value = String.valueOf(entry.getValue());
            String applicationConfigValue = applicationConfigKeyValues.get(code);
            String commonApplicationValue = commonApplicationConfigKeyValues.get(code);

            if (applicationConfigValue != null && !applicationConfigValue.equals(value) ||
            applicationConfigValue == null && !commonApplicationValue.equals(value)) {
                updatedKeyValues.put(code, value);
            }
        }

        if (!updatedKeyValues.isEmpty()) {
            configValueService.saveAllValues(appName, updatedKeyValues);
        }
    }

    @Override
    public ConfigResponse getConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();

        ApplicationResponse application = getApplicationResponse(configEntity.getApplicationCode());

        String value = configValueService.getValue(getAppName(application), configEntity.getCode());
        GroupEntity groupEntity = groupRepository.findOneGroupByConfigCodeStarts(configEntity.getCode());

        return ConfigMapper.toConfigResponse(configEntity, value, application, GroupMapper.toGroupForm(groupEntity));
    }

    @Override
    public void saveConfig(@Valid @NotNull ConfigRequest configRequest) {
        if (configRepository.existsByCode(configRequest.getCode())) {
            throw new UserException("config.code.not.unique");
        }

        ConfigEntity configEntity = ConfigMapper.toConfigEntity(configRequest);
        configRepository.save(configEntity);

        // -- TODO возможно придется убрать
//        if (configRequest.getValue() != null) {
//            ApplicationResponse application = getApplicationResponse(configEntity.getApplicationCode());
//            configValueService.saveValue(getAppName(application), configRequest.getCode(), configRequest.getValue());
//        }
    }

    @Override
    @Transactional
    public void updateConfig(String code, @Valid @NotNull ConfigRequest configRequest) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();

        configEntity.setApplicationCode(configRequest.getApplicationCode());
        configEntity.setName(configRequest.getName());
        configEntity.setValueType(configRequest.getValueType());
        configEntity.setDescription(configRequest.getDescription());
        configRepository.save(configEntity);

        // --TODO необходимо учесть случай при котором меняется applicationCode
        // в consul нужно удалить данные по предыдущему url и записать их по новому

        // -- TODO возможно придется убрать
//        if (configRequest.getValue() != null) {
//            ApplicationResponse application = getApplicationResponse(configEntity.getApplicationCode());
//            configValueService.saveValue(getAppName(application), configRequest.getCode(), configRequest.getValue());
//        }
    }

    @Override
    public void deleteConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();
        ApplicationResponse application = getApplicationResponse(configEntity.getApplicationCode());

        configRepository.deleteByCode(code);
        configValueService.deleteValue(getAppName(application), code);
    }

    private Predicate toPredicate(ConfigCriteria criteria) {
        QConfigEntity qConfigEntity = QConfigEntity.configEntity;
        QGroupEntity qGroupEntity = QGroupEntity.groupEntity;
        QGroupCodeEntity qGroupCodeEntity = QGroupCodeEntity.groupCodeEntity;
        QApplicationEntity qApplicationEntity = QApplicationEntity.applicationEntity;

        BooleanBuilder builder = new BooleanBuilder();

        List<Integer> groupIds = criteria.getGroupIds();
        if (groupIds != null && !groupIds.isEmpty()) {
            BooleanExpression exists = JPAExpressions.selectOne().from(qGroupCodeEntity).from(qGroupEntity)
                    .where(new BooleanBuilder()
                            .and(qGroupCodeEntity.group.id.eq(qGroupEntity.id))
                            .and(qGroupEntity.id.in(groupIds))
                            .and(qConfigEntity.code.startsWithIgnoreCase(qGroupCodeEntity.code)))
                    .exists();
            builder.and(exists);
        }

        if (criteria.getCode() != null) {
            builder.and(qConfigEntity.code.containsIgnoreCase(criteria.getCode()));
        }

        if (criteria.getName() != null) {
            builder.and(qConfigEntity.name.containsIgnoreCase(criteria.getName()));
        }

        List<String> systemCodes = criteria.getSystemCodes();
        if (systemCodes != null && !systemCodes.isEmpty()) {
            BooleanBuilder exists = new BooleanBuilder().and(JPAExpressions.selectOne().from(qApplicationEntity)
                    .where(new BooleanBuilder()
                            .and(qConfigEntity.applicationCode.eq(qApplicationEntity.code))
                            .and(qApplicationEntity.system.code.in(systemCodes)))
                    .exists());

            if (systemCodes.contains(new CommonSystemResponse().getCode())) {
                exists.or(qConfigEntity.applicationCode.isNull());
            }

            builder.and(exists);
        }

        // TODO отсортировать по systemCode
        return builder.getValue();
    }

    private String getAppName(ApplicationResponse application) {
        return (application != null && application.getCode() != null) ? application.getName() : "application";
    }

    private ApplicationResponse getApplicationResponse(String code) {
        return code == null ? ApplicationMapper.getCommonSystemApplication() : applicationRestService.getApplication(code);
    }
}
