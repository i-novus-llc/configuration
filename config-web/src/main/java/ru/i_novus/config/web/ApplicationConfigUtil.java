package ru.i_novus.config.web;

import net.n2oapp.criteria.dataset.DataList;
import net.n2oapp.criteria.dataset.DataSet;

import java.util.List;

public class ApplicationConfigUtil {

    public static <T extends List> T normalizeCommonSystemConfig(T children) {
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

    public static <T extends List> T normalizeApplicationConfig(T children, String appCode) {
        if (children == null || children.isEmpty())
            return null;
        for (Object item : children) {
            ((DataSet) item).put("id", appCode + "__" + ((DataSet) item).get("id"));
            ((DataSet) item).put("name", ((DataSet) item).get("name"));
            ((DataSet) item).put("isConfig", false);
            ((DataSet) item).put("children", new DataList());
            for (Object config : ((DataList) ((DataSet) item).get("configs"))) {
                DataSet configDataset = new DataSet();
                configDataset.put("id", ((DataSet) config).get("code"));
                configDataset.put("configCode", ((DataSet) config).get("code"));
                configDataset.put("name", ((DataSet) config).get("name"));
                configDataset.put("value", ((DataSet) config).get("value"));
                configDataset.put("commonSystemValue", ((DataSet) config).get("commonSystemValue"));
                configDataset.put("isConfig", true);
                ((DataList) ((DataSet) item).get("children")).add(configDataset);
            }
            ((DataSet) item).remove("configs");
        }
        return children;
    }
}
