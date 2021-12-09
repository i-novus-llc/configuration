package ru.i_novus.configuration.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.i_novus.config.api.util.AuditService;

@Component
@Slf4j
public class SimpleAuditService implements AuditService {
    @Override
    public void audit(String action, Object object, String objectId, String objectName) {
        log.info(String.format("Действие '%s' было произведено над объектом '%s' с идентификатором '%s'", action, objectName, objectId));
    }
}
