package ru.i_novus.config.service.service;

import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.service.ConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.service.entity.ConfigEntity;
import ru.i_novus.config.service.entity.QConfigEntity;
import ru.i_novus.config.service.entity.QGroupCodeEntity;
import ru.i_novus.config.service.entity.QGroupEntity;
import ru.i_novus.config.service.repository.ConfigRepository;
import ru.i_novus.config.service.repository.GroupCodeRepository;
import ru.i_novus.config.service.repository.GroupRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Реализация REST сервиса для работы с настройками
 */
@Service
public class ConfigRestServiceImpl implements ConfigRestService {

    private ConfigValueService configValueService;

    private ConfigRepository configRepository;
    private GroupRepository groupRepository;
    private GroupCodeRepository groupCodeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public void setConfigValueService(ConfigValueService configValueService) {
        this.configValueService = configValueService;
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
    public void setGroupCodeRepository(GroupCodeRepository groupCodeRepository) {
        this.groupCodeRepository = groupCodeRepository;
    }


    @Override
    public Page<ConfigForm> getAllConfig(ConfigCriteria criteria) {
        List<ConfigEntity> configEntities = findConfigs(criteria);

        List<ConfigForm> configForms = new ArrayList<>();
        for (ConfigEntity entity : configEntities) {
            String value = configValueService.getValue(getServiceCode(entity), entity.getCode());
            /// TODO - вытащить system_name по service_code из виртуальной таблицы
            String systemName = "application";
            String groupName = groupRepository.findGroupsNameByConfigCode(entity.getCode(), new PageRequest(0, 1)).get(0);
            configForms.add(entity.toConfigForm(value, systemName, groupName));
        }

        return new PageImpl<>(configForms, criteria, criteria.getPageSize());
    }

    @Override
    public ConfigForm getConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code))
                .orElseThrow(() -> new NotFoundException("Настройки с кодом " + code + " не существует."));

        String value = configValueService.getValue(getServiceCode(configEntity), configEntity.getCode());
        /// TODO - вытащить system_name по service_code из виртуальной таблицы
        String systemName = "application";
        String groupName = groupRepository.findGroupsNameByConfigCode(configEntity.getCode(), new PageRequest(0, 1)).get(0);

        return configEntity.toConfigForm(value, systemName, groupName);
    }


    @Override
    public void saveConfig(@Valid @NotNull ConfigForm configForm) {
        if (configRepository.existsByCode(configForm.getCode())) {
            throw new BadRequestException("Настройка с кодом " + configForm.getCode() + " уже существует");
        }

        if (configForm.getServiceCode() != null) {
            /// TODO - проверяем есть ли такой serviceCode в виртуальной таблице
        }

        ConfigEntity configEntity = new ConfigEntity(configForm);
        configRepository.save(configEntity);

        configValueService.saveValue(getServiceCode(configEntity), configForm.getCode(), configForm.getValue());
    }

    @Override
    @Transactional
    public void updateConfig(String code, @Valid @NotNull ConfigForm configForm) {
        ConfigEntity configEntity = Optional.ofNullable(
                configRepository.findByCode(code))
                .orElseThrow(() -> new NotFoundException("Настройки с кодом " + code + " не существует"));

        configEntity.setServiceCode(configForm.getServiceCode());
        configEntity.setDescription(configForm.getDescription());
        configRepository.save(configEntity);

        configValueService.saveValue(getServiceCode(configEntity),
                configForm.getCode(),
                configForm.getValue());
    }

    @Override
    public void deleteConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code))
                .orElseThrow(() -> new NotFoundException("Настройки с кодом " + code + " не существует."));
        configRepository.removeByCode(code);

        configValueService.deleteValue(getServiceCode(configEntity), code);
    }

    private List<ConfigEntity> findConfigs(ConfigCriteria criteria) {
        QConfigEntity qConfigEntity = QConfigEntity.configEntity;
        QGroupEntity qGroupEntity = QGroupEntity.groupEntity;
        QGroupCodeEntity qGroupCodeEntity = QGroupCodeEntity.groupCodeEntity;

        JPAQuery<ConfigEntity> query = new JPAQuery(entityManager);
        query.distinct().from(qConfigEntity);

        List<String> groups = criteria.getGroupNames();
        if (groups != null && !groups.isEmpty()) {
            query.innerJoin(qGroupCodeEntity).on(qConfigEntity.code.startsWithIgnoreCase(qGroupCodeEntity.code))
                    .innerJoin(qGroupEntity).on(qGroupEntity.id.eq(qGroupCodeEntity.group.id))
                    .where(qGroupEntity.name.in(groups));
        }

        if (criteria.getCode() != null) {
            query.where(qConfigEntity.code.containsIgnoreCase(criteria.getCode()));
        }

        if (criteria.getName() != null) {
            query.where(qConfigEntity.name.containsIgnoreCase(criteria.getName()));
        }

        List<String> systems = criteria.getSystemNames();
        if (systems != null && !systems.isEmpty()) {
            /// TODO
        }

        /// TODO order by system_name
        return query.orderBy(qConfigEntity.name.asc()).limit(criteria.getPageSize()).offset(criteria.getOffset()).fetch();
    }

    private String getServiceCode(ConfigEntity configEntity) {
        return Objects.requireNonNullElse(configEntity.getServiceCode(), "application");
    }
}
