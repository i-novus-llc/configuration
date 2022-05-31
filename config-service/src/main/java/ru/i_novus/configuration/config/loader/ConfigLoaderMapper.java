package ru.i_novus.configuration.config.loader;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.platform.loader.server.repository.LoaderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.configuration.config.entity.ApplicationEntity;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.entity.GroupEntity;
import ru.i_novus.configuration.config.mapper.ConfigMapper;
import ru.i_novus.configuration.config.repository.ApplicationRepository;
import ru.i_novus.configuration.config.repository.GroupRepository;

import java.util.Optional;

@Component
public class ConfigLoaderMapper implements LoaderMapper<ConfigForm, ConfigEntity> {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private MessageSourceAccessor messageAccessor;

    @Override
    public ConfigEntity map(ConfigForm configForm, String subject) {
        ConfigEntity configEntity = ConfigMapper.toConfigEntity(configForm);
        if (!"application".equals(subject)) {
            ApplicationEntity application = applicationRepository.findByCode(subject);
            configEntity.setApplication(application);
        }
        Optional<GroupEntity> group = Optional.ofNullable(groupRepository.findOneGroupByConfigCodeStarts(configEntity.getCode()));
        configEntity.setGroup(group.orElseThrow(() -> new UserException(messageAccessor
                .getMessage("config.group.not.found", new Object[]{configForm.getCode()}))));
        return configEntity;
    }
}
