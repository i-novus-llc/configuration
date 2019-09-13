package ru.i_novus.config.service.mapper;

import ru.i_novus.config.api.model.ConfigRequest;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.api.model.GroupedConfigRequest;

import java.util.List;

public class GroupedConfigRequestMapper {

    public static GroupedConfigRequest toGroupedConfigRequest(GroupForm groupForm, List<ConfigRequest> configRequestList) {
        GroupedConfigRequest groupedConfigRequest = new GroupedConfigRequest();
        groupedConfigRequest.setId(groupForm.getId());
        groupedConfigRequest.setName(groupForm.getName());
        groupedConfigRequest.setDescription(groupForm.getDescription());
        groupedConfigRequest.setPriority(groupForm.getPriority());
        groupedConfigRequest.setConfigs(configRequestList);
        return groupedConfigRequest;
    }
}
