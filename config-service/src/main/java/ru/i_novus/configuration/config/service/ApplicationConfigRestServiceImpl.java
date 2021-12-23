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
    public void saveApplicationConfig(String code, Map<String, Object> data) {

    }

    @Override
    @Transactional
    public void deleteApplicationConfigValue(String code) {
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
