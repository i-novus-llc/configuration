package ru.i_novus.config.service.loader;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.platform.loader.server.ServerLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.service.entity.GroupCodeEntity;
import ru.i_novus.config.service.entity.GroupEntity;
import ru.i_novus.config.service.mapper.GroupMapper;
import ru.i_novus.config.service.repository.GroupCodeRepository;
import ru.i_novus.config.service.repository.GroupRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ConfigGroupServerLoader implements ServerLoader<GroupForm> {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupCodeRepository groupCodeRepository;

    @Override
    @Transactional
    public void load(List<GroupForm> list, String subject) {
        List<GroupForm> insertedGroupForms = new ArrayList<>(list);
        List<GroupEntity> updatedEntities = new ArrayList<>();
        Map<String, Integer> groupCodes = groupCodeRepository.findAll().stream()
                .collect(Collectors.toMap(GroupCodeEntity::getCode, gc -> gc.getGroup().getId()));
        Map<String, GroupCodeEntity> deletedGroupCodes = new HashMap<>();

        // для оптимизации поиска list преобразуем в map
        Map<String, GroupEntity> oldGroupEntities = groupRepository.findAll().stream().collect(Collectors.toMap(GroupEntity::getName, Function.identity()));

        for (GroupForm newGroupForm : list) {
            GroupEntity groupEntity = oldGroupEntities.get(newGroupForm.getName());
            if (groupEntity != null) {
                GroupForm oldGroupForm = GroupMapper.toGroupForm(groupEntity);
                newGroupForm.setId(groupEntity.getId());

                if (!oldGroupForm.equals(newGroupForm)) {
                    // UPDATE
                    if (!newGroupForm.getCodes().stream()
                            .filter(c -> groupCodes.keySet().contains(c) && groupCodes.get(c) != groupEntity.getId())
                            .findAny().isEmpty()) {
                        throw new UserException("config.group.codes.not.unique");
                    }

                    if (newGroupForm.getCodes().size() != groupEntity.getCodes().size() ||
                            !groupEntity.getCodes().stream().allMatch(gc -> newGroupForm.getCodes().contains(gc.getCode()))) {
                        groupEntity.getCodes().stream().forEach(gc -> groupCodes.remove(gc.getCode()));
                        newGroupForm.getCodes().stream().forEach(c -> groupCodes.put(c, groupEntity.getId()));
                        groupEntity.getCodes().forEach(gc -> deletedGroupCodes.put(gc.getCode(), gc));
                        groupEntity.getCodes().clear();
                        newGroupForm.getCodes().forEach(groupEntity::setCode);
                        groupEntity.getCodes().forEach(gc -> deletedGroupCodes.remove(gc.getCode()));
                    }
                    groupEntity.setDescription(newGroupForm.getDescription());
                    groupEntity.setPriority(newGroupForm.getPriority());

                    insertedGroupForms.remove(newGroupForm);
                    updatedEntities.add(groupEntity);
                }
                insertedGroupForms.remove(newGroupForm);
            }
        }

        // CREATE
        for (GroupForm groupForm : insertedGroupForms) {
            if (groupForm.getCodes().stream().filter(groupCodes.keySet()::contains).findAny().isEmpty()) {
                groupForm.getCodes().stream().forEach(c -> {
                    if (deletedGroupCodes.containsKey(c)) deletedGroupCodes.remove(c);
                    groupCodes.put(c, 0);
                });
            } else {
                throw new UserException("config.group.codes.not.unique");
            }
        }

        List<GroupEntity> savedGroupEntities = insertedGroupForms.stream().map(GroupMapper::toGroupEntity).collect(Collectors.toList());
        savedGroupEntities.addAll(updatedEntities);

        groupRepository.saveAll(savedGroupEntities);
        groupCodeRepository.deleteAll(deletedGroupCodes.values());
        groupCodeRepository.saveAll(savedGroupEntities.stream().flatMap(x -> x.getCodes().stream()).collect(Collectors.toList()));
    }

    @Override
    public String getTarget() {
        return "groups";
    }

    @Override
    public Class<GroupForm> getDataType() {
        return GroupForm.class;
    }
}
