package ru.i_novus.configuration.config.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import net.n2oapp.platform.i18n.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.criteria.GroupCriteria;
import ru.i_novus.config.api.model.enums.EventTypeEnum;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.api.model.enums.ObjectTypeEnum;
import ru.i_novus.config.api.service.ConfigGroupRestService;
import ru.i_novus.config.api.util.AuditService;
import ru.i_novus.configuration.config.entity.GroupEntity;
import ru.i_novus.configuration.config.entity.QGroupCodeEntity;
import ru.i_novus.configuration.config.entity.QGroupEntity;
import ru.i_novus.configuration.config.mapper.GroupMapper;
import ru.i_novus.configuration.config.repository.GroupCodeRepository;
import ru.i_novus.configuration.config.repository.GroupRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация REST сервиса для работы с группами настроек
 */
@Service
public class ConfigGroupRestServiceImpl implements ConfigGroupRestService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupCodeRepository groupCodeRepository;
    @Autowired
    private AuditService auditService;
    @Autowired
    private MessageSourceAccessor messageAccessor;


    @Override
    public GroupForm getGroup(Integer groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(NotFoundException::new);
        return GroupMapper.toGroupForm(groupEntity);
    }

    @Override
    public List<GroupForm> getGroupByConfigCode(String code) {
        return groupRepository.findGroupsByConfigCode(code).stream()
                .map(GroupMapper::toGroupForm)
                .collect(Collectors.toList());
    }

    @Override
    public Page<GroupForm> getAllGroup(GroupCriteria criteria) {
        Predicate predicate = toPredicate(criteria);
        Page<GroupEntity> groupEntities = groupRepository.findAll(predicate == null ? new BooleanBuilder() : predicate, criteria);
        return groupEntities.map(GroupMapper::toGroupForm);
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
        audit(groupForm, EventTypeEnum.CONFIG_GROUP_CREATE);

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

        groupRepository.save(groupEntity);
        audit(groupForm, EventTypeEnum.CONFIG_GROUP_UPDATE);
    }

    @Override
    @Transactional
    public void deleteGroup(Integer groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(NotFoundException::new);
        groupRepository.deleteById(groupEntity.getId());
        audit(GroupMapper.toGroupForm(groupEntity), EventTypeEnum.CONFIG_GROUP_DELETE);
    }

    private Predicate toPredicate(GroupCriteria criteria) {
        QGroupEntity qGroupEntity = QGroupEntity.groupEntity;
        QGroupCodeEntity qGroupCodeEntity = QGroupCodeEntity.groupCodeEntity;
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.getCode() != null) {
            BooleanExpression exists = JPAExpressions.selectOne().from(qGroupCodeEntity)
                    .where(new BooleanBuilder()
                            .and(qGroupCodeEntity.group.id.eq(qGroupEntity.id))
                            .and(qGroupCodeEntity.code.containsIgnoreCase(criteria.getCode())))
                    .exists();
            builder.and(exists);
        }

        if (criteria.getName() != null) {
            builder.and(qGroupEntity.name.containsIgnoreCase(criteria.getName()));
        }

        return builder.getValue();
    }

    private void audit(GroupForm groupForm, EventTypeEnum eventType) {
        auditService.audit(eventType.getTitle(), groupForm, String.valueOf(groupForm.getId()), ObjectTypeEnum.CONFIG_GROUP.getTitle());
    }
}
