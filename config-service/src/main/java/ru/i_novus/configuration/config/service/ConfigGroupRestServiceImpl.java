package ru.i_novus.configuration.config.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import net.n2oapp.platform.i18n.UserException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.criteria.GroupCriteria;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.api.model.enums.EventTypeEnum;
import ru.i_novus.config.api.model.enums.ObjectTypeEnum;
import ru.i_novus.config.api.service.ConfigGroupRestService;
import ru.i_novus.configuration.config.utils.LogUtils;
import ru.i_novus.configuration.config.entity.GroupEntity;
import ru.i_novus.configuration.config.mapper.GroupMapper;
import ru.i_novus.configuration.config.repository.GroupCodeRepository;
import ru.i_novus.configuration.config.repository.GroupRepository;
import ru.i_novus.configuration.config.specification.ConfigGroupSpecification;

/**
 * Реализация REST сервиса для работы с группами настроек
 */
@Service
@RequiredArgsConstructor
public class ConfigGroupRestServiceImpl implements ConfigGroupRestService {

    private final GroupRepository groupRepository;
    private final GroupCodeRepository groupCodeRepository;
    private final MessageSourceAccessor messageAccessor;

    @Override
    @Transactional(readOnly = true)
    public GroupForm getGroup(Integer groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(NotFoundException::new);
        return GroupMapper.toGroupForm(groupEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupForm> getAllGroup(GroupCriteria criteria) {
        ConfigGroupSpecification specification = new ConfigGroupSpecification(criteria);
        return groupRepository.findAll(specification, criteria)
                .map(GroupMapper::toGroupForm);
    }

    @Override
    @Transactional
    public Integer saveGroup(@Valid @NotNull GroupForm groupForm) {
        GroupEntity groupEntity = GroupMapper.toGroupEntity(groupForm);

        if (groupCodeRepository.existsAtLeastOneCode(groupForm.getCodes(), -1))
            throw new UserException(messageAccessor.getMessage("config.group.codes.not.unique"));

        if (groupRepository.existsByName(groupForm.getName(), -1))
            throw new UserException(messageAccessor.getMessage("config.group.name.not.unique"));

        GroupEntity savedGroupEntity = groupRepository.save(groupEntity);
        groupCodeRepository.saveAll(groupEntity.getCodes());

        LogUtils.log(EventTypeEnum.CONFIG_GROUP_CREATE.getTitle(), String.valueOf(groupForm.getId()), ObjectTypeEnum.CONFIG_GROUP.getTitle());
        return savedGroupEntity.getId();
    }

    @Override
    @Transactional
    public void updateGroup(Integer groupId, @Valid @NotNull GroupForm groupForm) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(NotFoundException::new);

        if (groupCodeRepository.existsAtLeastOneCode(groupForm.getCodes(), groupEntity.getId()))
            throw new UserException(messageAccessor.getMessage("config.group.codes.not.unique"));

        if (groupRepository.existsByName(groupForm.getName(), groupEntity.getId()))
            throw new UserException(messageAccessor.getMessage("config.group.name.not.unique"));

        groupEntity.setName(groupForm.getName());
        groupEntity.setDescription(groupForm.getDescription());
        groupEntity.setPriority(groupForm.getPriority());

        if (groupForm.getCodes().size() != groupEntity.getCodes().size() ||
                !groupEntity.getCodes().stream().allMatch(e -> groupForm.getCodes().contains(e.getCode()))) {
            groupCodeRepository.deleteByGroupId(groupEntity.getId());
            groupEntity.getCodes().clear();
            groupForm.getCodes().forEach(groupEntity::setCode);
            groupCodeRepository.saveAll(groupEntity.getCodes());
        }

        LogUtils.log(EventTypeEnum.CONFIG_GROUP_UPDATE.getTitle(), String.valueOf(groupForm.getId()), ObjectTypeEnum.CONFIG_GROUP.getTitle());
        groupRepository.save(groupEntity);
    }

    @Override
    @Transactional
    public void deleteGroup(Integer groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(NotFoundException::new);
        LogUtils.log(EventTypeEnum.CONFIG_GROUP_DELETE.getTitle(), String.valueOf(groupId), ObjectTypeEnum.CONFIG_GROUP.getTitle());
        groupRepository.deleteById(groupEntity.getId());
    }
}
