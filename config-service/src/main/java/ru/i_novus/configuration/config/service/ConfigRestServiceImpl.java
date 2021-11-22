package ru.i_novus.configuration.config.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import net.n2oapp.platform.i18n.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.config.api.model.*;
import ru.i_novus.config.api.service.ConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.configuration.config.entity.*;
import ru.i_novus.configuration.config.mapper.ConfigMapper;
import ru.i_novus.configuration.config.mapper.GroupMapper;
import ru.i_novus.configuration.config.repository.ConfigRepository;
import ru.i_novus.configuration.config.repository.GroupRepository;
import ru.i_novus.configuration.config.utils.AuditHelper;
import ru.i_novus.ms.audit.client.AuditClient;
import ru.i_novus.ms.audit.client.model.AuditClientRequest;
import ru.i_novus.system_application.api.model.ApplicationResponse;
import ru.i_novus.system_application.api.service.ApplicationRestService;
import ru.i_novus.configuration.system_application.entity.QApplicationEntity;
import ru.i_novus.configuration.system_application.mapper.ApplicationMapper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * Реализация REST сервиса для работы с настройками
 */
@Service
public class ConfigRestServiceImpl implements ConfigRestService {

    @Autowired
    private ConfigValueService configValueService;
    @Autowired
    private ApplicationRestService applicationRestService;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private AuditClient auditClient;
    @Autowired
    private MessageSourceAccessor messageAccessor;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${spring.cloud.consul.config.defaultContext}")
    private String defaultAppCode;

    @Value("${config.common.system.code}")
    private String commonSystemCode;


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
                            GroupEntity groupEntity = groupRepository.findOneGroupByConfigCodeStarts(e.getCode());
                            ApplicationResponse application = getApplicationResponse(e.getApplicationCode());
                            return ConfigMapper.toConfigResponse(
                                    e, application,
                                    groupEntity == null ? null : GroupMapper.toGroupForm(groupEntity)
                            );
                        }
                );
    }

    @Override
    public ConfigResponse getConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow(NotFoundException::new);

        ApplicationResponse application = getApplicationResponse(configEntity.getApplicationCode());
        GroupEntity groupEntity = groupRepository.findOneGroupByConfigCodeStarts(configEntity.getCode());
        GroupForm groupForm = (groupEntity == null) ? null : GroupMapper.toGroupForm(groupEntity);

        return ConfigMapper.toConfigResponse(configEntity, application, groupForm);
    }

    @Override
    @Transactional
    public void saveConfig(@Valid @NotNull ConfigForm configForm) {
        if (configRepository.existsByCode(configForm.getCode()))
            throw new UserException(messageAccessor.getMessage("config.code.not.unique"));

        ConfigEntity configEntity = ConfigMapper.toConfigEntity(configForm);

        configRepository.save(configEntity);
        audit(configEntity, EventTypeEnum.CONFIG_CREATE);
    }

    @Override
    @Transactional
    public void updateConfig(String code, @Valid @NotNull ConfigForm configForm) {
        ConfigEntity configEntity = ConfigMapper.toConfigEntity(
                Optional.ofNullable(configRepository.findByCode(code)).orElseThrow(NotFoundException::new),
                configForm);

        if (configEntity.getApplicationCode() != null &&
                !configEntity.getApplicationCode().equals(configForm.getApplicationCode())) {
            String value;
            try {
                value = configValueService.getValue(configEntity.getApplicationCode(), code);
                configValueService.saveValue(configForm.getApplicationCode(), code, value);
                configValueService.deleteValue(configEntity.getApplicationCode(), code);
            } catch (Exception ignored) {
            }
        }

        configRepository.save(configEntity);
        audit(configEntity, EventTypeEnum.CONFIG_UPDATE);
    }

    @Override
    @Transactional
    public void deleteConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow(NotFoundException::new);

        configRepository.deleteByCode(code);
        configValueService.deleteValue(configEntity.getApplicationCode(), code);
        audit(configEntity, EventTypeEnum.APPLICATION_CONFIG_DELETE);
    }

    private ApplicationResponse getApplicationResponse(String code) {
        if (code == null) return ApplicationMapper.getCommonSystemApplication();
        ApplicationResponse applicationResponse;

        try {
            applicationResponse = applicationRestService.getApplication(code);
        } catch (Exception e) {
            applicationResponse = null;
        }
        return applicationResponse;
    }

    private void audit(ConfigEntity configEntity, EventTypeEnum eventType) {
        AuditClientRequest request = AuditHelper.getAuditClientRequest();
        request.setEventType(eventType.getTitle());
        request.setObjectType(ObjectTypeEnum.CONFIG.toString());
        request.setObjectId(configEntity.getCode());
        request.setObjectName(ObjectTypeEnum.CONFIG.getTitle());
        request.setContext(AuditHelper.getContext(configEntity));
        auditClient.add(request);
    }
}