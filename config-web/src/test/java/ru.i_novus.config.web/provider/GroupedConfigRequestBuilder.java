package ru.i_novus.config.web.provider;

import ru.i_novus.config.api.model.ConfigRequest;
import ru.i_novus.config.api.model.GroupedConfigRequest;
import ru.i_novus.config.api.model.ValueTypeEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupedConfigRequestBuilder {

    public static List<GroupedConfigRequest> build() {
        ConfigRequest configRequest1 = new ConfigRequest();
        configRequest1.setCode("data.code1");
        configRequest1.setName("name1");
        configRequest1.setDescription("desc1");
        configRequest1.setValueType(ValueTypeEnum.STRING);
        configRequest1.setValue("text");
        ConfigRequest configRequest2 = new ConfigRequest();
        configRequest2.setCode("data.code2");
        configRequest2.setName("name2");
        configRequest2.setDescription("desc2");
        configRequest2.setValueType(ValueTypeEnum.NUMBER);
        configRequest2.setValue("123");
        ConfigRequest configRequest3 = new ConfigRequest();
        configRequest3.setCode("data.code3");
        configRequest3.setName("name3");
        configRequest3.setDescription("desc3");
        configRequest3.setValueType(ValueTypeEnum.BOOLEAN);
        configRequest3.setValue("true");

        GroupedConfigRequest groupedConfigRequest1 = new GroupedConfigRequest();
        groupedConfigRequest1.setId(1);
        groupedConfigRequest1.setName("group1");
        groupedConfigRequest1.setConfigs(Arrays.asList(configRequest1, configRequest2));
        GroupedConfigRequest groupedConfigRequest2 = new GroupedConfigRequest();
        groupedConfigRequest2.setId(2);
        groupedConfigRequest2.setName("group2");
        groupedConfigRequest2.setConfigs(Arrays.asList(configRequest3));

        List<GroupedConfigRequest> groupedConfigRequests = new ArrayList<>();
        groupedConfigRequests.add(groupedConfigRequest1);
        groupedConfigRequests.add(groupedConfigRequest2);

        return groupedConfigRequests;
    }
}
