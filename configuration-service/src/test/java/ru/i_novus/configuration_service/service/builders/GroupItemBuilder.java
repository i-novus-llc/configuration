package ru.i_novus.configuration_service.service.builders;

import ru.i_novus.configuration_api.items.GroupResponseItem;

import java.util.Arrays;

public class GroupItemBuilder {

    public static GroupResponseItem buildGroupItem1() {
        GroupResponseItem groupResponseItem = new GroupResponseItem();
        groupResponseItem.setName("Security settings");
        groupResponseItem.setCodes(Arrays.asList("sec", "security"));
        return groupResponseItem;
    }

    public static GroupResponseItem buildGroupItem2() {
        GroupResponseItem groupResponseItem = new GroupResponseItem();
        groupResponseItem.setName("Base security settings");
        groupResponseItem.setCodes(Arrays.asList("base-sec"));
        return groupResponseItem;
    }

    public static GroupResponseItem buildGroupItem3() {
        GroupResponseItem groupResponseItem = new GroupResponseItem();
        groupResponseItem.setName("Authentication settings");
        groupResponseItem.setCodes(Arrays.asList("auth"));
        return groupResponseItem;
    }
}
