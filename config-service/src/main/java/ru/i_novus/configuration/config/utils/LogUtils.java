package ru.i_novus.configuration.config.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class LogUtils {
    public static void log(String action, String objectId, String objectName) {
        log.info(String.format("Действие '%s' было произведено над объектом '%s' с идентификатором '%s'", action, objectName, objectId));
    }
}