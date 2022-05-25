package ru.i_novus.configuration.config.service.builders;

import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.enums.ValueTypeEnum;

public class ConfigFormBuilder {

    public static ConfigForm buildConfigForm1() {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("auth.config");
        configForm.setName("something");
        configForm.setValueType(ValueTypeEnum.STRING.getId());
        configForm.setValue("test-value");
        configForm.setApplicationCode("app-auth");
        return configForm;
    }

    public static ConfigForm buildConfigForm2() {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("sec1.url");
        configForm.setName("name");
        configForm.setValueType(ValueTypeEnum.STRING.getId());
        configForm.setValue("test-value");
        configForm.setApplicationCode("app-security");
        return configForm;
    }

    public static ConfigForm buildConfigForm3() {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("sec2.spring.sec-token");
        configForm.setName("name 2");
        configForm.setValueType(ValueTypeEnum.STRING.getId());
        configForm.setValue("test-value");
        configForm.setApplicationCode(null);
        return configForm;
    }

    public static ConfigForm buildTestConfigForm(int groupId) {
        ConfigForm configForm = new ConfigForm();
        configForm.setCode("sec1.test");
        configForm.setName("test");
        configForm.setValueType(ValueTypeEnum.STRING.getId());
        configForm.setValue("test-value");
        configForm.setApplicationCode("app-security");
        configForm.setGroupId(groupId);
        return configForm;
    }
}
