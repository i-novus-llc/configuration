package ru.i_novus.configuration.config.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import ru.i_novus.configuration.config.entity.ApplicationEntity;
import ru.i_novus.config.api.model.ApplicationResponse;

/**
 * Загрузчик приложений
 */
@Component
public class ApplicationServerLoader extends RepositoryServerLoader<ApplicationResponse, ApplicationEntity, String> {

    public ApplicationServerLoader(CrudRepository<ApplicationEntity, String> repository) {
        super(repository, (model, subject) -> {
            if (model == null) return null;
            ApplicationEntity entity = new ApplicationEntity();
            entity.setCode(model.getCode());
            entity.setName(model.getName());
            return entity;
        });
    }

    @Override
    public String getTarget() {
        return "applications";
    }

    @Override
    public Class<ApplicationResponse> getDataType() {
        return ApplicationResponse.class;
    }
}
