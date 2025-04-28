package ru.i_novus.configuration.config.validators.value;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;

/**
 * Реализация валидатора значений параметров типа {@link ValueTypeEnum#TIME TIME}
 */
@Component
@ConditionalOnProperty(name = "config.value.validate.enabled", havingValue = "true")
public class TimeConfigValueValidatorImpl extends AbstractDateTimeValidator {

    @Value("${config.value.validate.time.pattern:HH:mm:ss}")
    private String timePattern;

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueTypeEnum getType() {
        return ValueTypeEnum.TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPattern() {
        return timePattern;
    }

}
