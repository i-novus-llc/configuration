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
        configForm.setGroup(buildGroupForm1());
        configForm.setSystemName("application");
        return configForm;
    }

    public static ConfigForm buildConfigForm2() {
        ConfigForm configForm = new ConfigForm();
        configForm.setName("name 2");
        configForm.setCode("sec2.spring.sec-token");
        configForm.setValueType("Строка");
        configForm.setValue("test-value");
        configForm.setGroup(buildGroupForm1());
        configForm.setSystemName("application");
        return configForm;
    }

    public static ConfigForm buildConfigForm3() {
        ConfigForm configForm = new ConfigForm();
        configForm.setName("something");
        configForm.setCode("auth.config");
        configForm.setValueType("Строка");
        configForm.setValue("test-value");
        configForm.setGroup(buildGroupForm2());
        configForm.setSystemName("application");
        return configForm;
    }

    public static GroupForm buildGroupForm1() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("Security settings");
        groupForm.setCodes(Arrays.asList("sec", "sec2"));
        return groupForm;
    }

    public static GroupForm buildGroupForm2() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("Authentication settings");
        groupForm.setCodes(Arrays.asList("auth"));
        return groupForm;
    }
}
