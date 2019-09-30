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
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.ConfigResponse;
import ru.i_novus.config.api.model.EventTypeEnum;
import ru.i_novus.config.api.model.ObjectTypeEnum;
import ru.i_novus.config.api.service.ConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.service.entity.*;
import ru.i_novus.config.service.mapper.ConfigMapper;
import ru.i_novus.config.service.mapper.GroupMapper;
import ru.i_novus.config.service.repository.ConfigRepository;
import ru.i_novus.config.service.repository.GroupRepository;
import ru.i_novus.ms.audit.client.AuditClient;
import ru.i_novus.ms.audit.client.model.AuditClientRequest;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.service.ApplicationRestService;
import ru.i_novus.system_application.service.entity.QApplicationEntity;
import ru.i_novus.system_application.service.mapper.ApplicationMapper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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

    private AuditClient auditClient;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${spring.cloud.consul.config.defaultContext}")
    private String defaultAppCode;

    @Value("${config.common.system.code}")
    private String commonSystemCode;

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

    @Autowired
    public void setAuditClient(AuditClient auditClient) {
        this.auditClient = auditClient;
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

            if (systemCodes.contains(commonSystemCode)) {
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
                                    e, application,
                                    GroupMapper.toGroupForm(groupRepository.findOneGroupByConfigCodeStarts(e.getCode()))
                            );
                        }
                );
    }

    @Override
    public ConfigResponse getConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();

        ApplicationResponse application = getApplicationResponse(configEntity.getApplicationCode());
        GroupEntity groupEntity = groupRepository.findOneGroupByConfigCodeStarts(configEntity.getCode());

        return ConfigMapper.toConfigResponse(configEntity, application, GroupMapper.toGroupForm(groupEntity));
    }

    @Override
    public void saveConfig(@Valid @NotNull ConfigForm configForm) {
        if (configRepository.existsByCode(configForm.getCode())) {
            throw new UserException("config.code.not.unique");
        }

        ConfigEntity configEntity = ConfigMapper.toConfigEntity(configForm);

        configRepository.save(configEntity);
        audit(configEntity, EventTypeEnum.CONFIG_CREATE);
    }

    @Override
    @Transactional
    public void updateConfig(String code, @Valid @NotNull ConfigForm configForm) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();

        configEntity.setName(configForm.getName());
        configEntity.setValueType(configForm.getValueType());
        configEntity.setDescription(configForm.getDescription());

        if (configEntity.getApplicationCode() != null &&
                !configEntity.getApplicationCode().equals(configForm.getApplicationCode())) {
            String value;
            try {
                value = configValueService.getValue(configEntity.getApplicationCode(), code);
            } catch (Exception e) {
                value = configValueService.getValue(defaultAppCode, code);
            }
            configValueService.saveValue(configForm.getApplicationCode(), code, value);
            configValueService.deleteValue(configEntity.getApplicationCode(), code);
        }

        configEntity.setApplicationCode(configForm.getApplicationCode());
        configRepository.save(configEntity);
        audit(configEntity, EventTypeEnum.CONFIG_UPDATE);
    }

    @Override
    public void deleteConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();

        configRepository.deleteByCode(code);
        configValueService.deleteValue(configEntity.getApplicationCode(), code);
        audit(configEntity, EventTypeEnum.APPLICATION_CONFIG_DELETE);
    }

    private ApplicationResponse getApplicationResponse(String code) {
        return code == null ? ApplicationMapper.getCommonSystemApplication() : applicationRestService.getApplication(code);
    }

    private void audit(ConfigEntity configEntity, EventTypeEnum eventType) {
        AuditClientRequest request = new AuditClientRequest();
        request.setEventDate(LocalDateTime.now());
        request.setEventType(eventType.toString());
        request.setObjectType(ObjectTypeEnum.CONFIG.toString());
        request.setObjectId(configEntity.getCode());
        request.setObjectName(ObjectTypeEnum.CONFIG.getTitle());
        request.setContext(configEntity.toString());
        auditClient.add(request);
    }
}
