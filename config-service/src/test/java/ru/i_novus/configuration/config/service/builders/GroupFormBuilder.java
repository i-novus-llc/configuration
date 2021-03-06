package ru.i_novus.configuration.config.service.builders;

import ru.i_novus.config.api.model.GroupForm;

import java.util.Set;

public class GroupFormBuilder {

    public static GroupForm buildGroupForm1() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("Authentication settings");
        groupForm.setCodes(Set.of("auth"));
        return groupForm;
    }

    public static GroupForm buildGroupForm2() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("Security settings");
        groupForm.setCodes(Set.of("sec1", "sec2"));
        return groupForm;
    }

    public static GroupForm buildGroupForm3() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("Base security settings");
        groupForm.setCodes(Set.of("base-sec"));
        return groupForm;
    }

    public static GroupForm buildTestGroupForm() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("test");
        groupForm.setCodes(Set.of("test1", "test2", "test3"));
        return groupForm;
    }
}
