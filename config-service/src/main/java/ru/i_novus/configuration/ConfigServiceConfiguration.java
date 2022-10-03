package ru.i_novus.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.consul.config.ConsulConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.configuration.config.service.ConfigValueServiceConsulImpl;
import ru.i_novus.configuration.config.service.YamlConfigValueServiceConsulImpl;

@Configuration
@EnableConfigurationProperties({ConsulConfigProperties.class})
public class ConfigServiceConfiguration {

    @Bean
    public ConfigValueService configValueService(ConsulConfigProperties properties) {
        final RestTemplate restTemplate = new RestTemplate();
        return ConsulConfigProperties.Format.YAML.equals(properties.getFormat()) ?
                new YamlConfigValueServiceConsulImpl(restTemplate) :
                new ConfigValueServiceConsulImpl(restTemplate);
    }

}
