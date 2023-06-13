package ru.i_novus.configuration;

import brave.propagation.B3Propagation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.consul.config.ConsulConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.configuration.config.service.ConfigValueServiceConsulImpl;
import ru.i_novus.configuration.config.service.FileConfigValueServiceConsulImpl;
import ru.i_novus.configuration.config.service.YamlConfigValueServiceConsulImpl;

@Configuration
public class ConfigServiceConfiguration {

    @Bean
    public ConfigValueService configValueService(@Value("${spring.cloud.consul.config.format}")
                                                         ConsulConfigProperties.Format format) {
        final RestTemplate restTemplate = new RestTemplate();
        switch (format) {
            case YAML:
                return new YamlConfigValueServiceConsulImpl(restTemplate);
            case FILES:
                return new FileConfigValueServiceConsulImpl(restTemplate);
            default:
                return new ConfigValueServiceConsulImpl(restTemplate);
        }
    }
}
