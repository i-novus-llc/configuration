package ru.i_novus.config.service.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import net.n2oapp.platform.i18n.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.criteria.GroupCriteria;
import ru.i_novus.config.api.model.GroupForm;
import ru.i_novus.config.api.service.ConfigGroupRestService;
import ru.i_novus.config.service.entity.GroupEntity;
import ru.i_novus.config.service.entity.QGroupCodeEntity;
import ru.i_novus.config.service.entity.QGroupEntity;
import ru.i_novus.config.service.repository.GroupCodeRepository;
import ru.i_novus.config.service.repository.GroupRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Autowired
    public void setGroupCodeRepository(GroupCodeRepository groupCodeRepository) {
        this.groupCodeRepository = groupCodeRepository;
    }


    @Override
    public GroupForm getGroup(Integer groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new UserException("Группы с идентификатором " + groupId + " не существует"));

        return groupEntity.toGroupForm();
    }

    @Override
    public Page<GroupForm> getAllGroup(GroupCriteria criteria) {
        Pageable pageable = PageRequest.of(criteria.getPageNumber(), criteria.getPageSize());
        Page<GroupEntity> groupEntities = groupRepository.findAll(toPredicate(criteria), pageable);

        List<GroupForm> groupForms = groupEntities.getContent().stream()
                .map(GroupEntity::toGroupForm).collect(Collectors.toList());
        return new PageImpl<>(groupForms, pageable, groupEntities.getTotalElements());
    }

    @Override
    @Transactional
    public Integer saveGroup(@Valid @NotNull GroupForm groupForm) {
        GroupEntity groupEntity = new GroupEntity(groupForm);

        if (groupCodeRepository.existsAtLeastOneCode(groupForm.getCodes(), -1)) {
            throw new UserException("Один или более кодов принадлежат другой группе");
        }

        groupForm.getCodes().forEach(groupEntity::setCode);

        final GroupEntity savedGroupEntity;
        try {
            savedGroupEntity = groupRepository.save(groupEntity);
        } catch (Exception e) {
            throw new UserException("Группа настроек с именем " + groupForm.getName() + " уже существует");
        }
        groupCodeRepository.saveAll(groupEntity.getCodes());

        return savedGroupEntity.getId();
    }

    @Override
    @Transactional
    public void updateGroup(Integer groupId, @Valid @NotNull GroupForm groupForm) {
        GroupEntity groupEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new UserException("Группы с идентификатором " + groupId + " не существует"));

        if (groupCodeRepository.existsAtLeastOneCode(groupForm.getCodes(), groupEntity.getId())) {
            throw new UserException("Один или более кодов принадлежат другой группе");
        }

        if (groupRepository.existsByName(groupForm.getName(), groupEntity.getId())) {
            throw new UserException("Имя " + groupForm.getName() + " уже используется другой группой");
        }

        groupEntity.setName(groupForm.getName());
        groupEntity.setDescription(groupForm.getDescription());

        if (groupForm.getCodes().size() != groupEntity.getCodes().size() ||
                !groupEntity.getCodes().stream().allMatch(e -> groupForm.getCodes().contains(e.getCode()))) {
            groupCodeRepository.deleteByGroupId(groupEntity.getId());
            groupEntity.getCodes().clear();
            groupForm.getCodes().forEach(groupEntity::setCode);
            groupCodeRepository.saveAll(groupEntity.getCodes());
        }

        groupRepository.save(groupEntity);
    }

    @Override
    public void deleteGroup(Integer groupId) {
        if (groupRepository.removeById(groupId) == 0) {
            throw new UserException("Группы с идентификатором " + groupId + " не существует.");
        }
    }

    private Predicate toPredicate(GroupCriteria criteria) {
        QGroupEntity qGroupEntity = QGroupEntity.groupEntity;
        QGroupCodeEntity qGroupCodeEntity = QGroupCodeEntity.groupCodeEntity;
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.getCode() != null) {
            BooleanExpression exists = JPAExpressions.selectOne().from(qGroupCodeEntity)
                    .where(new BooleanBuilder().and(qGroupCodeEntity.group.id.eq(qGroupEntity.id))
                            .and(qGroupCodeEntity.code.containsIgnoreCase(criteria.getCode()))).exists();
            builder.and(exists);
        }

        if (criteria.getName() != null) {
            builder.and(qGroupEntity.name.containsIgnoreCase(criteria.getName()));
        }

        return builder.getValue();
    }
}
