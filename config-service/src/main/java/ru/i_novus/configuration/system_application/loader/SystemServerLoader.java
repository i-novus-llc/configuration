package ru.i_novus.configuration.system_application.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import ru.i_novus.configuration.system_application.entity.SystemEntity;
import ru.i_novus.system_application.api.model.SimpleSystemResponse;

/**
 * Загрузчик систем
 */
@Component
public class SystemServerLoader extends RepositoryServerLoader<SimpleSystemResponse, SystemEntity, String> {

    public SystemServerLoader(CrudRepository<SystemEntity, String> repository) {
        super(repository, (model, subject) -> {
            if (model == null) return null;
            SystemEntity entity = new SystemEntity();
            entity.setCode(model.getCode());
            entity.setName(model.getName());
            entity.setDescription(model.getDescription());
            return entity;
        });
    }

    @Override
    public String getTarget() {
        return "systems";
    }

    @Override
    public Class<SimpleSystemResponse> getDataType() {
        return SimpleSystemResponse.class;
    }
}
