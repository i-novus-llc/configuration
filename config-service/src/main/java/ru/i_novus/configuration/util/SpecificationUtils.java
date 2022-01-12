package ru.i_novus.configuration.util;

import static org.springframework.util.StringUtils.hasText;

public class SpecificationUtils {

    public static String toLowerCaseString(String str) {
        if (!hasText(str))
            return null;

        return "%" + str.trim().toLowerCase() + "%";
    }
}