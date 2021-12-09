package ru.i_novus.configuration.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.i_novus.config.api.util.AuditService;
import ru.i_novus.ms.audit.client.AuditClient;
import ru.i_novus.ms.audit.client.model.AuditClientRequest;

public class AuditServiceImpl implements AuditService {

    @Autowired
    private AuditClient auditClient;

    private static ObjectMapper objectMapper = new ObjectMapper();


    public void audit(String action, Object object, String objectId, String objectName) {
        AuditClientRequest request = new AuditClientRequest();
        request.setEventType(action);
        request.setObjectType(object.getClass().getSimpleName());
        request.setObjectId(objectId);
        try {
            request.setContext(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            request.setContext(object.toString());
        }
        request.setObjectName(objectName);
        request.setAuditType((short) 1);
        request.setUsername(getUsername());
        request.setSourceApplication("config-service");

        auditClient.add(request);
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
}
