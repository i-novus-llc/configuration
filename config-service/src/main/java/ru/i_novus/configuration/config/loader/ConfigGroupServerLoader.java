package ru.i_novus.configuration.config.loader;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.platform.loader.server.ServerLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.configuration.config.entity.GroupCodeEntity;
import ru.i_novus.configuration.config.entity.GroupEntity;
import ru.i_novus.configuration.config.mapper.GroupMapper;
import ru.i_novus.configuration.config.repository.GroupCodeRepository;
import ru.i_novus.configuration.config.repository.GroupRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ConfigGroupServerLoader implements ServerLoader<GroupForm> {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupCodeRepository groupCodeRepository;

    @Autowired
    private MessageSourceAccessor messageAccessor;

    @Override
    @Transactional
    public void load(List<GroupForm> list, String subject) {
        List<GroupForm> insertedGroupForms = new ArrayList<>(list);
        List<GroupEntity> updatedEntities = new ArrayList<>();
        Map<String, Integer> groupCodes = groupCodeRepository.findAll().stream()
                .collect(Collectors.toMap(GroupCodeEntity::getCode, gc -> gc.getGroup().getId()));
        Map<String, GroupCodeEntity> deletedGroupCodes = new HashMap<>();

        // для оптимизации поиска list преобразуем в map
        Map<String, GroupEntity> oldGroupEntities = groupRepository.findAll().stream()
                .collect(Collectors.toMap(GroupEntity::getName, Function.identity()));

        for (GroupForm newGroupForm : list) {
            GroupEntity groupEntity = oldGroupEntities.get(newGroupForm.getName());
            if (groupEntity == null)
                continue;

            newGroupForm.setId(groupEntity.getId());
            insertedGroupForms.remove(newGroupForm);

            if (GroupMapper.toGroupForm(groupEntity).equals(newGroupForm))
                continue;

            updateGroup(newGroupForm, groupEntity, groupCodes, deletedGroupCodes);
            updatedEntities.add(groupEntity);
        }

        // CREATE
        insertedGroupForms.forEach(groupForm -> registerNewGroupCodes(groupForm, groupCodes, deletedGroupCodes));

        List<GroupEntity> savedGroupEntities = insertedGroupForms.stream()
                .map(GroupMapper::toGroupEntity).collect(Collectors.toList());
        savedGroupEntities.addAll(updatedEntities);

        groupRepository.saveAll(savedGroupEntities);
        groupCodeRepository.deleteAll(deletedGroupCodes.values());
        groupCodeRepository.saveAll(savedGroupEntities.stream().flatMap(x -> x.getCodes().stream()).collect(Collectors.toList()));
    }

    private void updateGroup(GroupForm newGroupForm, GroupEntity groupEntity,
                              Map<String, Integer> groupCodes, Map<String, GroupCodeEntity> deletedGroupCodes) {
        validateCodesUnique(newGroupForm.getCodes(), groupCodes, groupEntity.getId());

        if (codesChanged(newGroupForm, groupEntity)) {
            replaceGroupCodes(newGroupForm, groupEntity, groupCodes, deletedGroupCodes);
        }
        groupEntity.setDescription(newGroupForm.getDescription());
        groupEntity.setPriority(newGroupForm.getPriority());
    }

    private void validateCodesUnique(Set<String> codes, Map<String, Integer> groupCodes, Integer ownerGroupId) {
        boolean hasConflict = codes.stream()
                .anyMatch(c -> groupCodes.containsKey(c) && !groupCodes.get(c).equals(ownerGroupId));
        if (hasConflict) {
            throw new UserException(messageAccessor.getMessage("config.group.codes.not.unique"));
        }
    }

    private boolean codesChanged(GroupForm newGroupForm, GroupEntity groupEntity) {
        return newGroupForm.getCodes().size() != groupEntity.getCodes().size()
                || !groupEntity.getCodes().stream().allMatch(gc -> newGroupForm.getCodes().contains(gc.getCode()));
    }

    private void replaceGroupCodes(GroupForm newGroupForm, GroupEntity groupEntity,
                                    Map<String, Integer> groupCodes, Map<String, GroupCodeEntity> deletedGroupCodes) {
        groupEntity.getCodes().forEach(gc -> groupCodes.remove(gc.getCode()));
        newGroupForm.getCodes().forEach(c -> groupCodes.put(c, groupEntity.getId()));
        groupEntity.getCodes().forEach(gc -> deletedGroupCodes.put(gc.getCode(), gc));
        groupEntity.getCodes().clear();
        newGroupForm.getCodes().forEach(groupEntity::setCode);
        groupEntity.getCodes().forEach(gc -> deletedGroupCodes.remove(gc.getCode()));
    }

    private void registerNewGroupCodes(GroupForm groupForm, Map<String, Integer> groupCodes,
                                        Map<String, GroupCodeEntity> deletedGroupCodes) {
        if (groupForm.getCodes().stream().anyMatch(groupCodes::containsKey)) {
            throw new UserException(messageAccessor.getMessage("config.group.codes.not.unique"));
        }
        groupForm.getCodes().forEach(c -> {
            deletedGroupCodes.remove(c);
            groupCodes.put(c, 0);
        });
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
