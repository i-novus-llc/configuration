package ru.i_novus.configuration.config.loader.builders;

import ru.i_novus.config.api.model.GroupForm;

import java.util.Set;

public class LoaderGroupBuilder {

    public static GroupForm buildGroup1() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("group1");
        groupForm.setDescription("desc1");
        groupForm.setPriority(1);
        groupForm.setCodes(Set.of("a", "b"));
        return groupForm;
    }

    public static GroupForm buildGroup2() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("group2");
        groupForm.setDescription("desc2");
        groupForm.setPriority(2);
        groupForm.setCodes(Set.of("c", "d"));
        return groupForm;
    }

    public static GroupForm buildGroup2Updated() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("group2");
        groupForm.setDescription("desc2-new");
        groupForm.setPriority(20);
        groupForm.setCodes(Set.of("c1", "d1"));
        return groupForm;
    }

    public static GroupForm buildGroup3() {
        GroupForm groupForm = new GroupForm();
        groupForm.setName("group3");
        groupForm.setDescription("desc3");
        groupForm.setPriority(3);
        groupForm.setCodes(Set.of("e", "f"));
        return groupForm;
    }
}
