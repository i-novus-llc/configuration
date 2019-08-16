package ru.i_novus.config.service.service;

import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.config.api.criteria.FindGroupCriteria;
import ru.i_novus.config.api.items.GroupForm;
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
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация REST сервиса для работы с группами настроек
 */
@Controller
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
                .orElseThrow(() -> new NotFoundException("Группы с идентификатором " + groupId + " не существует"));

        return groupEntity.toGroupForm();
    }

    @Override
    public Page<GroupForm> getAllGroup(FindGroupCriteria criteria) {
        return new PageImpl<>(findGroups(criteria), criteria, criteria.getPageSize());
    }

    @Override
    @Transactional
    public Integer saveGroup(@Valid @NotNull GroupForm groupForm) {
        GroupEntity groupEntity = new GroupEntity(groupForm);

        if (groupCodeRepository.existsAtLeastOneCode(groupForm.getCodes(), -1)) {
            throw new BadRequestException("Один или более кодов принадлежат другой группе");
        }

        groupForm.getCodes().forEach(groupEntity::setCode);

        final GroupEntity savedGroupEntity;
        try {
            savedGroupEntity = groupRepository.save(groupEntity);
        } catch (Exception e) {
            throw new BadRequestException("Группа настроек с именем " + groupForm.getName() + " уже существует");
        }
        groupCodeRepository.saveAll(groupEntity.getCodes());

        return savedGroupEntity.getId();
    }

    @Override
    @Transactional
    public void updateGroup(Integer groupId, @Valid @NotNull GroupForm groupForm) {
        GroupEntity groupEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Группы с идентификатором " + groupId + " не существует"));

        if (groupCodeRepository.existsAtLeastOneCode(groupForm.getCodes(), groupEntity.getId())) {
            throw new BadRequestException("Один или более кодов принадлежат другой группе");
        }

        if (groupRepository.existsByName(groupForm.getName(), groupEntity.getId())) {
            throw new BadRequestException("Имя " + groupForm.getName() + " уже используется другой группой");
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
            throw new NotFoundException("Группы с идентификатором " + groupId + " не существует.");
        }
    }

    private List<GroupForm> findGroups(FindGroupCriteria criteria) {
        QGroupEntity qGroupEntity = QGroupEntity.groupEntity;
        QGroupCodeEntity qGroupCodeEntity = QGroupCodeEntity.groupCodeEntity;

        JPAQuery<GroupEntity> query = new JPAQuery<>(entityManager);
        query.distinct().from(qGroupEntity).innerJoin(qGroupCodeEntity).on(qGroupEntity.id.eq(qGroupCodeEntity.group.id));

        if (criteria.getCode() != null) {
            query.where(qGroupCodeEntity.code.containsIgnoreCase(criteria.getCode()));
        }

        if (criteria.getName() != null) {
            query.where(qGroupEntity.name.containsIgnoreCase(criteria.getName()));
        }

        List<GroupEntity> result = query.limit(criteria.getPageSize()).offset(criteria.getOffset()).fetch();

        return result.stream().map(GroupEntity::toGroupForm).collect(Collectors.toList());
    }
}
