package ru.i_novus.configuration.config.mapper;

import ru.i_novus.config.api.model.ConfigForm;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.api.model.GroupedApplicationConfig;

import java.util.List;

public class GroupedApplicationConfigMapper {

    public static GroupedApplicationConfig toGroupedApplicationConfig(GroupForm groupForm, List<ConfigForm> configFormList) {
        GroupedApplicationConfig groupedApplicationConfig = new GroupedApplicationConfig();
        groupedApplicationConfig.setId(groupForm.getId());
        groupedApplicationConfig.setName(groupForm.getName());
        groupedApplicationConfig.setConfigs(configFormList);
        return groupedApplicationConfig;
    }
}
