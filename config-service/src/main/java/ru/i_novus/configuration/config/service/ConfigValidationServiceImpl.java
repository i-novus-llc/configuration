package ru.i_novus.configuration.config.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import ru.i_novus.config.api.exception.ConfigValidationUserException;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;
import ru.i_novus.config.api.service.ConfigValidationService;
import ru.i_novus.configuration.config.validators.value.ConfigValueValidator;

import java.util.Map;

import static java.util.Objects.nonNull;
import static ru.i_novus.configuration.config.validators.value.ConfigValueValidatorConstants.IS_BLANK_MSG;
import static ru.i_novus.configuration.config.validators.value.ConfigValueValidatorConstants.NOT_FOUND_VALIDATOR_MSG;

/**
 * Реализация сервиса валидации значений настроек
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigValidationServiceImpl implements ConfigValidationService {

    private final MessageSourceAccessor messageAccessor;
    private final Map<ValueTypeEnum, ConfigValueValidator> configValueValidators;

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateConfigValue(String value, ValueTypeEnum valueType) {
        if (configValueValidators.isEmpty()) {
            return;
        }

        var validator = configValueValidators.get(valueType);
        if (nonNull(validator)) {
            var tmpValue = StringUtils.trimToNull(value);
            if (StringUtils.isBlank(tmpValue)) {
                throw new ConfigValidationUserException(IS_BLANK_MSG);
            }
            if (!validator.validate(tmpValue)) {
                throw new ConfigValidationUserException(validator.getErrorMessageCode(), validator.getArgs(value));
            }
        } else {
            log.warn(messageAccessor.getMessage(NOT_FOUND_VALIDATOR_MSG, new String[]{valueType.getId()}));
        }
    }

}
