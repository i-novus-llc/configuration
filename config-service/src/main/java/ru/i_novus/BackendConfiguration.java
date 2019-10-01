package ru.i_novus;

import net.n2oapp.platform.jaxrs.autoconfigure.EnableJaxRsProxyClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.i_novus.ms.audit.client.AuditClient;
import ru.i_novus.ms.audit.client.impl.SimpleAuditClientImpl;
import ru.i_novus.ms.audit.client.impl.converter.RequestConverter;
import ru.i_novus.ms.audit.client.model.User;
import ru.i_novus.ms.audit.service.api.AuditRest;

import java.util.UUID;

@Configuration
public class BackendConfiguration {

    private static final String UNKNOWN = "UNKNOWN";


    @Bean
    public RequestConverter requestConverter() {
        return new RequestConverter(
                () -> {
                    String username = getUsername();
                    if (username.equals(UNKNOWN)) return new User(UNKNOWN, UNKNOWN);
                    return new User(UUID.randomUUID().toString(), username);
                },
                () -> "Access",
                () -> "SOURCE_WORKST                                                                                   ATION"
        );
    }

    /**
     * Получение имени текущего пользователя по контексту {@link SecurityContextHolder}
     *
     * @return пустую строку или имя пользователя, если он не анонимный
     */
    public static String getUsername() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null)
            return UNKNOWN;
        Authentication authentication = context.getAuthentication();
        if (authentication == null)
            return UNKNOWN;
        if (authentication instanceof AnonymousAuthenticationToken)
            return UNKNOWN;

        Object principal = authentication.getPrincipal();
        String username = UNKNOWN;
        if (principal instanceof String) {
            username = (String) principal;
        }
        return username;
    }


    @Configuration
    @EnableJaxRsProxyClient(
            classes = {AuditRest.class},
            address = "${audit.url}")
    static class AuditClientConfiguration {
        @Bean
        public AuditClient simpleAuditClient(@Qualifier("auditRestJaxRsProxyClient") AuditRest auditRest) {
            SimpleAuditClientImpl simpleAuditClient = new SimpleAuditClientImpl();
            simpleAuditClient.setAuditRest(auditRest);
            return simpleAuditClient;
        }
    }
}
