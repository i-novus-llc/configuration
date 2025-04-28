package ru.i_novus.configuration.config.validators.value;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;

/**
 * Реализация валидатора значений параметров типа {@link ValueTypeEnum#DATE DATE}
 */
@Component
@ConditionalOnProperty(name = "config.value.validate.enabled", havingValue = "true")
public class DateConfigValueValidatorImpl extends AbstractDateTimeValidator {

    @Value("${config.value.validate.date.pattern:yyyy-MM-dd}")
    private String datePattern;

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueTypeEnum getType() {
        return ValueTypeEnum.DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPattern() {
        return datePattern;
    }

}
