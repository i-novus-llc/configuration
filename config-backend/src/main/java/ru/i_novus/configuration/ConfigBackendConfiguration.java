package ru.i_novus.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.loader.ConfigLoaderMapper;
import ru.i_novus.configuration.config.loader.ConfigServerLoader;
import ru.i_novus.configuration.config.repository.ConfigRepository;

@Configuration
@EnableJpaRepositories("ru.i_novus.configuration")
public class ConfigBackendConfiguration {

    @Bean
    ConfigServerLoader configServerLoader(ConfigRepository repository) {
        return new ConfigServerLoader(
                repository,
                new ConfigLoaderMapper(),
                c -> repository.findByApplicationCode("application".equals(c) ? null : c),
                ConfigEntity::getCode
        );
    }
}
