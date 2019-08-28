package ru.i_novus.config.service.service.builders;

import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.GroupForm;

import java.util.Arrays;

public class ConfigFormBuilder {

    public static ConfigForm buildConfigForm1() {
        ConfigForm configForm = new ConfigForm();
        configForm.setName("name");
        configForm.setCode("sec.url");
        configForm.setValueType("Строка");
        configForm.setValue("test-value");
        configForm.setGroup(GroupFormBuilder.buildGroupForm1());
        configForm.setApplicationCode("app-security");
        return configForm;
    }

    public static ConfigForm buildConfigForm2() {
        ConfigForm configForm = new ConfigForm();
        configForm.setName("name 2");
        configForm.setCode("sec2.spring.sec-token");
        configForm.setValueType("Строка");
        configForm.setValue("test-value");
        configForm.setGroup(GroupFormBuilder.buildGroupForm1());
        configForm.setApplicationCode("app-security");
        return configForm;
    }

    public static ConfigForm buildConfigForm3() {
        ConfigForm configForm = new ConfigForm();
        configForm.setName("something");
        configForm.setCode("auth.config");
        configForm.setValueType("Строка");
        configForm.setValue("test-value");
        configForm.setGroup(GroupFormBuilder.buildGroupForm2());
        configForm.setApplicationCode("app-auth");
        return configForm;
    }
}
