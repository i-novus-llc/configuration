package ru.i_novus.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.configuration.config.service.ConfigValueServiceConsulImpl;
import ru.i_novus.configuration.config.service.YamlConfigValueServiceConsulImpl;

@Configuration
public class ConfigServiceConfiguration {

    @Bean
    public ConfigValueService configValueService(@Value("${config.consul.yaml.enabled:false}") boolean yamlEnabled) {
        final RestTemplate restTemplate = new RestTemplate();
        return yamlEnabled ?
                new YamlConfigValueServiceConsulImpl(restTemplate) :
                new ConfigValueServiceConsulImpl(restTemplate);
    }

}
