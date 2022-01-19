package ru.i_novus.configuration.config.specification;

import static org.springframework.util.StringUtils.hasText;

public class SpecificationUtils {

    public static String toLowerCaseLikeString(String str) {
        if (!hasText(str))
            return null;

        return "%" + str.trim().toLowerCase() + "%";
    }
}