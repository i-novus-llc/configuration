package ru.i_novus.config.service.loader.builders;

import ru.i_novus.config.api.model.GroupForm;

import java.util.Arrays;

public class GroupFormBuilder {

    public static GroupForm buildGroupForm1() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("group1");
        groupForm.setDescription("desc1");
        groupForm.setPriority(1);
        groupForm.setCodes(Arrays.asList("a", "b"));
        return groupForm;
    }

    public static GroupForm buildGroupForm2() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("group2");
        groupForm.setDescription("desc2");
        groupForm.setPriority(2);
        groupForm.setCodes(Arrays.asList("c", "d"));
        return groupForm;
    }

    public static GroupForm buildGroupForm2Updated() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("group2");
        groupForm.setDescription("desc2-new");
        groupForm.setPriority(20);
        groupForm.setCodes(Arrays.asList("c1", "d1"));
        return groupForm;
    }

    public static GroupForm buildGroupForm3() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("group3");
        groupForm.setDescription("desc3");
        groupForm.setPriority(3);
        groupForm.setCodes(Arrays.asList("e", "f"));
        return groupForm;
    }
}
