package ru.i_novus.config.service.loader;

import net.n2oapp.platform.loader.server.repository.LoaderMapper;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.service.entity.ConfigEntity;
import ru.i_novus.config.service.mapper.ConfigMapper;

public class ConfigLoaderMapper implements LoaderMapper<ConfigForm, ConfigEntity> {
    @Override
    public ConfigEntity map(ConfigForm configForm, String subject) {
        ConfigEntity configEntity = ConfigMapper.toConfigEntity(configForm);
        if (!"application".equals(subject))
            configEntity.setApplicationCode(subject);
        return configEntity;
    }
}
