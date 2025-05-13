package ru.i_novus.configuration.config.validators.value;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static ru.i_novus.configuration.config.validators.value.ConfigValueValidatorConstants.INVALID_FORMAT_MSG;

/**
 * Базовая часть реализации валидатора даты и времени
 */
abstract class AbstractDateTimeValidator implements ConfigValueValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate(String value) {
        var pattern = isBlank(getPattern())
                ? DateTimeFormatter.ISO_DATE_TIME.toString()
                : getPattern();
        try {
            DateTimeFormatter.ofPattern(pattern, Locale.getDefault()).parse(value);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorMessageCode() {
        return INVALID_FORMAT_MSG;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getArgs(String value) {
        return new String[]{value, getPattern(), getType().getId()};
    }

}
