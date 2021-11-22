package ru.i_novus.configuration.config.loader;

import net.n2oapp.platform.loader.server.repository.EntityIdentifier;
import net.n2oapp.platform.loader.server.repository.LoaderMapper;
import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.platform.loader.server.repository.SubjectFilter;
import org.springframework.data.repository.CrudRepository;
import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.configuration.config.entity.ConfigEntity;


public class ConfigServerLoader extends RepositoryServerLoader<ConfigForm, ConfigEntity, String> {
    public ConfigServerLoader(CrudRepository<ConfigEntity, String> repository,
                              LoaderMapper<ConfigForm, ConfigEntity> mapper,
                              SubjectFilter<ConfigEntity> filter,
                              EntityIdentifier<ConfigEntity, String> identifier) {
        super(repository, mapper, filter, identifier);
    }

    @Override
    public String getTarget() {
        return "configs";
    }

    @Override
    public Class<ConfigForm> getDataType() {
        return ConfigForm.class;
    }
}
