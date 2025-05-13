package ru.i_novus.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.consul.config.ConsulConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.configuration.config.service.ConfigValueServiceConsulImpl;
import ru.i_novus.configuration.config.service.FileConfigValueServiceConsulImpl;
import ru.i_novus.configuration.config.service.YamlConfigValueServiceConsulImpl;
import ru.i_novus.configuration.config.validators.value.ConfigValueValidator;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Configuration
public class ConfigServiceConfiguration {

    @Bean
    public ConfigValueService configValueService(@Value("${spring.cloud.consul.config.format}")
                                                 ConsulConfigProperties.Format format) {
        final RestTemplate restTemplate = new RestTemplate();
        return switch (format) {
            case YAML -> new YamlConfigValueServiceConsulImpl(restTemplate);
            case FILES -> new FileConfigValueServiceConsulImpl(restTemplate);
            default -> new ConfigValueServiceConsulImpl(restTemplate);
        };
    }

    @Bean
    public Map<ValueTypeEnum, ConfigValueValidator> configValueValidators(@Value("${config.value.validate.enabled:false}") boolean enabled,
                                                                          @Autowired(required = false) Collection<ConfigValueValidator> validators) {
        if (enabled) {
            return Optional.ofNullable(validators)
                    .orElse(Collections.emptyList())
                    .stream()
                    .collect(Collectors.toMap(ConfigValueValidator::getType, Function.identity()));
        }
        return Collections.emptyMap();
    }

}
