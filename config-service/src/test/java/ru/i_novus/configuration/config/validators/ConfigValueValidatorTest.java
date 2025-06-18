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
import ru.i_novus.config.api.exception.ConfigValidationUserException;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;
import ru.i_novus.config.api.service.ConfigValidationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.i_novus.config.api.model.enums.ValueTypeEnum.*;

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
                Arguments.of(BOOLEAN, "\r\n true    "),
                Arguments.of(DATE, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))),
                Arguments.of(DATE, " " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))),
                Arguments.of(TIME, LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))),
                Arguments.of(TIME, LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " "),
                Arguments.of(DATETIME, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))),
                Arguments.of(DATETIME, " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) + " "));

    }

    /**
     * Проверка валидации некоренных значений настроек, приводящие к {@link BadRequestException BadRequestException}
     */
    @ParameterizedTest
    @MethodSource("argumentIncorrectValues")
    void testValidateIncorrectConfigValue(ValueTypeEnum valueType, String value) {
        assertThrows(ConfigValidationUserException.class, () -> configValidationService.validateConfigValue(value, valueType));
    }

    private static Stream<Arguments> argumentIncorrectValues() {
        return Stream.of(
                Arguments.of(STRING, StringUtils.SPACE),
                Arguments.of(STRING, StringUtils.EMPTY),
                Arguments.of(NUMBER, "text"),
                Arguments.of(NUMBER, "\\t 100"),
                Arguments.of(BOOLEAN, "text"),
                Arguments.of(BOOLEAN, "t"),
                Arguments.of(BOOLEAN, "true true"),
                Arguments.of(DATE, "text"),
                Arguments.of(DATE, "2025-03-331"),
                Arguments.of(DATE, "31-03-2025 14:45:00"),
                Arguments.of(TIME, "text"),
                Arguments.of(TIME, "31-03-2025"),
                Arguments.of(TIME, "14:50"),
                Arguments.of(DATETIME, "text"),
                Arguments.of(DATETIME, "14:45:00 31-03-2025"),
                Arguments.of(DATETIME, "31-03-2025"),
                Arguments.of(DATETIME, "31-03-2025"),
                Arguments.of(DATETIME, "14:45:00"));

    }

}
