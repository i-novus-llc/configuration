package ru.i_novus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.i_novus.configuration.config.entity.ConfigEntity;
import ru.i_novus.configuration.config.loader.ConfigLoaderMapper;
import ru.i_novus.configuration.config.loader.ConfigServerLoader;
import ru.i_novus.configuration.config.repository.ConfigRepository;

@SpringBootApplication
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }

    @Bean
    ConfigServerLoader configServerLoader(ConfigRepository repository, ConfigLoaderMapper mapper) {
        return new ConfigServerLoader(
                repository,
                mapper,
                c -> repository.findByApplicationCode("application".equals(c) ? null : c),
                ConfigEntity::getCode
        );
    }
}
