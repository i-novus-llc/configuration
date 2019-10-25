package ru.i_novus.config.service.mapper;

import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.service.entity.GroupCodeEntity;
import ru.i_novus.config.service.entity.GroupEntity;

import java.util.stream.Collectors;

public class GroupMapper {

    public static GroupEntity toGroupEntity(GroupForm groupForm) {
        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setName(groupForm.getName());
        groupEntity.setDescription(groupForm.getDescription());
        groupEntity.setPriority(groupForm.getPriority());
        groupForm.getCodes().forEach(groupEntity::setCode);
        return groupEntity;
    }

    public static GroupForm toGroupForm(GroupEntity groupEntity) {
        GroupForm groupForm = new GroupForm();
        groupForm.setId(groupEntity.getId());
        groupForm.setName(groupEntity.getName());
        groupForm.setDescription(groupEntity.getDescription());
        groupForm.setPriority(groupEntity.getPriority());
        groupForm.setCodes(
                groupEntity.getCodes().stream()
                        .map(GroupCodeEntity::getCode)
                        .collect(Collectors.toList())
        );
        return groupForm;
    }
}
