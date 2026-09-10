package ru.i_novus.config.web;

import net.n2oapp.criteria.dataset.DataList;
import net.n2oapp.criteria.dataset.DataSet;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;

import java.util.List;

public class ApplicationConfigUtil {

    private static final String ID = "id";
    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final String VALUE_TYPE = "valueType";
    private static final String VALUE = "value";
    private static final String IS_CONFIG = "isConfig";
    private static final String CHILDREN = "children";
    private static final String CONFIGS = "configs";
    private static final String CONFIG_CODE = "configCode";
    private static final String COMMON_SYSTEM_VALUE = "commonSystemValue";
    private static final String DEFAULT_VALUE = "defaultValue";

    public static <T extends List> T normalizeCommonSystemConfig(T children) {
        if (children == null || children.isEmpty())
            return null;
        for (Object item : children) {
            ((DataSet) item).put(ID, ((DataSet) item).get(CODE));
            ((DataSet) item).put(NAME, ((DataSet) item).get(NAME));
            ((DataSet) item).put(VALUE_TYPE, ((DataSet) item).get(VALUE_TYPE));

            if (ValueTypeEnum.BOOLEAN.name().equals(((DataSet) item).get(VALUE_TYPE))) {
                boolean result = "true".equals(((DataSet) item).get(VALUE));
                ((DataSet) item).put(VALUE, result);
            } else {
                ((DataSet) item).put(VALUE, ((DataSet) item).get(VALUE));
            }

            ((DataSet) item).put(IS_CONFIG, true);
        }
        return children;
    }

    public static <T extends List> T normalizeApplicationConfig(T children, String appCode) {
        if (children == null || children.isEmpty())
            return null;
        for (Object item : children) {
            ((DataSet) item).put(ID, appCode + "__" + ((DataSet) item).get(ID));
            ((DataSet) item).put(NAME, ((DataSet) item).get(NAME));
            ((DataSet) item).put(IS_CONFIG, false);
            ((DataSet) item).put(CHILDREN, new DataList());
            for (Object config : ((DataList) ((DataSet) item).get(CONFIGS))) {
                DataSet configDataset = new DataSet();
                configDataset.put(ID, ((DataSet) config).get(CODE));
                configDataset.put(CONFIG_CODE, ((DataSet) config).get(CODE));
                configDataset.put(NAME, ((DataSet) config).get(NAME));
                configDataset.put(COMMON_SYSTEM_VALUE, ((DataSet) config).get(COMMON_SYSTEM_VALUE));
                configDataset.put(VALUE_TYPE, ((DataSet) config).get(VALUE_TYPE));
                configDataset.put(DEFAULT_VALUE, ((DataSet) config).get(DEFAULT_VALUE));

                if (ValueTypeEnum.BOOLEAN.getName().equals(((DataSet) config).get(VALUE_TYPE))) {
                    boolean result = "true".equals(((DataSet) config).get(VALUE));
                    configDataset.put(VALUE, result);
                } else {
                    configDataset.put(VALUE, ((DataSet) config).get(VALUE));
                }

                configDataset.put(IS_CONFIG, true);
                ((DataList) ((DataSet) item).get(CHILDREN)).add(configDataset);
            }
            ((DataSet) item).remove(CONFIGS);
        }
        return children;
    }
}
