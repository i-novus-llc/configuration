package ru.i_novus.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.consul.config.ConsulConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.i_novus.config.api.service.ConfigValueService;
import ru.i_novus.configuration.config.service.ConfigValueServiceConsulImpl;
import ru.i_novus.configuration.config.service.YamlConfigValueServiceConsulImpl;

@Configuration
public class ConfigServiceConfiguration {

    @Bean
    public ConfigValueService configValueService(@Value("${spring.cloud.consul.config.format:KEY_VALUE}")
                                                         ConsulConfigProperties.Format format) {
        final RestTemplate restTemplate = new RestTemplate();
        return format == ConsulConfigProperties.Format.YAML ?
                new YamlConfigValueServiceConsulImpl(restTemplate) :
                new ConfigValueServiceConsulImpl(restTemplate);
    }

}
