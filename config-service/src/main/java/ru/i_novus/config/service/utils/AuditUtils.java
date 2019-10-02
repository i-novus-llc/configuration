package ru.i_novus.config.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.i_novus.ms.audit.client.model.AuditClientRequest;

import java.time.LocalDateTime;

public class AuditUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static AuditClientRequest getAuditClientRequest() {
        AuditClientRequest request = new AuditClientRequest();
        request.setEventDate(LocalDateTime.now());
        request.setUsername(getUsername());
        request.setSourceApplication("config-service");
        return request;
    }

    private static String getUsername() {
        String defaultName = "UNKNOWN";
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null)
            return defaultName;
        Authentication authentication = context.getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            return defaultName;

        Object principal = authentication.getPrincipal();
        if (principal instanceof String)
            return (String) principal;
        return defaultName;
    }

    public static String getContext(Object obj) {
        String context;
        try {
            context = objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            context = e.getMessage();
        }
        return context;
    }
}
