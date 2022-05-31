package ru.i_novus.configuration.config.loader.builders;

import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;

public class LoaderConfigBuilder {

    public static ConfigForm buildConfig1() {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("auth.code1");
        configForm.setName("name1");
        configForm.setDescription("desc1");
        configForm.setValueType(ValueTypeEnum.STRING.getId());
        return configForm;
    }

    public static ConfigForm buildConfig2() {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("auth.code2");
        configForm.setName("name2");
        configForm.setDescription("desc2");
        configForm.setValueType(ValueTypeEnum.NUMBER.getId());
        return configForm;
    }

    public static ConfigForm buildConfig2Updated() {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("auth.code2");
        configForm.setName("name2-new");
        configForm.setDescription("desc2-new");
        configForm.setValueType(ValueTypeEnum.STRING.getId());
        return configForm;
    }

    public static ConfigForm buildConfig3() {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("auth.code3");
        configForm.setName("name3");
        configForm.setDescription("desc3");
        configForm.setValueType(ValueTypeEnum.STRING.getId());
        return configForm;
    }

    public static ConfigForm buildConfig4() {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("auth.code4");
        configForm.setName("name4");
        configForm.setDescription("desc4");
        configForm.setValueType(ValueTypeEnum.STRING.getId());
        return configForm;
    }

    public static ConfigForm buildConfig5() {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("null.code5");
        configForm.setName("name5");
        configForm.setDescription("desc5");
        configForm.setValueType(ValueTypeEnum.STRING.getId());
        return configForm;
    }
}
