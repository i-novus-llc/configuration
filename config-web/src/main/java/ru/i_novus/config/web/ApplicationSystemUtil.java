package ru.i_novus.config.web;

import java.util.List;

public class ApplicationSystemUtil {

    public static <T extends List> T get(T item) {
        return (item == null || item.isEmpty()) ? null : item;
    }
}
