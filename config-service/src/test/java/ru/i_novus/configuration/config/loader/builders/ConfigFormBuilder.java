package ru.i_novus.configuration.config.loader.builders;

import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.ValueTypeEnum;

public class ConfigFormBuilder {

    public static ConfigForm buildConfigForm1() {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("code1");
        configForm.setName("name1");
        configForm.setDescription("desc1");
        configForm.setValueType(ValueTypeEnum.STRING);
        return configForm;
    }

    public static ConfigForm buildConfigForm2() {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("code2");
        configForm.setName("name2");
        configForm.setDescription("desc2");
        configForm.setValueType(ValueTypeEnum.NUMBER);
        return configForm;
    }

    public static ConfigForm buildConfigForm2Updated() {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("code2");
        configForm.setName("name2-new");
        configForm.setDescription("desc2-new");
        configForm.setValueType(ValueTypeEnum.STRING);
        return configForm;
    }

    public static ConfigForm buildConfigForm3() {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("code3");
        configForm.setName("name3");
        configForm.setDescription("desc3");
        configForm.setValueType(ValueTypeEnum.STRING);
        return configForm;
    }

    public static ConfigForm buildConfigForm4() {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("code4");
        configForm.setName("name4");
        configForm.setDescription("desc4");
        configForm.setValueType(ValueTypeEnum.STRING);
        return configForm;
    }
}
