package ru.i_novus.configuration.config.validators;

import jakarta.ws.rs.BadRequestException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.i_novus.config.api.model.enums.ValueTypeEnum.BOOLEAN;
import static ru.i_novus.config.api.model.enums.ValueTypeEnum.NUMBER;
import static ru.i_novus.config.api.model.enums.ValueTypeEnum.STRING;

/**
 * Тесты проверки валидаторов значений настроек
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "config.value.validate.enabled=true")
@EnableTestcontainersPg
class ConfigValueValidatorTest {

    @Autowired
    private ConfigValidationService configValidationService;

    /**
     * Проверка успешного прохождения валидации значений настроек
     */
    @ParameterizedTest
    @MethodSource("argumentValues")
    void testValidateConfigValue(ValueTypeEnum valueType, String value) {
        assertDoesNotThrow(() -> configValidationService.validateConfigValue(value, valueType));
    }

    private static Stream<Arguments> argumentValues() {
        return Stream.of(
                Arguments.of(STRING, "text"),
                Arguments.of(STRING, " text"),
                Arguments.of(STRING, "text "),
                Arguments.of(NUMBER, "10"),
                Arguments.of(NUMBER, "\t 100"),
                Arguments.of(NUMBER, "\n 100"),
                Arguments.of(BOOLEAN, "true"),
                Arguments.of(BOOLEAN, "false"),
                Arguments.of(BOOLEAN, "\r\n false"),
                Arguments.of(BOOLEAN, "\r\n true    "));
    }

    /**
     * Проверка валидации некоренных значений настроек, приводящие к {@link BadRequestException BadRequestException}
     */
    @ParameterizedTest
    @MethodSource("argumentIncorrectValues")
    void testValidateIncorrectConfigValue(ValueTypeEnum valueType, String value) {
        assertThrows(BadRequestException.class, () -> configValidationService.validateConfigValue(value, valueType));
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
