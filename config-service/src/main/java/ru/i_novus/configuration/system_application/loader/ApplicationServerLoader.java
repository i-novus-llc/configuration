package ru.i_novus.configuration.system_application.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import ru.i_novus.configuration.system_application.entity.ApplicationEntity;
import ru.i_novus.configuration.system_application.entity.SystemEntity;
import ru.i_novus.system_application.api.model.SimpleApplicationResponse;

/**
 * Загрузчик приложений
 */
@Component
public class ApplicationServerLoader extends RepositoryServerLoader<SimpleApplicationResponse, ApplicationEntity, String> {

    public ApplicationServerLoader(CrudRepository<ApplicationEntity, String> repository) {
        super(repository, (model, subject) -> {
            if (model == null) return null;
            ApplicationEntity entity = new ApplicationEntity();
            entity.setCode(model.getCode());
            entity.setName(model.getName());
            entity.setSystem(new SystemEntity(model.getSystemCode()));
            return entity;
        });
    }

    @Override
    public String getTarget() {
        return "applications";
    }

    @Override
    public Class<SimpleApplicationResponse> getDataType() {
        return SimpleApplicationResponse.class;
    }
}
