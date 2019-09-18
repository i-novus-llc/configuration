package ru.i_novus.config.service.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import net.n2oapp.platform.i18n.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.config.api.model.ConfigRequest;
import ru.i_novus.config.api.model.ConfigResponse;
import ru.i_novus.config.api.service.ConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.service.entity.*;
import ru.i_novus.config.service.mapper.ConfigMapper;
import ru.i_novus.config.service.mapper.GroupMapper;
import ru.i_novus.config.service.repository.ConfigRepository;
import ru.i_novus.config.service.repository.GroupRepository;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.service.ApplicationRestService;
import ru.i_novus.system_application.service.CommonSystemResponse;
import ru.i_novus.system_application.service.entity.QApplicationEntity;
import ru.i_novus.system_application.service.mapper.ApplicationMapper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    private ApplicationRestService applicationRestService;

    private ConfigRepository configRepository;
    private GroupRepository groupRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${config.application.default.name}")
    private String defaultAppName;

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
        QConfigEntity qConfigEntity = QConfigEntity.configEntity;
        QGroupEntity qGroupEntity = QGroupEntity.groupEntity;
        QGroupCodeEntity qGroupCodeEntity = QGroupCodeEntity.groupCodeEntity;
        QApplicationEntity qApplicationEntity = QApplicationEntity.applicationEntity;

        JPAQuery<ConfigEntity> query = new JPAQuery(entityManager);

        query.from(qConfigEntity)
                .leftJoin(qApplicationEntity).on(qConfigEntity.applicationCode.eq(qApplicationEntity.code));

        List<Integer> groupIds = criteria.getGroupIds();
        if (groupIds != null && !groupIds.isEmpty()) {
            BooleanExpression exists = JPAExpressions.selectOne().from(qGroupCodeEntity).from(qGroupEntity)
                    .where(new BooleanBuilder()
                            .and(qGroupCodeEntity.group.id.eq(qGroupEntity.id))
                            .and(qGroupEntity.id.in(groupIds))
                            .and(qConfigEntity.code.startsWithIgnoreCase(qGroupCodeEntity.code)))
                    .exists();
            query.where(exists);
        }

        if (criteria.getCode() != null) {
            query.where(qConfigEntity.code.containsIgnoreCase(criteria.getCode()));
        }

        if (criteria.getName() != null) {
            query.where(qConfigEntity.name.containsIgnoreCase(criteria.getName()));
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

            query.where(exists);
        }

        query.orderBy(qApplicationEntity.system.code.asc().nullsFirst(), qConfigEntity.code.asc())
                .limit(criteria.getPageSize())
                .offset(criteria.getOffset());
        long total = query.fetchCount();

        return new PageImpl<>(query.fetch(), criteria, total)
                .map(e -> {
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
    }

    @Override
    @Transactional
    public void updateConfig(String code, @Valid @NotNull ConfigRequest configRequest) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();

        configEntity.setName(configRequest.getName());
        configEntity.setValueType(configRequest.getValueType());
        configEntity.setDescription(configRequest.getDescription());

        // --TODO необходимо учесть случай при котором меняется applicationCode
//        if (configEntity.getApplicationCode() != null &&
//                configRequest.getApplicationCode() != configEntity.getApplicationCode()) {
//            String appName = getAppName(applicationRestService.getApplication(configEntity.getApplicationCode()));
//            if (configRequest != null) {
//                String value = configValueService.getValue(appName, configRequest.getCode());
//                configValueService.saveValue(appName, code, value);
//            }
//            configValueService.deleteValue(appName, code);
//        }

        configEntity.setApplicationCode(configRequest.getApplicationCode());
        configRepository.save(configEntity);
    }

    @Override
    public void deleteConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();
        ApplicationResponse application = getApplicationResponse(configEntity.getApplicationCode());

        configRepository.deleteByCode(code);
        configValueService.deleteValue(getAppName(application), code);
    }


    private String getAppName(ApplicationResponse application) {
        return (application != null && application.getCode() != null) ? application.getName() : defaultAppName;
    }

    private ApplicationResponse getApplicationResponse(String code) {
        return code == null ? ApplicationMapper.getCommonSystemApplication() : applicationRestService.getApplication(code);
    }
}
