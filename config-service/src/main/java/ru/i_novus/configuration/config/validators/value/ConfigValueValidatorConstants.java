package ru.i_novus.configuration.config.validators.value;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Константы аалидатора значений параметров
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigValueValidatorConstants {

    public static final String NOT_FOUND_VALIDATOR_MSG = "config.value.validation.not.found.validator";
    public static final String IS_BLANK_MSG = "config.value.validation.is.blank";
    public static final String INVALID_TYPE_MSG = "config.value.validation.invalid.type";
    public static final String INVALID_FORMAT_MSG = "config.value.validation.invalid.format";

}
