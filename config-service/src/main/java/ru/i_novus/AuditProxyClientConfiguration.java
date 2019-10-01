package ru.i_novus;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import org.springframework.context.annotation.Configuration;
import ru.i_novus.ms.audit.service.api.AuditRest;


@Configuration
@EnableJaxRsProxyClient(
        classes = {AuditRest.class},
        address = "${audit.backend.url}")
public class AuditProxyClientConfiguration {

}

