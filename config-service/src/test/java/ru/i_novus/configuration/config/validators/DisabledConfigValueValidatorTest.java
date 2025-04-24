package ru.i_novus.configuration.config.validators;

import net.n2oapp.platform.test.autoconfigure.pg.EnableTestcontainersPg;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.i_novus.TestApp;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;
import ru.i_novus.config.api.service.ConfigValidationService;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static ru.i_novus.config.api.model.enums.ValueTypeEnum.BOOLEAN;
import static ru.i_novus.config.api.model.enums.ValueTypeEnum.NUMBER;
import static ru.i_novus.config.api.model.enums.ValueTypeEnum.STRING;

/**
 * Тесты проверки валидатора значений настроек, если он отключён
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "config.value.validate.enabled=false")
@EnableTestcontainersPg
class DisabledConfigValueValidatorTest {

    @Autowired
    private ConfigValidationService configValidationService;

    /**
     * Проверка прохождения валидации значений настроек, если валидатор отключён
     */
    @ParameterizedTest
    @MethodSource("argumentIncorrectValues")
    void testDisabledValidatorConfigValue(ValueTypeEnum valueType, String value) {
        assertDoesNotThrow(() -> configValidationService.validateConfigValue(value, valueType));
    }

    private static Stream<Arguments> argumentIncorrectValues() {
        return Stream.of(
                Arguments.of(STRING, StringUtils.SPACE),
                Arguments.of(STRING, StringUtils.EMPTY),
                Arguments.of(NUMBER, "text"),
                Arguments.of(NUMBER, "\\t 100"),
                Arguments.of(BOOLEAN, "text"),
                Arguments.of(BOOLEAN, "t"),
                Arguments.of(BOOLEAN, "true true"));
    }

}
