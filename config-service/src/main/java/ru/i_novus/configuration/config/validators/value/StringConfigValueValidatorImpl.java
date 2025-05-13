package ru.i_novus.configuration.config.validators.value;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;

/**
 * Реализация валидатора значений настроек, типа {@link ValueTypeEnum#STRING STRING}
 */
@Component
@ConditionalOnProperty(name = "config.value.validate.enabled", havingValue = "true")
public class StringConfigValueValidatorImpl implements ConfigValueValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueTypeEnum getType() {
        return ValueTypeEnum.STRING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(String value) {
        return true;
    }

}
