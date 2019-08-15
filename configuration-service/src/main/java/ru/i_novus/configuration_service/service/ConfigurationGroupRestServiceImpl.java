package ru.i_novus.configuration_service.service;

import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import ru.i_novus.configuration_api.criteria.FindGroupCriteria;
import ru.i_novus.configuration_api.items.GroupResponseItem;
import ru.i_novus.configuration_api.service.ConfigurationGroupRestService;
import ru.i_novus.configuration_service.entity.GroupCodeEntity;
import ru.i_novus.configuration_service.entity.GroupEntity;
import ru.i_novus.configuration_service.entity.QGroupCodeEntity;
import ru.i_novus.configuration_service.entity.QGroupEntity;
import ru.i_novus.configuration_service.repository.GroupCodeRepository;
import ru.i_novus.configuration_service.repository.GroupRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Реализация REST сервиса для работы с группами настроек
 */
@Controller
public class ConfigurationGroupRestServiceImpl implements ConfigurationGroupRestService {

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
    public GroupResponseItem getGroup(Integer groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Группы с идентификатором " + groupId + " не существует"));

        List<String> codes = groupCodeRepository.findAllCodeByGroupId(groupId);
        return groupEntity.toItem(codes);
    }

    @Override
    public Page<GroupResponseItem> getAllGroup(FindGroupCriteria criteria) {
        return new PageImpl<>(findGroups(criteria), criteria, criteria.getPageSize());
    }

    @Override
    @Transactional
    public Integer saveGroup(@Valid @NotNull GroupResponseItem groupResponseItem) {
        GroupEntity groupEntity = new GroupEntity(groupResponseItem);

        if (groupCodeRepository.existsAtLeastOneCode(groupResponseItem.getCodes(), -1)) {
            throw new BadRequestException("Один или более кодов принадлежат другой группе");
        }

        final GroupEntity savedGroupEntity;
        try {
            savedGroupEntity = groupRepository.save(groupEntity);
        } catch (Exception e) {
            throw new BadRequestException("Группа настроек с именем " + groupResponseItem.getName() + " уже существует");
        }

        groupCodeRepository.saveAll(getGroupCodeEntities(
                groupResponseItem.getCodes(), savedGroupEntity.getId()));
        return savedGroupEntity.getId();
    }

    @Override
    @Transactional
    public void updateGroup(Integer groupId, @Valid @NotNull GroupResponseItem groupResponseItem) {
        GroupEntity groupEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Группы с идентификатором " + groupId + " не существует"));

        if (groupCodeRepository.existsAtLeastOneCode(groupResponseItem.getCodes(), groupEntity.getId())) {
            throw new BadRequestException("Один или более кодов принадлежат другой группе");
        }

        groupEntity.setName(groupResponseItem.getName());
        groupEntity.setDescription(groupResponseItem.getDescription());

        groupRepository.save(groupEntity);

        List<String> codes = groupCodeRepository.findAllCodeByGroupId(groupId);
        if (!groupResponseItem.getCodes().equals(codes)) {
            groupCodeRepository.deleteByGroupId(groupId);
            groupCodeRepository.saveAll(getGroupCodeEntities(groupResponseItem.getCodes(), groupId));
        }
    }

    @Override
    public void deleteGroup(Integer groupId) {
        if (groupRepository.removeById(groupId) == 0) {
            throw new NotFoundException("Группы с идентификатором " + groupId + " не существует.");
        }
    }

    private List<GroupResponseItem> findGroups(FindGroupCriteria criteria) {
        QGroupEntity qGroupEntity = QGroupEntity.groupEntity;
        QGroupCodeEntity qGroupCodeEntity = QGroupCodeEntity.groupCodeEntity;

        JPAQuery<String> query = new JPAQuery(entityManager);
        query.select(qGroupEntity)
                .from(qGroupEntity).leftJoin(qGroupCodeEntity).on(qGroupEntity.id.eq(qGroupCodeEntity.groupId));

        if (criteria.getCode() != null) {
            query.where(qGroupCodeEntity.code.containsIgnoreCase(criteria.getCode()));
        }

        if (criteria.getName() != null) {
            query.where(qGroupEntity.name.containsIgnoreCase(criteria.getName()));
        }

//        select(qGroupEntity.name, qGroupEntity.description, SQLExpressions.groupConcat(qGroupCodeEntity.code, " "))
        /// TODO - есть вероятность, что из-за лимита\офсетта не все коды будут включены в запись группы
        /// TODO - пока так, но вероятно нужно использовать group_concat без использования внешнего преобразования
        List<String> results =
                query.groupBy(qGroupEntity.id).fetch();
//
//                        .limit(criteria.getPageSize()).offset(criteria.getOffset())
        return null;
    }

    /**
     * Конструирует экземпляры кодов конкретной группы
     *
     * @param codes   Коды групп
     * @param groupId Идентификатор группы
     * @return Список экземпляров кодов группы
     */
    private List<GroupCodeEntity> getGroupCodeEntities(List<String> codes, Integer groupId) {
        return codes.stream()
                .map(i -> new GroupCodeEntity(i, groupId))
                .collect(toList());
    }
}
