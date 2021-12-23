package ru.i_novus.config.web;

import net.n2oapp.criteria.dataset.DataSet;

import java.util.List;

public class ApplicationConfigUtil {

    public static <T extends List> T normalizeCommonSystem(T children) {
        if (children == null || children.isEmpty())
            return null;
        for (Object item : children) {
            ((DataSet) item).put("id", ((DataSet) item).get("code"));
            ((DataSet) item).put("name", ((DataSet) item).get("name"));
            ((DataSet) item).put("value", ((DataSet) item).get("value"));
            ((DataSet) item).put("isConfig", true);
        }
        return children;
    }
}
