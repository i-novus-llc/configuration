package ru.i_novus.config.service.service.builders;

import ru.i_novus.config.api.model.GroupForm;

import java.util.Arrays;

public class GroupFormBuilder {

    public static GroupForm buildGroupForm1() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("Security settings");
        groupForm.setCodes(Arrays.asList("sec", "security"));
        return groupForm;
    }

    public static GroupForm buildGroupForm2() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("Base security settings");
        groupForm.setCodes(Arrays.asList("base-sec"));
        return groupForm;
    }

    public static GroupForm buildGroupForm3() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("Authentication settings");
        groupForm.setCodes(Arrays.asList("auth"));
        return groupForm;
    }
}
