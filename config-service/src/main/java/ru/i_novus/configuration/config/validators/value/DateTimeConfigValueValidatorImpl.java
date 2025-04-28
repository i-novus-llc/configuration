package ru.i_novus.configuration.config.validators.value;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;

/**
 * Реализация валидатора значений параметров типа {@link ValueTypeEnum#DATETIME DATETIME}
 */
@Component
@ConditionalOnProperty(name = "config.value.validate.enabled", havingValue = "true")
public class DateTimeConfigValueValidatorImpl extends AbstractDateTimeValidator {

    @Value("${config.value.validate.datetime.pattern:yyyy-MM-dd'T'HH:mm:ss}")
    private String datetimePattern;

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueTypeEnum getType() {
        return ValueTypeEnum.DATETIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPattern() {
        return datetimePattern;
    }

}
