package ru.i_novus.config.service.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import net.n2oapp.platform.i18n.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.config.api.criteria.ConfigCriteria;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.service.ConfigRestService;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.config.service.entity.*;
import ru.i_novus.config.service.model.Application;
import ru.i_novus.config.service.repository.ConfigRepository;
import ru.i_novus.config.service.repository.GroupRepository;

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

    private ConfigRepository configRepository;
    private GroupRepository groupRepository;

    private RestTemplate restTemplate;

    @Value("${security.admin.url}")
    private String url;


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
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public Page<ConfigForm> getAllConfig(ConfigCriteria criteria) {
        /// TODO - отсортировать по system
        criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "code"));

        Page<ConfigEntity> configEntities = configRepository.findAll(toPredicate(criteria), criteria);

        return configEntities.map(e -> {
                    Application application = getApplication(e.getApplicationCode());
                    return e.toConfigForm(
                            configValueService.getValue(getAppName(e, application), e.getCode()),
                            getSystemName(application),
                            groupRepository.findOneGroupByConfigCodeStarts(e.getCode()).toGroupForm()
                    );
                }
        );
    }

//    @Override
//    public Map<GroupForm, List<ConfigForm>> getAllConfigByAppCode(String appCode) {
//        List<Object[]> objectList = configRepository.findByAppCode(appCode);
//
//        Map<GroupForm, List<ConfigForm>> result = new LinkedHashMap<>();
//
//        for (Object[] obj : objectList) {
//            GroupForm groupForm = ((GroupEntity) obj[0]).toGroupForm();
//            ConfigEntity configEntity = ((ConfigEntity) obj[1]);
//            Application application = getApplication(configEntity.getApplicationCode());
//            ConfigForm configForm = configEntity.toConfigForm(
//                    configValueService.getValue(getAppName(configEntity, application), configEntity.getCode()),
//                    getSystemName(application),
//                    groupForm
//            );
//            if (!result.containsKey(groupForm)) {
//                result.put(groupForm, new ArrayList<>(List.of(configForm)));
//            } else {
//                result.get(groupForm).add(configForm);
//            }
//        }
//
//        return result;
//    }

    @Override
    public ConfigForm getConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();

        Application application = getApplication(configEntity.getApplicationCode());

        String value = configValueService.getValue(getAppName(configEntity, application), configEntity.getCode());
        GroupEntity groupEntity = groupRepository.findOneGroupByConfigCodeStarts(configEntity.getCode());

        return configEntity.toConfigForm(value, getSystemName(application), groupEntity.toGroupForm());
    }

    @Override
    public void saveConfig(@Valid @NotNull ConfigForm configForm) {
        if (configRepository.existsByCode(configForm.getCode())) {
            throw new UserException("config.code.not.unique");
        }

        ConfigEntity configEntity = new ConfigEntity(configForm);
        configRepository.save(configEntity);

        if (configForm.getValue() != null) {
            Application application = getApplication(configEntity.getApplicationCode());
            configValueService.saveValue(getAppName(configEntity, application), configForm.getCode(), configForm.getValue());
        }
    }

    @Override
    @Transactional
    public void updateConfig(String code, @Valid @NotNull ConfigForm configForm) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();

        configEntity.setApplicationCode(configForm.getApplicationCode());
        configEntity.setName(configForm.getName());
        configEntity.setValueType(configForm.getValueType());
        configEntity.setDescription(configForm.getDescription());
        configRepository.save(configEntity);

        if (configForm.getValue() != null) {
            Application application = getApplication(configEntity.getApplicationCode());
            configValueService.saveValue(getAppName(configEntity, application), configForm.getCode(), configForm.getValue());
        }
    }

    @Override
    public void deleteConfig(String code) {
        ConfigEntity configEntity = Optional.ofNullable(configRepository.findByCode(code)).orElseThrow();
        Application application = getApplication(configEntity.getApplicationCode());

        configRepository.deleteByCode(code);
//        configValueService.deleteValue(getAppName(configEntity, application), code);
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

    private String getAppName(ConfigEntity configEntity, Application application) {
        return configEntity.getApplicationCode() != null ? application.getName() : "application";
    }

    private String getSystemName(Application application) {
        return application != null ? application.getSystem().getName() : "Общесистемные";
    }

    private Application getApplication(String code) {
        return code != null ? restTemplate.getForObject(url + "/applications/" + code, Application.class) : null;
    }
}
