package ru.i_novus.configuration.config.loader;

import net.n2oapp.platform.loader.server.repository.LoaderMapper;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.mapper.ConfigMapper;

public class ConfigLoaderMapper implements LoaderMapper<ConfigForm, ConfigEntity> {
    @Override
    public ConfigEntity map(ConfigForm configForm, String subject) {
        ConfigEntity configEntity = ConfigMapper.toConfigEntity(configForm, null);
        if (!"application".equals(subject))
            configEntity.setApplicationCode(subject);
        return configEntity;
    }
}
