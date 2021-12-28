package ru.i_novus.configuration.config.loader;

import net.n2oapp.platform.loader.server.repository.LoaderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.entity.GroupEntity;
import ru.i_novus.configuration.config.mapper.ConfigMapper;
import ru.i_novus.configuration.config.repository.GroupRepository;

import java.util.Optional;

@Component
public class ConfigLoaderMapper implements LoaderMapper<ConfigForm, ConfigEntity> {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public ConfigEntity map(ConfigForm configForm, String subject) {
        ConfigEntity configEntity = ConfigMapper.toConfigEntity(configForm);
        if (!"application".equals(subject))
            configEntity.setApplicationCode(subject);
        Optional<GroupEntity> group = Optional.ofNullable(groupRepository.findOneGroupByConfigCodeStarts(configEntity.getCode()));
        configEntity.setGroupId(group.map(GroupEntity::getId).orElse(null));

        return configEntity;
    }
}
