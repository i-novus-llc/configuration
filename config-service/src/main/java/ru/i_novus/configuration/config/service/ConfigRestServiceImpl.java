package ru.i_novus.configuration.config.service;

import net.n2oapp.platform.i18n.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.ConfigResponse;
import ru.i_novus.config.api.model.enums.EventTypeEnum;
import ru.i_novus.config.api.model.enums.ObjectTypeEnum;
import ru.i_novus.config.api.service.ApplicationRestService;
import ru.i_novus.config.api.service.ConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.api.util.AuditService;
import ru.i_novus.configuration.config.entity.ApplicationEntity;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.entity.GroupEntity;
import ru.i_novus.configuration.config.mapper.ConfigMapper;
import ru.i_novus.configuration.config.mapper.GroupMapper;
import ru.i_novus.configuration.config.repository.ApplicationRepository;
import ru.i_novus.configuration.config.repository.ConfigRepository;
import ru.i_novus.configuration.config.repository.GroupRepository;
import ru.i_novus.configuration.config.specification.ConfigSpecification;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
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
    private ApplicationRepository applicationRepository;
    @Autowired
    private AuditService auditService;
    @Autowired
    private MessageSourceAccessor messageAccessor;

    @Value("${spring.cloud.consul.config.defaultContext}")
    private String commonSystemCode;


    @Override
    @Transactional(readOnly = true)
    public Page<ConfigResponse> getAllConfig(ConfigCriteria criteria) {
        ConfigSpecification specification = new ConfigSpecification(criteria);
        return configRepository.findAll(specification, criteria).map(e -> ConfigMapper.toConfigResponse(e, GroupMapper.toGroupForm(e.getGroup())));
    }

    @Override
    @Transactional(readOnly = true)
    public ConfigResponse getConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow(NotFoundException::new);
        return ConfigMapper.toConfigResponse(configEntity, GroupMapper.toGroupForm(configEntity.getGroup()));
    }

    @Override
    @Transactional
    public void saveConfig(@Valid @NotNull ConfigForm configForm) {
        if (configRepository.existsByCode(configForm.getCode()))
            throw new UserException(messageAccessor.getMessage("config.code.not.unique"));

        ConfigEntity configEntity = ConfigMapper.toConfigEntity(configForm);

        if (configForm.getGroupId() != null) {
            GroupEntity group = groupRepository.findById(configForm.getGroupId())
                    .orElseThrow(() -> new UserException(messageAccessor.getMessage(
                            "config.group.not.found.by.id", new Object[]{configForm.getGroupId()})));
            configEntity.setGroup(group);
        }

        if (configForm.getApplicationCode() != null) {
            ApplicationEntity application = applicationRepository.findByCode(configForm.getApplicationCode());
            if (application == null)
                throw new UserException(messageAccessor.getMessage(
                        "application.not.found.by.code", new Object[]{configForm.getApplicationCode()}));
            configEntity.setApplication(application);
        }

        configRepository.save(configEntity);
        audit(configEntity, EventTypeEnum.CONFIG_CREATE);
    }

    @Override
    @Transactional
    public void updateConfig(String code, @Valid @NotNull ConfigForm configForm) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow(NotFoundException::new);
        configEntity = ConfigMapper.toConfigEntity(configEntity, configForm);

        GroupEntity group = null;
        if (configForm.getGroupId() != null)
            group = groupRepository.findById(configForm.getGroupId())
                    .orElseThrow(() -> new UserException(messageAccessor.getMessage(
                            "config.group.not.found.by.id", new Object[]{configForm.getGroupId()})));
        configEntity.setGroup(group);

        if (configForm.getApplicationCode() != null) {
            ApplicationEntity application = applicationRepository.findByCode(configForm.getApplicationCode());
            if (application == null)
                throw new UserException(messageAccessor.getMessage(
                        "application.not.found.by.code", new Object[]{configForm.getApplicationCode()}));
            configEntity.setApplication(application);
        } else if (configEntity.getApplication() != null && configForm.getApplicationCode() == null) {
            configEntity.setApplication(null);
        }

        configRepository.save(configEntity);
        rewriteConfigValue(configForm, configEntity);

        audit(configEntity, EventTypeEnum.CONFIG_UPDATE);
    }

    @Override
    @Transactional
    public void deleteConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow(NotFoundException::new);

        configRepository.deleteByCode(code);
        String appCode = configEntity.getApplication() != null ? configEntity.getApplication().getCode() : commonSystemCode;
        configValueService.deleteValue(appCode, code);

        audit(configEntity, EventTypeEnum.APPLICATION_CONFIG_DELETE);
    }

    private void rewriteConfigValue(@Valid @NotNull ConfigForm configForm, ConfigEntity configEntity) {
        String oldAppCode = null;
        String newAppCode = null;

        if (configEntity.getApplication() == null && configForm.getApplicationCode() != null) {
            oldAppCode = commonSystemCode;
            newAppCode = configForm.getApplicationCode();
        } else if (configEntity.getApplication() != null && configForm.getApplicationCode() == null) {
            oldAppCode = configEntity.getApplication().getCode();
            newAppCode = commonSystemCode;
        } else if (configEntity.getApplication() != null && configEntity.getApplication().getCode() != null &&
                !configEntity.getApplication().getCode().equals(configForm.getApplicationCode())) {
            oldAppCode = configEntity.getApplication().getCode();
            newAppCode = configForm.getApplicationCode();
        }

        if (newAppCode != null) {
            try {
                String value = configValueService.getValue(oldAppCode, configEntity.getCode());
                configValueService.saveValue(newAppCode, configEntity.getCode(), value);
                configValueService.deleteValue(oldAppCode, configEntity.getCode());
            } catch (Exception ignored) {
            }
        }
    }

    private void audit(ConfigEntity configEntity, EventTypeEnum eventType) {
        auditService.audit(eventType.getTitle(), configEntity, configEntity.getCode(), ObjectTypeEnum.CONFIG.getTitle());
    }
}
