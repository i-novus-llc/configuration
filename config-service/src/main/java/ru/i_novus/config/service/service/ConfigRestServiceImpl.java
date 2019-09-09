package ru.i_novus.config.service.service;

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
import ru.i_novus.config.api.service.ConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.service.entity.*;
import ru.i_novus.config.service.repository.ConfigRepository;
import ru.i_novus.config.service.repository.GroupRepository;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.model.System;
import ru.i_novus.system_application.api.model.SystemResponse;
import ru.i_novus.system_application.api.service.ApplicationService;
import ru.i_novus.system_application.api.service.SystemService;
import ru.i_novus.system_application.service.CommonSystemResponse;
import ru.i_novus.system_application.service.entity.QApplicationEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * Реализация REST сервиса для работы с настройками
 */
@Service
public class ConfigRestServiceImpl implements ConfigRestService {

    private ConfigValueService configValueService;
    private SystemService systemService;
    private ApplicationService applicationService;

    private ConfigRepository configRepository;
    private GroupRepository groupRepository;


    @Autowired
    public void setConfigValueService(ConfigValueService configValueService) {
        this.configValueService = configValueService;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @Autowired
    public void setApplicationService(ApplicationService applicationService) {
        this.applicationService = applicationService;
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
                    return e.toConfigResponse(
                            configValueService.getValue(getAppName(application), e.getCode()),
                            application,
                            groupRepository.findOneGroupByConfigCodeStarts(e.getCode()).toGroupForm()
                    );
                }
        );
    }

//    @Override
//    public List<GroupedConfigForm> getGroupedConfigByAppCode(String appCode) {
//        List<Object[]> objectList = configRepository.findByAppCode(appCode);
//
//        List<GroupedConfigForm> result = new ArrayList<>();
//
//        for (Object[] obj : objectList) {
//            GroupForm groupForm = ((GroupEntity) obj[0]).toGroupForm();
//            ConfigEntity configEntity = ((ConfigEntity) obj[1]);
//            Application application = getApplication(configEntity.getApplicationCode());
//
//            ConfigRequest configRequest = new ConfigRequest(
//                    configEntity.getCode(), configEntity.getName(), configEntity.getDescription(),
//                    configEntity.getValueType().getTitle(),
//                    configValueService.getValue(getAppName(configEntity, application), configEntity.getCode()),
//                    configEntity.getApplicationCode()
//            );
//
//            GroupedConfigForm existingGroupedConfigForm =
//                    result.stream().filter(i -> i.getId().equals(groupForm.getId())).findFirst().orElse(null);
//            if (existingGroupedConfigForm != null) {
//                existingGroupedConfigForm.getConfigs().add(configRequest);
//            } else {
//                result.add(new GroupedConfigForm(
//                        groupForm.getId(), groupForm.getName(), groupForm.getDescription(),
//                        groupForm.getPriority(), Lists.newArrayList(configRequest)
//                ));
//            }
//        }
//
//        return result;
//    }

    @Override
    public ConfigResponse getConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();

        ApplicationResponse application = getApplicationResponse(configEntity.getApplicationCode());

        String value = configValueService.getValue(getAppName(application), configEntity.getCode());
        GroupEntity groupEntity = groupRepository.findOneGroupByConfigCodeStarts(configEntity.getCode());

        return configEntity.toConfigResponse(value, application, groupEntity.toGroupForm());
    }

    @Override
    public void saveConfig(@Valid @NotNull ConfigRequest configRequest) {
        if (configRepository.existsByCode(configRequest.getCode())) {
            throw new UserException("config.code.not.unique");
        }

        ConfigEntity configEntity = new ConfigEntity(configRequest);
        configRepository.save(configEntity);

        if (configRequest.getValue() != null) {
            ApplicationResponse application = getApplicationResponse(configEntity.getApplicationCode());
            configValueService.saveValue(getAppName(application), configRequest.getCode(), configRequest.getValue());
        }
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

        if (configRequest.getValue() != null) {
            ApplicationResponse application = getApplicationResponse(configEntity.getApplicationCode());
            configValueService.saveValue(getAppName(application), configRequest.getCode(), configRequest.getValue());
        }
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
            BooleanExpression exists = JPAExpressions.selectOne().from(qApplicationEntity)
                    .where(new BooleanBuilder()
                            .and(qConfigEntity.code.eq(qApplicationEntity.system.code))
                            .and(qApplicationEntity.system.code.in(systemCodes)))
                    .exists();
            builder.and(exists);
        }

        // TODO отсортировать по systemCode
        return builder.getValue();
    }

    private String getAppName(ApplicationResponse application) {
        return (application != null && application.getCode() != null) ? application.getName() : "application";
    }

    private ApplicationResponse getApplicationResponse(String code) {
        if (code == null) {
            CommonSystemResponse commonSystemResponse = new CommonSystemResponse();
            return new ApplicationResponse(null, null,
                    new System(commonSystemResponse.getCode(), commonSystemResponse.getName(), null)
            );
        }
        return applicationService.getApplication(code);
    }
}
