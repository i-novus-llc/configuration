package ru.i_novus.configuration.configuration_access_service.service.group;

import net.n2oapp.platform.jaxrs.RestPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.configuration.configuration_access_service.criteria.FindConfigurationGroupsCriteria;
import ru.i_novus.configuration.configuration_access_service.entity.group.ConfigurationGroupEntity;
import ru.i_novus.configuration.configuration_access_service.entity.group.ConfigurationGroupResponseItem;
import ru.i_novus.configuration.configuration_access_service.entity.group_code.ConfigurationGroupCodeEntity;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationGroupCodeRepository;
import ru.i_novus.configuration.configuration_access_service.repository.ConfigurationGroupRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Реализация REST сервиса для работы с группами настроек
 */
@Controller
public class GroupAccessRestServiceImpl implements GroupAccessRestService {

    private ConfigurationGroupRepository configurationGroupRepository;
    private ConfigurationGroupCodeRepository configurationGroupCodeRepository;

    @Autowired
    public void setConfigurationGroupRepository(ConfigurationGroupRepository configurationGroupRepository) {
        this.configurationGroupRepository = configurationGroupRepository;
    }

    @Autowired
    public void setConfigurationGroupCodeRepository(ConfigurationGroupCodeRepository configurationGroupCodeRepository) {
        this.configurationGroupCodeRepository = configurationGroupCodeRepository;
    }


    @Override
    public Page<ConfigurationGroupEntity> getConfigurationsGroup(FindConfigurationGroupsCriteria criteria) {
        Pageable pageable = PageRequest.of(criteria.getPageNumber(), criteria.getPageSize());
        Page<ConfigurationGroupEntity> configurationGroupEntities = (criteria.getGroupName() != null) ?
                configurationGroupRepository.findByNameStartingWith(criteria.getGroupName(), pageable) :
                configurationGroupRepository.findAll(pageable);

        return new RestPage<>(configurationGroupEntities.getContent());
    }

    ///TODO - сохранение с повторяющимися кодами работает (не должно)
    @Override
    @Transactional
    public Integer saveConfigurationGroup(@Valid @NotNull ConfigurationGroupResponseItem configurationGroupResponseItem) {
        ConfigurationGroupEntity configurationGroupEntity =
                new ConfigurationGroupEntity(configurationGroupResponseItem.getName(), configurationGroupResponseItem.getDescription());

        final ConfigurationGroupEntity savedConfigurationGroupEntity;
        try {
            savedConfigurationGroupEntity = configurationGroupRepository.save(configurationGroupEntity);
        } catch (Exception e) {
            throw new BadRequestException("Группа настроек с именем " + configurationGroupResponseItem.getName() + " уже существует");
        }

        configurationGroupCodeRepository.saveAll(getConfigurationGroupCodeEntities(configurationGroupResponseItem.getCodes(), savedConfigurationGroupEntity.getId()));
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

    private List<ConfigurationGroupCodeEntity> getConfigurationGroupCodeEntities(List<String> codes, Integer groupId) {
        return codes.stream()
                .map(i -> new ConfigurationGroupCodeEntity(i, groupId))
                .collect(toList());
    }
}
