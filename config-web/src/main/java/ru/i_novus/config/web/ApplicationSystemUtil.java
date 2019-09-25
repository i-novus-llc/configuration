package ru.i_novus.config.web;

import net.n2oapp.criteria.dataset.DataSet;

import java.util.List;

public class ApplicationSystemUtil {

    public static <T extends List> T normalize(T children) {
        if (children == null || children.isEmpty())
            return null;
        for (Object item : children) {
            ((DataSet)item).put("id", ((DataSet)item).get("code"));
            ((DataSet)item).put("codeStr", ((DataSet)item).get("code"));
        }
        return children;
    }
}
