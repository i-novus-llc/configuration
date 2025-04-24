package ru.i_novus.configuration.config.validators.value;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;

/**
 * Реализация валидатора значений настроек, типа {@link ValueTypeEnum#BOOLEAN BOOLEAN}
 */
@Component
@ConditionalOnProperty(name = "config.value.validate.enabled", havingValue = "true")
public class BooleanConfigValueValidatorImpl implements ConfigValueValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueTypeEnum getType() {
        return ValueTypeEnum.BOOLEAN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(String value) {
        return Boolean.TRUE.toString().equalsIgnoreCase(value)
                || Boolean.FALSE.toString().equalsIgnoreCase(value);
    }

}
