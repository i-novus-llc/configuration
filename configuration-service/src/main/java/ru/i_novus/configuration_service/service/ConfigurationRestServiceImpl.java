package ru.i_novus.configuration_service.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import ru.i_novus.configuration_api.criteria.FindConfigurationCriteria;
import ru.i_novus.configuration_api.items.ConfigurationResponseItem;
import ru.i_novus.configuration_api.service.ConfigurationRestService;
import ru.i_novus.configuration_api.service.ConfigurationValueService;
import ru.i_novus.configuration_service.entity.MetadataEntity;
import ru.i_novus.configuration_service.entity.QGroupCodeEntity;
import ru.i_novus.configuration_service.entity.QGroupEntity;
import ru.i_novus.configuration_service.entity.QMetadataEntity;
import ru.i_novus.configuration_service.repository.GroupCodeRepository;
import ru.i_novus.configuration_service.repository.GroupRepository;
import ru.i_novus.configuration_service.repository.MetadataRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.*;

/**
 * Реализация REST сервиса для работы с настройками
 */
@Controller
public class ConfigurationRestServiceImpl implements ConfigurationRestService {

    private ConfigurationValueService configurationValueService;

    private MetadataRepository metadataRepository;
    private GroupRepository groupRepository;
    private GroupCodeRepository groupCodeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public void setConfigurationValueService(ConfigurationValueService configurationValueService) {
        this.configurationValueService = configurationValueService;
    }

    @Autowired
    public void setMetadataRepository(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
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
    public Page<ConfigurationResponseItem> getAllConfigurations(FindConfigurationCriteria criteria) {
        Predicate predicate = buildFindConfigurationPredicate(criteria);
        Iterable<MetadataEntity> metadataEntities = metadataRepository.findAll(predicate);

        List<ConfigurationResponseItem> configurationResponseItems = new ArrayList<>();
        for (MetadataEntity entity : metadataEntities) {
            String value = configurationValueService.getValue(getServiceCode(entity), entity.getCode());
            /// TODO - вытащить system_name по service_code из виртуальной таблицы
            String systemName = "application";
            String groupName = groupRepository.findGroupsNameByConfigurationCode(entity.getCode(), new PageRequest(0, 1)).get(0);
            configurationResponseItems.add(entity.toItem(value, systemName, groupName));
        }

        Collections.sort(configurationResponseItems, Comparator.comparing(ConfigurationResponseItem::getSystemName));
        return new PageImpl<>(configurationResponseItems, criteria, criteria.getPageSize());
    }

    @Override
    public ConfigurationResponseItem getConfiguration(String code) {
        MetadataEntity metadataEntity = Optional.ofNullable(metadataRepository.findByCode(code))
                .orElseThrow(() -> new NotFoundException("Настройки с кодом " + code + " не существует."));

        String value = configurationValueService.getValue(getServiceCode(metadataEntity), metadataEntity.getCode());
        /// TODO - вытащить system_name по service_code из виртуальной таблицы
        String systemName = "application";
        String groupName = groupRepository.findGroupsNameByConfigurationCode(metadataEntity.getCode(), new PageRequest(0, 1)).get(0);

        return metadataEntity.toItem(value, systemName, groupName);
    }


    @Override
    public void saveConfiguration(@Valid @NotNull ConfigurationResponseItem configurationResponseItem) {
        if (configurationResponseItem.getServiceCode() != null) {
            /// TODO - проверяем есть ли такой serviceCode в виртуальной таблице
        }

        MetadataEntity metadataEntity = new MetadataEntity(configurationResponseItem);
        try {
            metadataRepository.save(metadataEntity);
        } catch (Exception e) {
            throw new BadRequestException("Настройка с кодом " + configurationResponseItem.getCode() + " уже существует", e);
        }

        configurationValueService.saveValue(getServiceCode(metadataEntity), configurationResponseItem.getCode(), configurationResponseItem.getValue());
    }

    @Override
    public void updateConfiguration(String code, @Valid @NotNull ConfigurationResponseItem configurationResponseItem) {
        MetadataEntity metadataEntity = Optional.ofNullable(
                metadataRepository.findByCode(code))
                .orElseThrow(() -> new NotFoundException("Настройки с кодом " + code + " не существует"));

        metadataEntity.setServiceCode(configurationResponseItem.getServiceCode());
        metadataEntity.setDescription(configurationResponseItem.getDescription());
        metadataRepository.save(metadataEntity);

        configurationValueService.saveValue(getServiceCode(metadataEntity),
                configurationResponseItem.getCode(),
                configurationResponseItem.getValue());
    }

    @Override
    public void deleteConfiguration(String code) {
        MetadataEntity metadataEntity = Optional.ofNullable(metadataRepository.findByCode(code))
                .orElseThrow(() -> new NotFoundException("Настройки с кодом " + code + " не существует."));
        metadataRepository.removeByCode(code);

        configurationValueService.deleteValue(getServiceCode(metadataEntity), code);
    }

    private Predicate buildFindConfigurationPredicate(FindConfigurationCriteria criteria) {
        QMetadataEntity qMetadataEntity = QMetadataEntity.metadataEntity;
        QGroupEntity qGroupEntity = QGroupEntity.groupEntity;
        QGroupCodeEntity qGroupCodeEntity = QGroupCodeEntity.groupCodeEntity;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        JPAQuery query = new JPAQuery(entityManager);
        query.from(qMetadataEntity).join(qGroupEntity).leftJoin(qGroupCodeEntity)
                .on(qGroupEntity.id.eq(qGroupCodeEntity.groupId));

        if (criteria.getCode() != null) {
            booleanBuilder.and(qMetadataEntity.code.containsIgnoreCase(criteria.getCode()));
        }

        if (criteria.getName() != null) {
            booleanBuilder.and(qMetadataEntity.name.containsIgnoreCase(criteria.getName()));
        }

        List<String> groups = criteria.getGroupNames();
        if (groups != null && !groups.isEmpty()) {

        }

        List<String> systems = criteria.getSystemNames();
        if (systems != null && !systems.isEmpty()) {
            /// TODO
        }

        return booleanBuilder.getValue();
    }

    private String getServiceCode(MetadataEntity metadataEntity) {
        return Objects.requireNonNullElse(metadataEntity.getServiceCode(), "application");
    }
}
