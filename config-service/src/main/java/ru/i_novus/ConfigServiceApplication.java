package ru.i_novus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.i_novus.config.service.entity.ConfigEntity;
import ru.i_novus.config.service.loader.ConfigLoaderMapper;
import ru.i_novus.config.service.loader.ConfigServerLoader;
import ru.i_novus.config.service.repository.ConfigRepository;

@SpringBootApplication
public class ConfigServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApplication.class, args);
    }

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
