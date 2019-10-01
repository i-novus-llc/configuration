package ru.i_novus.config.service.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import net.n2oapp.platform.i18n.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.service.utils.AuditUtils;
import ru.i_novus.config.api.criteria.GroupCriteria;
import ru.i_novus.config.api.model.EventTypeEnum;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.api.model.ObjectTypeEnum;
import ru.i_novus.config.api.service.ConfigGroupRestService;
import ru.i_novus.config.service.entity.GroupEntity;
import ru.i_novus.config.service.entity.QGroupCodeEntity;
import ru.i_novus.config.service.entity.QGroupEntity;
import ru.i_novus.config.service.mapper.GroupMapper;
import ru.i_novus.config.service.repository.GroupCodeRepository;
import ru.i_novus.config.service.repository.GroupRepository;
import ru.i_novus.ms.audit.client.AuditClient;
import ru.i_novus.ms.audit.client.model.AuditClientRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация REST сервиса для работы с группами настроек
 */
@Service
public class ConfigGroupRestServiceImpl implements ConfigGroupRestService {

    private GroupRepository groupRepository;
    private GroupCodeRepository groupCodeRepository;

    private AuditClient auditClient;

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Autowired
    public void setGroupCodeRepository(GroupCodeRepository groupCodeRepository) {
        this.groupCodeRepository = groupCodeRepository;
    }

    @Autowired
    @Qualifier("simpleAuditClient")
    public void setAuditClient(AuditClient auditClient) {
        this.auditClient = auditClient;
    }


    @Override
    public GroupForm getGroup(Integer groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow();
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
        criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "id"));
        Page<GroupEntity> groupEntities = groupRepository.findAll(toPredicate(criteria), criteria);

        return groupEntities.map(GroupMapper::toGroupForm);
    }

    @Override
    @Transactional
    public Integer saveGroup(@Valid @NotNull GroupForm groupForm) {
        GroupEntity groupEntity = GroupMapper.toGroupEntity(groupForm);

        if (groupCodeRepository.existsAtLeastOneCode(groupForm.getCodes(), -1)) {
            throw new UserException("config.group.codes.not.unique");
        }

        if (groupRepository.existsByName(groupForm.getName(), -1)) {
            throw new UserException("config.group.name.not.unique");
        }

        groupForm.getCodes().forEach(groupEntity::setCode);

        GroupEntity savedGroupEntity = groupRepository.save(groupEntity);
        groupCodeRepository.saveAll(groupEntity.getCodes());
        audit(groupForm, EventTypeEnum.CONFIG_GROUP_CREATE);

        return savedGroupEntity.getId();
    }

    @Override
    @Transactional
    public void updateGroup(Integer groupId, @Valid @NotNull GroupForm groupForm) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow();

        if (groupCodeRepository.existsAtLeastOneCode(groupForm.getCodes(), groupEntity.getId())) {
            throw new UserException("config.group.codes.not.unique");
        }

        if (groupRepository.existsByName(groupForm.getName(), groupEntity.getId())) {
            throw new UserException("config.group.name.not.unique");
        }

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
    public void deleteGroup(Integer groupId) {
        groupRepository.deleteById(groupId);
        audit(getGroup(groupId), EventTypeEnum.CONFIG_GROUP_DELETE);
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
        AuditClientRequest request = AuditUtils.getAuditClientRequest();
        request.setEventType(eventType.getTitle());
        request.setObjectType(ObjectTypeEnum.CONFIG_GROUP.toString());
        request.setObjectId(String.valueOf(groupForm.getId()));
        request.setObjectName(ObjectTypeEnum.CONFIG_GROUP.getTitle());
        request.setContext(AuditUtils.getContext(groupForm));
        request.setAuditType((short) 1);
        auditClient.add(request);
    }
}
