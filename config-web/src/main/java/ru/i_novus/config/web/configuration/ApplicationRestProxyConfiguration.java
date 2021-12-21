package ru.i_novus.config.web.configuration;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import org.springframework.context.annotation.Configuration;
import ru.i_novus.config.api.service.ApplicationConfigRestService;

@Configuration
@EnableJaxRsProxyClient(
        classes = ApplicationConfigRestService.class,
        address = "${config.service.url}"
)
public class ApplicationRestProxyConfiguration {
}
