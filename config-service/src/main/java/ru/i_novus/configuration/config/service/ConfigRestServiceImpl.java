package ru.i_novus.configuration.config.service;

import net.n2oapp.platform.i18n.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.ConfigResponse;
import ru.i_novus.config.api.model.GroupForm;
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

    @Override
    @Transactional(readOnly = true)
    public Page<ConfigResponse> getAllConfig(ConfigCriteria criteria) {
        ConfigSpecification specification = new ConfigSpecification(criteria);
        return configRepository.findAll(specification, criteria)
                .map(e -> {
                            GroupEntity groupEntity = groupRepository.findOneGroupByConfigCodeStarts(e.getCode());
                            GroupForm groupForm = (groupEntity == null) ? null : GroupMapper.toGroupForm(groupEntity);
                            return ConfigMapper.toConfigResponse(e, groupForm);
                        }
                );
    }

    @Override
    @Transactional(readOnly = true)
    public ConfigResponse getConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow(NotFoundException::new);
        GroupEntity groupEntity = groupRepository.findOneGroupByConfigCodeStarts(configEntity.getCode());
        GroupForm groupForm = (groupEntity == null) ? null : GroupMapper.toGroupForm(groupEntity);
        return ConfigMapper.toConfigResponse(configEntity, groupForm);
    }

    @Override
    @Transactional
    public void saveConfig(@Valid @NotNull ConfigForm configForm) {
        if (configRepository.existsByCode(configForm.getCode()))
            throw new UserException(messageAccessor.getMessage("config.code.not.unique"));

        GroupEntity group = null;
        ApplicationEntity application = applicationRepository.findByCode(configForm.getApplicationCode());

        if (configForm.getGroupId() != null)
            group = groupRepository.findById(configForm.getGroupId())
                    .orElseThrow(() -> new UserException(messageAccessor.getMessage(
                            "config.group.not.found.by.id", new Object[]{configForm.getGroupId()})));

        ConfigEntity configEntity = ConfigMapper.toConfigEntity(configForm);
        if (application == null && configForm.getApplicationCode() != null) {
            application = new ApplicationEntity(configForm.getApplicationCode());
        }
        configEntity.setApplication(application);
        configEntity.setGroup(group);

        configRepository.save(configEntity);
        audit(configEntity, EventTypeEnum.CONFIG_CREATE);
    }

    @Override
    @Transactional
    public void updateConfig(String code, @Valid @NotNull ConfigForm configForm) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow(NotFoundException::new);

        GroupEntity group = null;
        if (configForm.getGroupId() != null)
            group = groupRepository.findById(configForm.getGroupId())
                    .orElseThrow(() -> new UserException(messageAccessor.getMessage(
                            "config.group.not.found.by.id", new Object[]{configForm.getGroupId()})));

        configEntity = ConfigMapper.toConfigEntity(configEntity, configForm);
        if (configForm.getApplicationCode() != null) {
            configEntity.setApplication(applicationRepository.findByCode(configForm.getApplicationCode()));
        } else if (configEntity.getApplication() != null && configForm.getApplicationCode() == null) {
            configEntity.setApplication(null);
        }
        configEntity.setGroup(group);
        configRepository.save(configEntity);

        if (configEntity.getApplication() != null && configEntity.getApplication().getCode() != null &&
                !configEntity.getApplication().getCode().equals(configForm.getApplicationCode())) {
            String value;
            try {
                value = configValueService.getValue(configEntity.getApplication().getCode(), code);
                configValueService.saveValue(configForm.getApplicationCode(), code, value);
                configValueService.deleteValue(configEntity.getApplication().getCode(), code);
            } catch (Exception ignored) {
            }
        }

        audit(configEntity, EventTypeEnum.CONFIG_UPDATE);
    }

    @Override
    @Transactional
    public void deleteConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow(NotFoundException::new);

        configRepository.deleteByCode(code);
        if (configEntity.getApplication() != null) {
            configValueService.deleteValue(configEntity.getApplication().getCode(), code);
        }
        audit(configEntity, EventTypeEnum.APPLICATION_CONFIG_DELETE);
    }

    private void audit(ConfigEntity configEntity, EventTypeEnum eventType) {
        auditService.audit(eventType.getTitle(), configEntity, configEntity.getCode(), ObjectTypeEnum.CONFIG.getTitle());
    }
}
