package ru.i_novus.configuration.config.validators.value;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;

/**
 * Реализация валидатора значений настроек, типа {@link ValueTypeEnum#NUMBER NUMBER}
 */
@Component
@ConditionalOnProperty(name = "config.value.validate.enabled", havingValue = "true")
public class NumberConfigValueValidatorImpl implements ConfigValueValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueTypeEnum getType() {
        return ValueTypeEnum.NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(String value) {
        return NumberUtils.isCreatable(value);
    }

}
