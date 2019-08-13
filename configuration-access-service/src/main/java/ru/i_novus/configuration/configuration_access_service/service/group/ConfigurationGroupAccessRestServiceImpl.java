package ru.i_novus.configuration.configuration_access_service.service.group;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.configuration.configuration_access_service.criteria.FindConfigurationGroupCriteria;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationGroupCodeEntity;
import ru.i_novus.configuration.configuration_access_service.entity.ConfigurationGroupEntity;
import ru.i_novus.configuration.configuration_access_service.entity.QConfigurationGroupCodeEntity;
import ru.i_novus.configuration.configuration_access_service.entity.QConfigurationGroupEntity;
import ru.i_novus.configuration.configuration_access_service.items.ConfigurationGroupResponseItem;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationGroupCodeRepository;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationGroupRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Реализация REST сервиса для работы с группами настроек
 */
@Controller
public class ConfigurationGroupAccessRestServiceImpl implements ConfigurationGroupAccessRestService {

    private ConfigurationGroupRepository configurationGroupRepository;
    private ConfigurationGroupCodeRepository configurationGroupCodeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public void setConfigurationGroupRepository(ConfigurationGroupRepository configurationGroupRepository) {
        this.configurationGroupRepository = configurationGroupRepository;
    }

    @Autowired
    public void setConfigurationGroupCodeRepository(ConfigurationGroupCodeRepository configurationGroupCodeRepository) {
        this.configurationGroupCodeRepository = configurationGroupCodeRepository;
    }


    @Override
    public ConfigurationGroupResponseItem getConfigurationGroup(Integer groupId) {
        ConfigurationGroupEntity configurationGroupEntity = configurationGroupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Группы с идентификатором " + groupId + " не существует"));

        List<String> codes = configurationGroupCodeRepository.findAllCodeByGroupId(groupId);
        return new ConfigurationGroupResponseItem(configurationGroupEntity, codes);
    }

    @Override
    public Page<ConfigurationGroupResponseItem> getAllConfigurationsGroup(FindConfigurationGroupCriteria criteria) {
        return new PageImpl<>(findConfigurationGroups(criteria), criteria, criteria.getPageSize());
    }

    ///TODO - сохранение новой группы с уже существующими кодами работает (не должно)
    @Override
    @Transactional
    public Integer saveConfigurationGroup(@Valid @NotNull ConfigurationGroupResponseItem configurationGroupResponseItem) {
        ConfigurationGroupEntity configurationGroupEntity = new ConfigurationGroupEntity(configurationGroupResponseItem);

        final ConfigurationGroupEntity savedConfigurationGroupEntity;
        try {
            savedConfigurationGroupEntity = configurationGroupRepository.save(configurationGroupEntity);
        } catch (Exception e) {
            throw new BadRequestException("Группа настроек с именем " + configurationGroupResponseItem.getName() + " уже существует");
        }

        configurationGroupCodeRepository.saveAll(getConfigurationGroupCodeEntities(
                configurationGroupResponseItem.getCodes(), savedConfigurationGroupEntity.getId()));
        return savedConfigurationGroupEntity.getId();
    }

    ///TODO - сохранение с not unique именем -> 500
    @Override
    @Transactional
    public void updateConfigurationGroup(Integer groupId, @Valid @NotNull ConfigurationGroupResponseItem configurationGroupResponseItem) {
        ConfigurationGroupEntity configurationGroupEntity = configurationGroupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Группы с идентификатором " + groupId + " не существует"));

        configurationGroupEntity.setName(configurationGroupResponseItem.getName());
        configurationGroupEntity.setDescription(configurationGroupResponseItem.getDescription());

        configurationGroupRepository.save(configurationGroupEntity);

        List<String> codes = configurationGroupCodeRepository.findAllCodeByGroupId(groupId);
        if (!configurationGroupResponseItem.getCodes().equals(codes)) {
            configurationGroupCodeRepository.deleteByGroupId(groupId);
            configurationGroupCodeRepository.saveAll(getConfigurationGroupCodeEntities(configurationGroupResponseItem.getCodes(), groupId));
        }
    }

    @Override
    public void deleteConfigurationGroup(Integer groupId) {
        if (configurationGroupRepository.removeById(groupId) == 0) {
            throw new NotFoundException("Группы с идентификатором " + groupId + " не существует.");
        }
    }

    private List<ConfigurationGroupResponseItem> findConfigurationGroups(FindConfigurationGroupCriteria criteria) {
        QConfigurationGroupEntity qConfigurationGroupEntity = QConfigurationGroupEntity.configurationGroupEntity;
        QConfigurationGroupCodeEntity qConfigurationGroupCodeEntity = QConfigurationGroupCodeEntity.configurationGroupCodeEntity;

        JPAQuery<ConfigurationGroupResponseItem> query = new JPAQuery(entityManager);
        query.select(qConfigurationGroupEntity).from(qConfigurationGroupEntity).leftJoin(qConfigurationGroupCodeEntity)
                .on(qConfigurationGroupEntity.id.eq(qConfigurationGroupCodeEntity.groupId));

        if (criteria.getCode() != null) {
            query.where(qConfigurationGroupCodeEntity.code.containsIgnoreCase(criteria.getCode()));
        }

        if (criteria.getName() != null) {
            query.where(qConfigurationGroupEntity.name.containsIgnoreCase(criteria.getName()));
        }

        /// TODO - есть вероятность, что из-за лимита\офсетта не все коды будут включены в запись группы
        /// TODO - пока так, но вероятно нужно использовать group_concat без использования внешнего преобразования
        Map<List<?>, List<String>> results =
                query.limit(criteria.getPageSize()).offset(criteria.getOffset())
                        .transform(GroupBy.groupBy(qConfigurationGroupEntity.name, qConfigurationGroupEntity.description)
                                .as(GroupBy.list(qConfigurationGroupCodeEntity.code))
                        );

        List<ConfigurationGroupResponseItem> configurationGroupResponseItems = new ArrayList<>();
        for (Map.Entry<List<?>, List<String>> entry : results.entrySet()) {
            String name = (String) entry.getKey().get(0);
            String description = entry.getKey().size() > 1 ? (String) entry.getKey().get(1) : null;
            configurationGroupResponseItems.add(new ConfigurationGroupResponseItem(name, description, entry.getValue()));
        }

        return configurationGroupResponseItems;
    }

    private List<ConfigurationGroupCodeEntity> getConfigurationGroupCodeEntities(List<String> codes, Integer groupId) {
        return codes.stream()
                .map(i -> new ConfigurationGroupCodeEntity(i, groupId))
                .collect(toList());
    }
}
