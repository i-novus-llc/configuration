package ru.i_novus.config.web.provider;

import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.GroupedApplicationConfig;
import ru.i_novus.config.api.model.ValueTypeEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupedApplicationConfigBuilder {

    public static List<GroupedApplicationConfig> build() {
        ConfigForm configForm1 = new ConfigForm();
        configForm1.setCode("test.code1");
        configForm1.setName("name1");
        configForm1.setDescription("desc1");
        configForm1.setValueType(ValueTypeEnum.STRING);
        configForm1.setValue("text");
        configForm1.setDefaultValue("default");
        ConfigForm configForm2 = new ConfigForm();
        configForm2.setCode("test.code2");
        configForm2.setName("name2");
        configForm2.setDescription("desc2");
        configForm2.setValueType(ValueTypeEnum.NUMBER);
        configForm2.setValue("123");
        ConfigForm configForm3 = new ConfigForm();
        configForm3.setCode("test.code3");
        configForm3.setName("name3");
        configForm3.setDescription("desc3");
        configForm3.setValueType(ValueTypeEnum.BOOLEAN);
        configForm3.setValue("true");
        ConfigForm configForm4 = new ConfigForm();
        configForm4.setCode("test.code4");
        configForm4.setName("name4");
        configForm4.setDescription("desc4");
        configForm4.setValueType(ValueTypeEnum.BOOLEAN);

        GroupedApplicationConfig groupedApplicationConfig1 = new GroupedApplicationConfig();
        groupedApplicationConfig1.setId(1);
        groupedApplicationConfig1.setName("group1");
        groupedApplicationConfig1.setConfigs(Arrays.asList(configForm1, configForm2));
        GroupedApplicationConfig groupedApplicationConfig2 = new GroupedApplicationConfig();
        groupedApplicationConfig2.setId(2);
        groupedApplicationConfig2.setName("group2");
        groupedApplicationConfig2.setConfigs(Arrays.asList(configForm3, configForm4));

        List<GroupedApplicationConfig> groupedApplicationConfigs = new ArrayList<>();
        groupedApplicationConfigs.add(groupedApplicationConfig1);
        groupedApplicationConfigs.add(groupedApplicationConfig2);

        return groupedApplicationConfigs;
    }
}
