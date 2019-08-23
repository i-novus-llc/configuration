package ru.i_novus.config.service.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import net.n2oapp.platform.i18n.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.service.ConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.service.entity.*;
import ru.i_novus.config.service.repository.ConfigRepository;
import ru.i_novus.config.service.repository.GroupRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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


    @Override
    public Page<ConfigForm> getAllConfig(ConfigCriteria criteria) {
        /// TODO - отсортировать по system
        criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "code"));

        Page<ConfigEntity> configEntities = configRepository.findAll(toPredicate(criteria), criteria);

        /// TODO - вытащить system_name по service_code из виртуальной таблицы
        return configEntities.map(e -> e.toConfigForm(
                configValueService.getValue(getServiceCode(e), e.getCode()),
                "application",
                groupRepository.findOneGroupByConfigCodeStarts(e.getCode()).toGroupForm()
                )
        );
    }

    @Override
    public ConfigForm getConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();

        String value = configValueService.getValue(getServiceCode(configEntity), configEntity.getCode());
        /// TODO - вытащить system_name по service_code из виртуальной таблицы
        String systemName = "application";
        GroupEntity groupEntity = groupRepository.findOneGroupByConfigCodeStarts(configEntity.getCode());

        return configEntity.toConfigForm(value, systemName, groupEntity.toGroupForm());
    }

    @Override
    public void saveConfig(@Valid @NotNull ConfigForm configForm) {
        if (configRepository.existsByCode(configForm.getCode())) {
            throw new UserException("config.code.not.unique");
        }

        if (configForm.getApplicationCode() != null) {
            /// TODO - проверяем есть ли такой serviceCode в виртуальной таблице
        }

        ConfigEntity configEntity = new ConfigEntity(configForm);
        configRepository.save(configEntity);

        configValueService.saveValue(getServiceCode(configEntity), configForm.getCode(), configForm.getValue());
    }

    @Override
    @Transactional
    public void updateConfig(String code, @Valid @NotNull ConfigForm configForm) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();

        configEntity.setApplicationCode(configForm.getApplicationCode());
        configEntity.setDescription(configForm.getDescription());
        configRepository.save(configEntity);

        configValueService.saveValue(getServiceCode(configEntity),
                configForm.getCode(),
                configForm.getValue());
    }

    @Override
    public void deleteConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();
        configRepository.deleteByCode(code);

        configValueService.deleteValue(getServiceCode(configEntity), code);
    }

    private Predicate toPredicate(ConfigCriteria criteria) {
        QConfigEntity qConfigEntity = QConfigEntity.configEntity;
        QGroupEntity qGroupEntity = QGroupEntity.groupEntity;
        QGroupCodeEntity qGroupCodeEntity = QGroupCodeEntity.groupCodeEntity;

        BooleanBuilder builder = new BooleanBuilder();

        List<Integer> groupIds = criteria.getGroupIds();
        if (groupIds != null && !groupIds.isEmpty()) {
            BooleanExpression exists = JPAExpressions.selectOne().from(qGroupCodeEntity).from(qGroupEntity)
                    .where(new BooleanBuilder()
                            .and(qGroupCodeEntity.group.id.eq(qGroupEntity.id))
                            .and(qGroupEntity.id.in(criteria.getGroupIds()))
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

        List<String> systems = criteria.getSystemNames();
        if (systems != null && !systems.isEmpty()) {
            /// TODO
        }

        return builder.getValue();
    }

    private String getServiceCode(ConfigEntity configEntity) {
        return Objects.requireNonNullElse(configEntity.getApplicationCode(), "application");
    }
}
